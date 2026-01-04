package normalrelics.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import normalrelics.NormalRelics;
import normalrelics.annotations.Playable;
import normalrelics.annotations.Unplayable;
import normalrelics.relics.VoiceBox;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AbstractCardDynamicPatch {
    public static void forceValues(AbstractCard c) {
        if (c.equals(HaltCard.paired) || (HaltCard.halted != null && !c.equals(HaltCard.halted))) {
            c.damage = Math.max(0, HaltCard.cachedDamage);
            c.block = Math.max(0, HaltCard.cachedBlock);

            c.isDamageModified = true;
            c.isBlockModified = true;
        }
    }


    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class HaltCard {
        static AbstractCard halted = null, paired = null;

        static int cachedDamage = 0, cachedBlock = 0;

        @SpirePrefixPatch
        public static void reset(GameActionManager __instance) {
            if (__instance.cardQueue.isEmpty()) {
                halted = null;

                if (__instance.actions.isEmpty() && __instance.currentAction == null) {
                    paired = null;
                }
            }
        }

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<?> mustPairOrTurnOver(GameActionManager __instance, AbstractCard ___c) {
            if (AbstractDungeon.player.hasRelic(VoiceBox.ID) && (__instance.cardQueue.size() == 1 || paired == null)) {
                if (___c == null || ___c == paired) { //allow paired card to play.
                    return SpireReturn.Continue();
                }

                //Not null or paired, so halt it
                if (halted != ___c) {
                    halted = ___c;
                    cachedBlock = halted.cardID.equals(RitualDagger.ID) ? 0 : halted.block;
                    cachedDamage = halted.damage;
                    paired = null; //onto a new halted card, so clear out the paired card

                    AbstractDungeon.player.hand.applyPowers();
                }
                else {
                    cachedBlock = halted.cardID.equals(RitualDagger.ID) ? 0 : halted.block;
                    cachedDamage = halted.damage;
                }

                if (halted != null) {
                    halted.target_x = Settings.WIDTH / 2f;
                    halted.target_y = Settings.HEIGHT / 2f;
                }

                if (__instance.cardQueue.size() > 1) {
                    //not paired yet
                    CardQueueItem queued = __instance.cardQueue.get(1);
                    paired = queued.card; //Might be null, but that's fine
                }
                else if (AbstractDungeon.overlayMenu.endTurnButton.enabled) {
                    return SpireReturn.Return();
                }

                //Ending turn should allow current card to play due to queueing up a null card in card queue?
                //Test. Tbh probably better to have a failsafe anyways, use EndTurnButton disabled
            }

            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethod) throws Exception {
                return LineFinder.findInOrder(ctMethod,
                        new Matcher.FieldAccessMatcher(CardQueueItem.class, "randomTarget"));
            }
        }
    }


    public static void patch(ClassFinder finder, ClassPool pool) throws NotFoundException, CannotCompileException, BadBytecode {
        System.out.println("- Voice Box patch:");

        ClassFilter filter = new AndClassFilter(
                new NotClassFilter(new InterfaceOnlyClassFilter()),
                new SubclassClassFilter(AbstractCard.class),
                new DynamicPatchTrigger.GamePackageFilter() //avoids about 4000 classes
        );

        ArrayList<ClassInfo> clzList = new ArrayList<>();
        finder.findClasses(clzList, filter);

        CtClass abstractMonsterClass = pool.get(AbstractMonster.class.getName());

        System.out.println("\t- Potential targets found (" + clzList.size() + ").\n\t- Patching:");

        int skipped = 0, patched = 0;
        ForceValues editor = new ForceValues();

        Field modified = null;
        boolean alreadyModified;
        Collection<String> references;
        CtMethod[] methods;

        CtClass ctClass = pool.get(AbstractCard.class.getName());
        methods = ctClass.getDeclaredMethods();
        for (CtMethod m : methods)
        {
            editor.work(ctClass, m, abstractMonsterClass);
        }

        outer:
        for (ClassInfo classInfo : clzList)
        {
            try
            {
                ctClass = pool.get(classInfo.getClassName());
                editor.changed = false;

                references = ctClass.getRefClasses();

                for (String s : references) {
                    if (pool.getOrNull(s) == null)
                    {
                        System.out.println("\t\t- Class " + ctClass.getSimpleName() + " refers to an unloaded class, " + s + ", and will be skipped.");
                        continue outer;
                    }
                }

                alreadyModified = ctClass.isModified();

                methods = ctClass.getDeclaredMethods();

                for (CtMethod m : methods)
                {
                    editor.work(ctClass, m, abstractMonsterClass);
                }

                if (editor.changed)
                {
                    System.out.println("\t\t- Class patched: " + ctClass.getSimpleName());
                    ++patched;
                }
                else
                {
                    ++skipped;
                    if (!alreadyModified) {
                        try {
                            if (modified == null) {
                                modified = ctClass.getClass().getDeclaredField("wasChanged");
                                modified.setAccessible(true);
                            }
                            modified.set(ctClass, false);
                        }
                        catch (NoSuchFieldException | IllegalAccessException e) {
                            System.out.println("\t\t- Failed to mark class as unchanged: " + ctClass.getSimpleName());
                        }
                    }
                }
            }
            catch (NotFoundException e) {
                System.out.println("\t\t- Class not found: " + classInfo.getClassName());
                System.out.println(classInfo);
                e.printStackTrace();
            }
            catch (CannotCompileException | BadBytecode e) {
                System.out.println("\t\t- Error occurred while patching class: " + ctClass.getSimpleName() + "\n");
                e.printStackTrace();
            }
        }
        System.out.println("- Voice Box patch complete. " + (patched + skipped) + " classes checked. " + patched + " classes changed. " + skipped + " classes unchanged.");
    }

    private static class ForceValues {
        public boolean changed = false;

        private static final String forceValues =
                AbstractCardDynamicPatch.class.getName() + ".forceValues((" + AbstractCard.class.getName() + ") this);";

        public void work(CtClass clz, CtMethod method, CtClass abstractMonsterClass) throws CannotCompileException, NotFoundException, BadBytecode {
            if (!method.getReturnType().equals(CtClass.voidType)) return;

            String methodName = method.getName();

            if (methodName.equals("applyPowers")) {
                if (method.getParameterTypes().length == 0) {
                    method.insertAfter(forceValues);
                    changed = true;
                }
            }
            else if (methodName.equals("calculateCardDamage")) {
                CtClass[] params = method.getParameterTypes();
                if (params.length == 1 && params[0].equals(abstractMonsterClass)) {
                    method.insertAfter(forceValues);
                    changed = true;
                }
            }
            else if (methodName.equals("canUse")) {
                if (checkUnplayable(clz, method)) {
                    changed = true;
                }
            }
        }
    }




    //Specifically checks for classes that will always return false for canUse
    private static Set<String> testedClasses = new HashSet<>();

    private static final Set<Integer> returnOps = new HashSet<>();
    private static final int zeroInt = 3;
    private static final int intReturn = 0b10101100; //172
    static {
        returnOps.add(0b10101101); //long
        returnOps.add(0b10101110); //float
        returnOps.add(0b10101111); //double
        returnOps.add(0b10110000); //reference
        returnOps.add(0b10110001); //void
    }
    private static boolean checkUnplayable(CtClass cardClass, CtMethod canUse) throws BadBytecode {
        testedClasses.add(cardClass.getName());
        if (cardClass.getName().equals(AbstractCard.class.getName())) {
            return false;
        }
        if (canUse != null) {
            //Check if canUse method directly returns false
            System.out.println("\t\t- Class has canUse: " + cardClass.getSimpleName());
            StringBuilder opCode = new StringBuilder();
            CodeAttribute ca = canUse.getMethodInfo().getCodeAttribute();

            int lastOp = 0, index, op;
            CodeIterator ci = ca.iterator();
            boolean hardFalse = false, unplayable = true;
            while (ci.hasNext()) {
                index = ci.next();
                op = ci.byteAt(index);
                opCode.append(op).append(" ");

                Bytecode b = new Bytecode(canUse.getMethodInfo().getConstPool());
                b.add(Bytecode.DUP);

                if (returnOps.contains(op)) {
                    //non-int return op. This has a possibly non-false return.
                    //System.out.println("\t\t\t- " + "Has non-boolean constant return value.");
                    unplayable = false;
                }
                if (op == intReturn && lastOp != zeroInt) {
                    //returns not a constant false
                    //System.out.println("\t\t\t- " + "Has non-false constant return value.");
                    unplayable = false;
                }
                else if (op == intReturn && lastOp == zeroInt) {
                    //has a hard-false return
                    hardFalse = true;
                }
                lastOp = op;
            }
            System.out.println("\t\t\t- Opcodes: " + opCode);

            if (unplayable) {
                System.out.println("\t\t\t- " + "Unplayable.");
                addUnusableAnnotation(cardClass);
            }
            else {
                System.out.println("\t\t\t- " + (hardFalse ? "Sometimes unplayable?" : "Not unplayable."));
                addPlayableAnnotation(cardClass, hardFalse);
            }
            return true;
        }
        else {
            //Check super
            return recursiveCheckUnplayable(cardClass, cardClass);
        }
    }
    //If a class doesn't declare a canUse, this will attempt to check the superclass.
    private static boolean recursiveCheckUnplayable(CtClass baseClass, CtClass testClass) throws BadBytecode {
        try {
            CtClass superClass = testClass.getSuperclass();
            if (superClass != null) {
                if (!testedClasses.contains(superClass.getName())) {
                    testedClasses.add(superClass.getName());

                    CtMethod[] methods = superClass.getDeclaredMethods();
                    for (CtMethod m : methods) {
                        if ("canUse".equals(m.getName())) {
                            CtClass[] params = m.getParameterTypes();
                            if (params.length == 2 && params[0].getName().equals(AbstractPlayer.class.getName()) && params[1].getName().equals(AbstractMonster.class.getName())) {
                                checkUnplayable(superClass, m);
                                break;
                            }
                        }
                    }
                }
                if (superClass.hasAnnotation(unplayableAnnotation)) {
                    addUnusableAnnotation(baseClass);
                    return true;
                }
            }
            return false;
        }
        catch (NotFoundException ignored) {
            return false;
        }
    }

    private static final String playableAnnotation = "normalrelics/annotations/Playable";
    private static final String unplayableAnnotation = "normalrelics/annotations/Unplayable";
    private static void addPlayableAnnotation(CtClass clazz, boolean sometimes) {
        ConstPool pool = clazz.getClassFile().getConstPool();
        Annotation usable = new Annotation(playableAnnotation, pool);
        usable.addMemberValue("sometimes", new BooleanMemberValue(sometimes, pool));
        AttributeInfo info = clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute attr;
        if (info != null) {
            attr = (AnnotationsAttribute) info;
        }
        else {
            attr = new AnnotationsAttribute(pool, AnnotationsAttribute.visibleTag);
        }
        attr.addAnnotation(usable);
        clazz.getClassFile().addAttribute(attr);
    }
    private static void addUnusableAnnotation(CtClass clazz) {
        ConstPool pool = clazz.getClassFile().getConstPool();
        Annotation usable = new Annotation(unplayableAnnotation, pool);
        AttributeInfo info = clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute attr;
        if (info != null) {
            attr = (AnnotationsAttribute) info;
        }
        else {
            attr = new AnnotationsAttribute(pool, AnnotationsAttribute.visibleTag);
        }
        attr.addAnnotation(usable);
        clazz.getClassFile().addAttribute(attr);
    }


    public static boolean isUnplayable(AbstractPlayer p, AbstractCard c) {
        if (alwaysUnplayable.contains(c.getClass().getName())) //unplayable.
            return true;

        //it's not playable For Now?
        return sometimesUnplayable.contains(c.getClass().getName()) && c.cost == -2 && c.costForTurn == -2 && //might be unplayable, cost says it is
                (!c.canUse(p, null));
    }
    private static Set<String> alwaysUnplayable = new HashSet<>();
    private static Set<String> sometimesUnplayable = new HashSet<>();
    private static boolean hasUnplayableAnnotation(Object o) {
        return o.getClass().isAnnotationPresent(Unplayable.class);
    }
    private static boolean hasPlayableAnnotation(Object o) {
        return o.getClass().isAnnotationPresent(Playable.class);
    }
    private static boolean sometimesUnplayable(AbstractCard c) {
        Playable a = c.getClass().getAnnotation(Playable.class);

        if (a != null) {
            return a.sometimes();
        }
        return false;
    }

    public static void testPlayability() {
        ArrayList<AbstractCard> all = CardLibrary.getAllCards();
        for (AbstractCard c : all) {
            if ((c.type == AbstractCard.CardType.CURSE || c.type == AbstractCard.CardType.STATUS) && c.costForTurn < -1) {
                //Theoretically, an unplayable curse/status.
                //unplayableCards shouldn't contain this since its superclass is just AbstractCard, which doesn't guarantee unplayability.
                if (!hasPlayableAnnotation(c)) {
                    //Does not have a canUse override that sometimes returns true.
                    NormalRelics.logger.info("\t- Unplayable: " + c.name);
                    alwaysUnplayable.add(c.getClass().getName());
                }
            }
            else {
                if (hasUnplayableAnnotation(c)) {
                    NormalRelics.logger.info("\t- Unplayable: " + c.name);
                    alwaysUnplayable.add(c.getClass().getName());
                }
                if (sometimesUnplayable(c)) {
                    NormalRelics.logger.info("\t- Sometimes unplayable? " + c.name);
                    sometimesUnplayable.add(c.getClass().getName());
                }
            }
        }
    }
}

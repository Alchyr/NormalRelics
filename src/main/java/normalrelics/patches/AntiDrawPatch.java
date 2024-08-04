package normalrelics.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;
import normalrelics.relics.MiniPagoda;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AntiDrawPatch {
    @SpirePatch(
            clz = DrawCardAction.class,
            method = "update"
    )
    public static class LikeNoDraw {
        private static final Method endActionWithFollowup;
        static {
            try {
                endActionWithFollowup = DrawCardAction.class.getDeclaredMethod("endActionWithFollowUp");
                endActionWithFollowup.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<?> gimmeACurse(DrawCardAction __instance) throws InvocationTargetException, IllegalAccessException {
            AbstractRelic pagoda = AbstractDungeon.player.getRelic(MiniPagoda.ID);
            if (pagoda != null && pagoda.counter >= 9) {
                pagoda.flash();
                AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, pagoda));

                endActionWithFollowup.invoke(__instance);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPower"));
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "draw",
            paramtypez = { int.class }
    )
    public static class AbsolutelyNot {
        @SpirePrefixPatch
        public static SpireReturn<?> no(AbstractPlayer __instance, int amt) {
            AbstractRelic pagoda = AbstractDungeon.player.getRelic(MiniPagoda.ID);
            if (pagoda != null && pagoda.counter >= 9) {
                pagoda.flash();
                AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, pagoda));
                return SpireReturn.Return();
            }


            return SpireReturn.Continue();
        }
    }
}

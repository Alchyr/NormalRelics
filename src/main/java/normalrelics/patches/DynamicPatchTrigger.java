package normalrelics.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = SpirePatch.CONSTRUCTOR
)
public class DynamicPatchTrigger {
    public static void Raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException, BadBytecode {
        System.out.println("Starting dynamic patches.");

        ClassFinder finder = new ClassFinder();

        finder.add(new File(Loader.STS_JAR));

        for (ModInfo modInfo : Loader.MODINFOS) {
            if (modInfo.jarURL != null) {
                try {
                    finder.add(new File(modInfo.jarURL.toURI()));
                } catch (URISyntaxException e) {
                    // do nothing
                }
            }
        }

        ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();

        AbstractCardDynamicPatch.patch(finder, pool);
        DeepDreamPatch.patch(finder, pool);

        System.out.println("Dynamic patches complete.");
    }

    public static class GamePackageFilter implements ClassFilter {
        private static final Set<String> rejectedPackages;
        static {
            rejectedPackages = new HashSet<>();
            rejectedPackages.add("com.badlogic");
            rejectedPackages.add("com.esotericsoftware");
            rejectedPackages.add("com.fasterxml");
            rejectedPackages.add("com.gikk");
            rejectedPackages.add("com.google");
            rejectedPackages.add("com.jcraft");
            rejectedPackages.add("com.sun");
            rejectedPackages.add("de.robojumper");
            rejectedPackages.add("io.sentry");
            rejectedPackages.add("javazoom.jl");
            rejectedPackages.add("net.arikia");
            rejectedPackages.add("net.java");
            rejectedPackages.add("org.apache");
            rejectedPackages.add("org.lwjgl");
            rejectedPackages.add("org.slf4j");
        }

        public boolean accept(ClassInfo classInfo, ClassFinder classFinder) {
            String name = classInfo.getClassName();
            int secondPackage = name.indexOf('.');
            if (secondPackage >= 0)
            {
                secondPackage = name.indexOf('.', secondPackage + 1);

                if (secondPackage > 0)
                {
                    name = name.substring(0, secondPackage);

                    return !rejectedPackages.contains(name);
                }
            }

            return true;
        }
    }
}
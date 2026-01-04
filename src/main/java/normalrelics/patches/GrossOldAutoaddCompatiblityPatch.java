package normalrelics.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.net.URL;

public class GrossOldAutoaddCompatiblityPatch {
    @SpirePatch(
            cls = "awakenedOne.AwakenedOneMod",
            method = "autoAddCards",
            optional = true
    )
    @SpirePatch(
            cls = "automaton.AutomatonMod",
            method = "autoAddCards",
            optional = true
    )
    @SpirePatch(
            cls = "champ.ChampMod",
            method = "autoAddCards",
            optional = true
    )
    @SpirePatch(
            cls = "collector.CollectorMod",
            method = "autoAddCards",
            optional = true
    )
    @SpirePatch(
            cls = "hermit.HermitMod",
            method = "autoAddCards",
            optional = true
    )
    @SpirePatch(
            cls = "sneckomod.SneckoMod",
            method = "autoAddCards",
            optional = true
    )
    @SpirePatch(
            cls = "theHexaghost.HexaMod",
            method = "autoAddCards",
            optional = true
    )
    public static class CompatibilityDownfall {
        @SpireInstrumentPatch
        public static ExprEditor findLoc() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    try {
                        if ("getLocation".equals(m.getMethodName())) {
                            //String containingClass = m.getEnclosingClass().getName();
                            m.replace("$_ = ($0 == null) || ($0.getLocation() == null) ? " + GrossOldAutoaddCompatiblityPatch.class.getName() + ".getDownfallModURL() : $proceed($$);");
                        }
                    }
                    catch (Exception ignored) { }
                }
            };
        }
    }

    public static URL getDownfallModURL() {
        for (ModInfo info : Loader.MODINFOS) {
            if (info != null && "downfall".equals(info.ID) && info.jarURL != null) {
                return info.jarURL;
            }
        }
        return null;
    }

    public static URL getModURL(String modID) {
        for (ModInfo info : Loader.MODINFOS) {
            if (info != null && modID != null && modID.equals(info.ID) && info.jarURL != null) {
                return info.jarURL;
            }
        }
        return null;
    }
}

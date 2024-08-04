package normalrelics.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import normalrelics.relics.Placebo;

//Definitely doesn't change anything.
public class PlaceboPatches {
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "setHp",
            paramtypez = { int.class, int.class }
    )
    public static class LowRollHP {
        @SpirePrefixPatch
        public static void littleAdjustment(AbstractMonster __instance, @ByRef int[] min, @ByRef int[] max) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(Placebo.ID)) {
                max[0] = min[0];
                min[0] -= 1;
            }
        }
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "addGoldToRewards"
    )
    public static class JustALittleOnTop {
        @SpirePrefixPatch
        public static void aLittleBit(AbstractRoom __instance, @ByRef int[] amt) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(Placebo.ID)) {
                amt[0] += 3;
            }
        }
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "addPotionToRewards",
            paramtypez = { }
    )
    public static class AFewMorePotionsMightDrop {
        @SpirePostfixPatch
        public static void wheee(AbstractRoom __instance) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(Placebo.ID)) {
                AbstractRoom.blizzardPotionMod += 4;
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getRewardCards"
    )
    public static class ABitMoreShiny {
        @SpirePrefixPatch
        public static void wheeeeeeeee() {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(Placebo.ID)) {
                AbstractDungeon.cardBlizzRandomizer += 1;
            }
        }
    }
}

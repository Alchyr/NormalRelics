package normalrelics.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import normalrelics.powers.FreePowerPower;
import normalrelics.powers.FreeSkillPower;
import normalrelics.relics.Dirt;

@SpirePatch(
        clz = AbstractCard.class,
        method = "freeToPlay"
)
public class FreeCardEffects {
    @SpirePostfixPatch
    public static boolean makeTheSkillsFree(boolean __result, AbstractCard __instance) {
        if (__result) return true;

        if (AbstractDungeon.player == null || AbstractDungeon.currMapNode == null ||
                AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) return false;

        if (__instance.rarity == AbstractCard.CardRarity.BASIC && AbstractDungeon.player.hasRelic(Dirt.ID)) {
            return true;
        }

        String reqPower;
        if (__instance.type == AbstractCard.CardType.SKILL) {
            reqPower = FreeSkillPower.POWER_ID;
        }
        else if (__instance.type == AbstractCard.CardType.POWER) {
            reqPower = FreePowerPower.POWER_ID;
        }
        else {
            return false;
        }

        return AbstractDungeon.player.hasPower(reqPower);
    }
}

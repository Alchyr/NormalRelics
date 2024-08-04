package normalrelics.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import normalrelics.relics.SendingBell;

public class SendingBellReward {
    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "setupItemReward"
    )
    public static class AddReward {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void gimmeACurse(CombatRewardScreen __instance) {
            if (AbstractDungeon.player == null) return;
            if (AbstractDungeon.getCurrRoom().event != null && AbstractDungeon.getCurrRoom().event.noCardsInRewards) return;

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof SendingBell) {
                    ((SendingBell) r).addReward(__instance);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.FieldAccessMatcher(AbstractDungeon.class, "overlayMenu"));
            }
        }
    }

    @SpirePatch(
            clz = RewardItem.class,
            method = "render"
    )
    public static class UseImage {
        @SpireInstrumentPatch
        public static ExprEditor ifItHasOne() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isReader() && f.getFieldName().equals("REWARD_CARD_NORMAL")) {
                        f.replace(
                                "$_ = (this.img != null) ? this.img : $proceed($$);"
                        );
                    }
                }
            };
        }
    }
}

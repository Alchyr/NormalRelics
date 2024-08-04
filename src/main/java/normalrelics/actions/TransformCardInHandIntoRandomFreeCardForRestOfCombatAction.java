package normalrelics.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class TransformCardInHandIntoRandomFreeCardForRestOfCombatAction extends AbstractGameAction {
    private final AbstractCard toReplace;

    public TransformCardInHandIntoRandomFreeCardForRestOfCombatAction(AbstractCard toReplace) {
        this.toReplace = toReplace;
        if (Settings.FAST_MODE) {
            this.startDuration = 0.05F;
        } else {
            this.startDuration = 0.1F;
        }
        this.duration = this.startDuration;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            int handIndex = AbstractDungeon.player.hand.group.indexOf(toReplace);
            if (handIndex == -1) {
                isDone = true;
                return;
            }

            AbstractCard replacement = AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy();

            replacement.modifyCostForCombat(-4177309);

            replacement.current_x = toReplace.current_x;
            replacement.current_y = toReplace.current_y;
            replacement.target_x = toReplace.target_x;
            replacement.target_y = toReplace.target_y;
            replacement.drawScale = 1.0F;
            replacement.targetDrawScale = toReplace.targetDrawScale;
            replacement.angle = toReplace.angle;
            replacement.targetAngle = toReplace.targetAngle;
            replacement.superFlash(Color.WHITE.cpy());
            AbstractDungeon.player.hand.group.set(handIndex, replacement);
            AbstractDungeon.player.hand.glowCheck();
        }

        this.tickDuration();
    }
}

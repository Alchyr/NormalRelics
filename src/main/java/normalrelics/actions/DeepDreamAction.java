package normalrelics.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepScreenCoverEffect;
import normalrelics.NormalRelics;
import normalrelics.effects.DreamFadeEffect;
import normalrelics.patches.DeepDreamPatch;

public class DeepDreamAction extends AbstractGameAction {
    private static final String[] TEXT;

    private int phase;
    private boolean phaseTriggered;

    public DeepDreamAction(int amt) {
        this.amount = amt;
        duration = 0;
        phase = 0;
        phaseTriggered = false;
    }

    @Override
    public void update() {
        if (DeepDreamPatch.isDreaming.get(AbstractDungeon.player) && phase == 0)
        {
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, TEXT[0], true));

            this.isDone = true;
            return;
        }

        duration += Gdx.graphics.getDeltaTime();

        switch (phase)
        {
            case 0: //add vfx to cover stuff up
                if (!phaseTriggered)
                {
                    CardCrawlGame.sound.play("SLEEP_BLANKET");
                    AbstractDungeon.effectList.add(new DreamFadeEffect(true));

                    for(int i = 0; i < 30; ++i) {
                        AbstractDungeon.topLevelEffects.add(new CampfireSleepScreenCoverEffect());
                    }

                    phaseTriggered = true;
                }

                if (duration > (Settings.FAST_MODE ? 0.5f : 1.0f))
                {
                    duration = 0;
                    phase = 1;
                }
                break;
            case 1: //do the swap.
                for (AbstractCard c : AbstractDungeon.player.hand.group)
                    c.unhover();

                //actionManager.cleanCardQueue but without fading every other card for no reason
                AbstractDungeon.actionManager.cardQueue.removeIf(e -> AbstractDungeon.player.hand.contains(e.card));
                AbstractDungeon.player.releaseCard();
                AbstractDungeon.player.hand.stopGlowing();
                AbstractDungeon.player.resetControllerValues(); //also refreshes hand layout
                if (!Settings.isControllerMode) //only if you're in controller mode, though.
                    AbstractDungeon.player.hand.refreshHandLayout();

                DeepDreamPatch.startDream(this.amount);

                AbstractDungeon.player.hand.refreshHandLayout();

                phase = 2;
                duration = 0;
                break;
            case 2: //pause
                if (duration > (Settings.FAST_MODE ? 0.25f : 0.5f))
                {
                    this.isDone = true;
                    addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            AbstractDungeon.player.hand.refreshHandLayout();
                            isDone = true;
                        }
                    });
                }
                break;
        }
    }

    static {
        TEXT = CardCrawlGame.languagePack.getUIString(NormalRelics.makeID("DeepDream")).TEXT;
    }
}

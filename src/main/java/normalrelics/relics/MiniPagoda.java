package normalrelics.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static normalrelics.NormalRelics.makeID;

public class MiniPagoda extends BaseRelic {
    private static final String NAME = MiniPagoda.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    public MiniPagoda() {
        super(ID, NAME, RARITY, SOUND);
    }

    public void onEquip() {
        ++AbstractDungeon.player.energy.energyMaster;
    }
    public void onUnequip() {
        --AbstractDungeon.player.energy.energyMaster;
    }

    @Override
    public void atPreBattle() {
        counter = 0;
    }

    @Override
    public void atTurnStart() {
        counter = 0;
    }

    @Override
    public void onVictory() {
        counter = -1;
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (counter <= 0) {
            counter = 1;
        }
        else if (counter < 9) {
            ++counter;
        }
        if (counter == 9) {
            flash();
            CardCrawlGame.sound.play("TINGSHA", MathUtils.random(-0.4f, -0.2f));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
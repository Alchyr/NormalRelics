package normalrelics.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import normalrelics.actions.DeepDreamAction;

import static normalrelics.NormalRelics.makeID;

public class DreamingStone extends BaseRelic {
    private static final String NAME = DreamingStone.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.SOLID;

    private static final int NUM_TURNS = 3;

    public DreamingStone() {
        super(ID, NAME, RARITY, SOUND);

    }

    public void onEquip() {
        counter = 0;
    }

    public void atTurnStart() {
        if (counter == -1) {
            counter += 2;
        } else {
            ++counter;
        }

        if (counter == NUM_TURNS) {
            counter = 0;
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new DeepDreamAction(10));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
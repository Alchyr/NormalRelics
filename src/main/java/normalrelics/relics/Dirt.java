package normalrelics.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static normalrelics.NormalRelics.makeID;

public class Dirt extends BaseRelic {
    private static final String NAME = Dirt.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.FLAT;

    public Dirt() {
        super(ID, NAME, RARITY, SOUND);
    }

    public boolean canSpawn() {
        return AbstractDungeon.actNum <= 1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
package normalrelics.relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static normalrelics.NormalRelics.makeID;

public class Placebo extends BaseRelic {
    private static final String NAME = Placebo.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.FLAT;

    public Placebo() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public boolean canSpawn() {
        return Settings.isEndless || AbstractDungeon.floorNum <= 30;
    }
}
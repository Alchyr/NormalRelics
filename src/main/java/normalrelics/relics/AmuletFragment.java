package normalrelics.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnRemoveCardFromMasterDeckRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static normalrelics.NormalRelics.makeID;

public class AmuletFragment extends BaseRelic implements OnRemoveCardFromMasterDeckRelic {
    private static final String NAME = AmuletFragment.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public AmuletFragment() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onRemoveCardFromMasterDeck(AbstractCard abstractCard) {
        this.flash();
        AbstractDungeon.player.increaseMaxHp(5, true);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public boolean canSpawn() {
        return Settings.isEndless || AbstractDungeon.floorNum <= 30;
    }
}
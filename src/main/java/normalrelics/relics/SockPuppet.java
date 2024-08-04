package normalrelics.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import normalrelics.patches.AbstractCardDynamicPatch;

import static normalrelics.NormalRelics.makeID;

public class SockPuppet extends BaseRelic {
    private static final String NAME = SockPuppet.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.FLAT;

    public SockPuppet() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (AbstractCardDynamicPatch.isUnplayable(AbstractDungeon.player, drawnCard)) {
            flash();
            addToTop(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4, true));
            addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
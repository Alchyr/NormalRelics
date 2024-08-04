package normalrelics.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import normalrelics.actions.TransformCardInHandIntoRandomFreeCardForRestOfCombatAction;

import static normalrelics.NormalRelics.makeID;

public class OwlCharm extends BaseRelic {
    private static final String NAME = OwlCharm.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.SHOP;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public OwlCharm() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (drawnCard.type == AbstractCard.CardType.POWER) {
            addToBot(new TransformCardInHandIntoRandomFreeCardForRestOfCombatAction(drawnCard));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
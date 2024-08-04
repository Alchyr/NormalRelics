package normalrelics.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static normalrelics.NormalRelics.makeID;

public class CounterfeitCoin extends BaseRelic {
    private static final String NAME = CounterfeitCoin.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public CounterfeitCoin() {
        super(ID, NAME, RARITY, SOUND);
    }

    public void atBattleStart() {
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE) {
                addToBot(new MakeTempCardInDrawPileAction(c.makeStatEquivalentCopy(), 1, true, true));
            }
        }
    }

    public boolean canSpawn() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
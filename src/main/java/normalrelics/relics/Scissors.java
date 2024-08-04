package normalrelics.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static normalrelics.NormalRelics.makeID;

public class Scissors extends BaseRelic {
    private static final String NAME = Scissors.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public Scissors() {
        super(ID, NAME, RARITY, SOUND);
    }

    public float atDamageModify(float damage, AbstractCard c) {
        int mod = 0;

        for (AbstractCard handCard : AbstractDungeon.player.hand.group) {
            if (handCard.type == AbstractCard.CardType.ATTACK && !handCard.equals(c)) {
                ++mod;
            }
        }

        return damage + mod;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
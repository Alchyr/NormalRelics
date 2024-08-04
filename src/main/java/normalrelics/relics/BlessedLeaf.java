package normalrelics.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import normalrelics.actions.BlessedLeafAction;

import java.util.HashSet;
import java.util.Set;

import static normalrelics.NormalRelics.makeID;

public class BlessedLeaf extends BaseRelic {
    private static final String NAME = BlessedLeaf.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public BlessedLeaf() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        countAttacks();
    }

    @Override
    public void onMasterDeckChange() {
        countAttacks();
    }

    private void countAttacks() {
        counter = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                ++counter;
            }
        }
    }

    @Override
    public void atBattleStart() {
        flash();

        Set<AbstractMonster> monsterSet = new HashSet<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                addToBot(new BlessedLeafAction(AbstractDungeon.player, this, 2, monsterSet));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
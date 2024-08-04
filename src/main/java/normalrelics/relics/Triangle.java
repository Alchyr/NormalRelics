package normalrelics.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.FreeAttackPower;
import normalrelics.powers.FreePowerPower;
import normalrelics.powers.FreeSkillPower;

import static normalrelics.NormalRelics.makeID;

public class Triangle extends BaseRelic {
    private static final String NAME = Triangle.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public Triangle() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        flash();
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FreeAttackPower(AbstractDungeon.player, 1)));
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FreeSkillPower(AbstractDungeon.player, 1)));
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FreePowerPower(AbstractDungeon.player, 1)));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
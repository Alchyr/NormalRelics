package normalrelics.actions;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.Set;

public class BlessedLeafAction extends AbstractGameAction {
    private final AbstractRelic sourceRelic;
    private final Set<AbstractMonster> monsterSet;

    public BlessedLeafAction(AbstractCreature source, AbstractRelic sourceRelic, int dmg, Set<AbstractMonster> monsterSet) {
        this.source = source;
        this.sourceRelic = sourceRelic;
        this.monsterSet = monsterSet;

        this.amount = dmg;
        this.actionType = ActionType.DAMAGE;
        this.duration = this.startDuration = 0.04f;
        this.attackEffect = MathUtils.randomBoolean(0.33f) ? AttackEffect.SLASH_DIAGONAL :
                (MathUtils.randomBoolean() ?  AttackEffect.SLASH_HORIZONTAL : AttackEffect.SLASH_VERTICAL);
    }

    @Override
    public void update() {
        if (duration == startDuration) {
            target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);

            if (target == null) {
                this.isDone = true;
                return;
            }

            AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX + MathUtils.random(-20.0F, 20.0F) * Settings.xScale,
                    target.hb.cY + MathUtils.random(-20.0F, 20.0F) * Settings.scale, attackEffect));
        }

        tickDuration();
        if (isDone && target instanceof AbstractMonster && target.currentHealth > 0) {
            if (monsterSet.add((AbstractMonster) target)) {
                addToTop(new RelicAboveCreatureAction(target, sourceRelic));
            }

            this.target.damage(new DamageInfo(source, amount, DamageInfo.DamageType.THORNS));
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
    }
}

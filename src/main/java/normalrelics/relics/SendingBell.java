package normalrelics.relics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Hoarder;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import normalrelics.NormalRelics;
import normalrelics.util.TextureLoader;

import java.util.ArrayList;

import static normalrelics.NormalRelics.imagePath;
import static normalrelics.NormalRelics.makeID;

public class SendingBell extends BaseRelic {
    private static final String NAME = SendingBell.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.SOLID;

    private static final Texture CURSED_ICON = TextureLoader.getTexture(imagePath("CursedReward.png"));

    public SendingBell() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        counter = -4;
    }

    @Override
    public void onVictory() {
        counter = -3;
    }

    public void addReward(CombatRewardScreen rewardScreen) {
        int amt = 3;

        for (AbstractRelic r : AbstractDungeon.player.relics) {
            amt = r.changeNumberOfCardsInReward(amt);
        }

        int attempts = 0;
        ArrayList<AbstractCard> curses = new ArrayList<>();

        outer:
        while (curses.size() < amt && attempts < 9) {
            AbstractCard c = AbstractDungeon.returnRandomCurse();
            for (AbstractCard already : curses) {
                if (already.cardID.equals(c.cardID)) {
                    ++attempts;
                    continue outer;
                }
            }

            curses.add(c);
            attempts = 0;
        }

        RewardItem reward = StSLib.generateCardReward(curses, false);

        reward.img = CURSED_ICON;
        reward.text = DESCRIPTIONS[1];

        rewardScreen.rewards.add(reward);
    }

    public void onObtainCard(AbstractCard card) {
        if (card.color == AbstractCard.CardColor.CURSE) {
            CardCrawlGame.sound.playA("BELL", MathUtils.random(0, 0.1F));

            queueRelic();

            if (ModHelper.isModEnabled(Hoarder.ID)) {
                queueRelic();
                queueRelic();
            }
        }
    }

    private void queueRelic() {
        NormalRelics.queuePreUpdate(()->{
            if (AbstractDungeon.currMapNode == null) return false;
            if (AbstractDungeon.getCurrRoom() == null) return false;

            AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, r);
            return true;
        });
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
    public void update() {
        super.update();

        if (this.hb.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.playA("BELL", MathUtils.random(0, 0.1F));
            this.flash();
        }
    }
}
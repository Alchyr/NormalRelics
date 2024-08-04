package normalrelics.relics;

import static normalrelics.NormalRelics.makeID;

public class VoiceBox extends BaseRelic {
    private static final String NAME = VoiceBox.class.getSimpleName();
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    public VoiceBox() {
        super(ID, NAME, RARITY, SOUND);
    }

    //man why am I like this

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
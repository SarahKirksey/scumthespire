package battleaimod.savestate.monsters.ending;

import basemod.ReflectionHacks;
import battleaimod.savestate.monsters.Monster;
import battleaimod.savestate.monsters.MonsterState;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.ending.SpireShield;
import com.megacrit.cardcrawl.monsters.exordium.SpikeSlime_L;

public class SpireShieldState extends MonsterState {
    private final int moveCount;

    public SpireShieldState(AbstractMonster monster) {
        super(monster);

        this.moveCount = ReflectionHacks
                .getPrivate(monster, SpireShield.class, "moveCount");

        //monsterTypeNumber = Monster.SPIRE_SHIELD.ordinal();
    }

    public SpireShieldState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.moveCount = parsed.get("moveCount").getAsInt();

        //monsterTypeNumber = Monster.SPIRE_SHIELD.ordinal();
    }

    @Override
    public AbstractMonster loadMonster() {
        SpireShield result = new SpireShield();
        populateSharedFields(result);
        return result;
    }

    @Override
    public String encode() {
        JsonObject monsterStateJson = new JsonParser().parse(super.encode()).getAsJsonObject();

        monsterStateJson.addProperty("moveCount", moveCount);

        return monsterStateJson.toString();
    }
}

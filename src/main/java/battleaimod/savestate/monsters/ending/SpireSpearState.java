package battleaimod.savestate.monsters.ending;

import basemod.ReflectionHacks;
import battleaimod.fastobjects.AnimationStateFast;
import battleaimod.savestate.monsters.Monster;
import battleaimod.savestate.monsters.MonsterState;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.cards.red.Immolate;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.ending.SpireSpear;
import com.megacrit.cardcrawl.monsters.exordium.AcidSlime_L;

import static battleaimod.patches.MonsterPatch.shouldGoFast;

public class SpireSpearState extends MonsterState {
    private final int moveCount;
    private final int skewerCount;

    public SpireSpearState(AbstractMonster monster) {
        super(monster);

        this.moveCount = ReflectionHacks.getPrivate(monster, SpireSpear.class, "moveCount");
        this.skewerCount = ReflectionHacks.getPrivate(monster, SpireSpear.class, "skewerCount");

        monsterTypeNumber = Monster.SPIRE_SPEAR.ordinal();
    }

    public SpireSpearState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.moveCount = parsed.get("moveCount").getAsInt();
        this.skewerCount = parsed.get("skewerCount").getAsInt();

        monsterTypeNumber = Monster.SPIRE_SPEAR.ordinal();
    }

    @Override
    public AbstractMonster loadMonster() {
        SpireSpear result = new SpireSpear();
        populateSharedFields(result);

        ReflectionHacks.setPrivate(result, SpireSpear.class, "moveCount", moveCount);
        ReflectionHacks.setPrivate(result, SpireSpear.class, "skewerCount", skewerCount);

        return result;
    }

    @Override
    public String encode() {
        JsonObject monsterStateJson = new JsonParser().parse(super.encode()).getAsJsonObject();

        monsterStateJson.addProperty("moveCount", moveCount);
        monsterStateJson.addProperty("skewerCount", skewerCount);

        return monsterStateJson.toString();
    }
}

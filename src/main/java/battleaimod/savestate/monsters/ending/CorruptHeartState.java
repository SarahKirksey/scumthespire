package battleaimod.savestate.monsters.ending;

import basemod.ReflectionHacks;
import battleaimod.savestate.monsters.Monster;
import battleaimod.savestate.monsters.MonsterState;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.actions.unique.BurnIncreaseAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.monsters.exordium.Hexaghost;
import com.megacrit.cardcrawl.monsters.exordium.HexaghostBody;
import com.megacrit.cardcrawl.monsters.exordium.HexaghostOrb;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static battleaimod.patches.MonsterPatch.shouldGoFast;

public class CorruptHeartState extends MonsterState {
    private final int bloodHitCount;
    private final boolean isFirstMove;
    private final int moveCount;
    private final int buffCount;

    public CorruptHeartState(AbstractMonster monster) {
        super(monster);
    
        bloodHitCount = ReflectionHacks.getPrivate(monster, CorruptHeart.class, "bloodHitCount");
        isFirstMove = ReflectionHacks.getPrivate(monster, CorruptHeart.class, "isFirstMove");
        moveCount = ReflectionHacks.getPrivate(monster, CorruptHeart.class, "moveCount");
        buffCount = ReflectionHacks.getPrivate(monster, CorruptHeart.class, "buffCount");

        monsterTypeNumber = Monster.CORRUPT_HEART.ordinal();
    }

    public CorruptHeartState(String jsonString) {
        super(jsonString);

        // TODO don't parse twice
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.bloodHitCount = parsed.get("bloodHitCount").getAsInt();
        this.isFirstMove = parsed.get("isFirstMove").getAsBoolean();
        this.moveCount = parsed.get("moveCount").getAsInt();
        this.buffCount = parsed.get("buffCount").getAsInt();

        monsterTypeNumber = Monster.HEXAGHOST.ordinal();
    }

    @Override
    public AbstractMonster loadMonster() {
        CorruptHeart monster = new CorruptHeart();
        populateSharedFields(monster);

        ReflectionHacks.setPrivate(monster, CorruptHeart.class, "bloodHitCount", bloodHitCount);
        ReflectionHacks.setPrivate(monster, CorruptHeart.class, "isFirstMove", isFirstMove);
        ReflectionHacks.setPrivate(monster, CorruptHeart.class, "moveCount", moveCount);
        ReflectionHacks.setPrivate(monster, CorruptHeart.class, "buffCount", buffCount);

        return monster;
    }

    @Override
    public String encode() {
        JsonObject monsterStateJson = new JsonParser().parse(super.encode()).getAsJsonObject();

        monsterStateJson.addProperty("bloodHitCount", bloodHitCount);
        monsterStateJson.addProperty("isFirstMove", isFirstMove);
        monsterStateJson.addProperty("moveCount", moveCount);
        monsterStateJson.addProperty("buffCount", buffCount);

        return monsterStateJson.toString();
    }
}

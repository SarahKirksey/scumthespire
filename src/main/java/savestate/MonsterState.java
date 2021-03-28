package savestate;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.vfx.TintEffect;
import fastobjects.monsters.AcidSlime_MFast;
import fastobjects.monsters.CultistFast;
import fastobjects.monsters.JawWormFast;
import fastobjects.monsters.SpikeSlime_SFast;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MonsterState extends CreatureState {
    private final float deathTimer;
    private final boolean tintFadeOutCalled;
    private final boolean escaped;
    private final boolean escapeNext;
    private final AbstractMonster.EnemyType type;
    private final boolean cannotEscape;
    private final ArrayList<DamageInfoState> damage;
    private final ArrayList<Byte> moveHistory;
    private final byte nextMove;
    private final Hitbox intentHb;
    private final AbstractMonster.Intent intent;
    private final AbstractMonster.Intent tipIntent;
    private final float intentAlpha;
    private final float intentAlphaTarget;
    private final float intentOffsetX;
    private final String moveName;
    private final AbstractMonster monster;
    private final EnemyMoveInfo moveInfo;

    public MonsterState(AbstractMonster monster) {
        super(monster);
        JawWormFast worm;
        this.moveInfo = ReflectionHacks
                .getPrivate(monster, AbstractMonster.class, "move");

        this.deathTimer = monster.deathTimer;
        this.tintFadeOutCalled = monster.tintFadeOutCalled;
        this.escaped = monster.escaped;
        this.escapeNext = monster.escapeNext;
        this.type = monster.type;
        this.cannotEscape = monster.cannotEscape;
        this.damage = monster.damage.stream().map(DamageInfoState::new)
                                    .collect(Collectors.toCollection(ArrayList::new));
        this.moveHistory = (ArrayList<Byte>) monster.moveHistory.clone();
        this.nextMove = monster.nextMove;
        this.intentHb = monster.intentHb;
        this.intent = monster.intent;
        this.tipIntent = monster.tipIntent;
        this.intentAlpha = monster.intentAlpha;
        this.intentAlphaTarget = monster.intentAlphaTarget;
        this.intentOffsetX = monster.intentOffsetX;
        this.moveName = monster.moveName;
        this.monster = monster;
    }

    public AbstractMonster loadMonster() {
        AbstractMonster monster = resetMonster();
        super.loadCreature(monster);
        monster.init();


        monster.deathTimer = this.deathTimer;
        monster.tintFadeOutCalled = this.tintFadeOutCalled;
        monster.escaped = this.escaped;
        monster.escapeNext = this.escapeNext;
        monster.type = this.type;
        monster.cannotEscape = this.cannotEscape;
        monster.damage = this.damage.stream().map(DamageInfoState::loadDamageInfo)
                                    .collect(Collectors.toCollection(ArrayList::new));
        monster.moveHistory = (ArrayList<Byte>) this.moveHistory.clone();
        monster.nextMove = this.nextMove;
        monster.intentHb = this.intentHb;
        monster.intent = this.intent;
        monster.tipIntent = this.tipIntent;
        monster.intentAlpha = this.intentAlpha;
        monster.intentAlphaTarget = this.intentAlphaTarget;
        monster.intentOffsetX = this.intentOffsetX;
        monster.moveName = this.moveName;
        monster.setMove(moveName, moveInfo.nextMove, moveInfo.intent, moveInfo.baseDamage, moveInfo.multiplier, moveInfo.isMultiDamage);


        monster.tint = new TintEffect();
        monster.healthBarUpdatedEvent();
        monster.showHealthBar();
        monster.update();
        monster.createIntent();

        monster.updatePowers();

        return monster;
    }

    private AbstractMonster resetMonster() {
        AbstractMonster monster = this.monster;
        float offsetX = (monster.drawX - (float) Settings.WIDTH * 0.75F) / Settings.xScale;
        float offsetY = (monster.drawY - AbstractDungeon.floorY) / Settings.yScale;

        // exordium fastobjects.monsters
        if (monster instanceof AcidSlime_L) {
            monster = new AcidSlime_L(offsetX, offsetY);
        } else if (monster instanceof AcidSlime_M) {
            monster = new AcidSlime_MFast(offsetX, offsetY);
        } else if (monster instanceof AcidSlime_S) {
            monster = new AcidSlime_S(offsetX, offsetY, 0);
        } else if (monster instanceof ApologySlime) {
            monster = new ApologySlime();
        } else if (monster instanceof Cultist) {
            monster = new CultistFast(offsetX, offsetY, false);
            if (intent != AbstractMonster.Intent.BUFF) {
                // clear the firstMove boolean by rolling a move
                monster.rollMove();
            }
        } else if (monster instanceof FungiBeast) {
            monster = new FungiBeast(offsetX, offsetY);
        } else if (monster instanceof GremlinFat) {
            monster = new GremlinFat(offsetX, offsetY);
        } else if (monster instanceof GremlinNob) {
            monster = new GremlinNob(offsetX, offsetY);
        } else if (monster instanceof GremlinThief) {
            monster = new GremlinThief(offsetX, offsetY);
        } else if (monster instanceof GremlinTsundere) {
            monster = new GremlinTsundere(offsetX, offsetY);
        } else if (monster instanceof GremlinWarrior) {
            monster = new GremlinWarrior(offsetX, offsetY);
        } else if (monster instanceof GremlinWizard) {
            monster = new GremlinWizard(offsetX, offsetY);
        } else if (monster instanceof Hexaghost) {
            monster = new Hexaghost();
        } else if (monster instanceof JawWorm || monster instanceof JawWormFast) {
            monster = new JawWormFast(offsetX, offsetY);
        } else if (monster instanceof Lagavulin) {
            monster = new Lagavulin(false);
        } else if (monster instanceof Looter) {
            monster = new Looter(offsetX, offsetY);
        } else if (monster instanceof LouseDefensive) {
            monster = new LouseDefensive(offsetX, offsetY);
        } else if (monster instanceof LouseNormal) {
            monster = new LouseNormal(offsetX, offsetY);
        } else if (monster instanceof Sentry) {
            monster = new Sentry(offsetX, offsetY);
        } else if (monster instanceof SlaverBlue) {
            monster = new SlaverBlue(offsetX, offsetY);
        } else if (monster instanceof SlaverRed) {
            monster = new SlaverRed(offsetX, offsetY);
        } else if (monster instanceof SlimeBoss) {
            monster = new SlimeBoss();
        } else if (monster instanceof SpikeSlime_L) {
            monster = new SpikeSlime_L(offsetX, offsetY);
        } else if (monster instanceof SpikeSlime_M) {
            monster = new SpikeSlime_M(offsetX, offsetY);
        } else if (monster instanceof SpikeSlime_S) {
            monster = new SpikeSlime_SFast(offsetX, offsetY, 0);
        } else if (monster instanceof TheGuardian) {
            monster = new TheGuardian();
        }

        return monster;
    }
}
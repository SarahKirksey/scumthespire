package battleaimod.savestate.monsters.exordium;

import basemod.ReflectionHacks;
import battleaimod.BattleAiMod;
import battleaimod.fastobjects.AnimationStateFast;
import battleaimod.savestate.Monster;
import battleaimod.savestate.MonsterState;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Sentry;

import static battleaimod.patches.MonsterPatch.shouldGoFast;

public class SentryState extends MonsterState {
    public SentryState(AbstractMonster monster) {
        super(monster);

        monsterTypeNumber = Monster.SENTRY.ordinal();
    }

    public SentryState(String jsonString) {
        super(jsonString);

        monsterTypeNumber = Monster.SENTRY.ordinal();
    }

    @Override
    public AbstractMonster loadMonster() {
        Sentry result = new Sentry(offsetX, offsetY);
        populateSharedFields(result);
        return result;
    }

    @SpirePatch(
            clz = Sentry.class,
            paramtypez = {float.class, float.class},
            method = SpirePatch.CONSTRUCTOR
    )
    public static class NoAnimationsPatch {
        @SpireInsertPatch(loc = 66)
        public static SpireReturn Sentry(Sentry _instance, float x, float y) {
            if (shouldGoFast()) {
                _instance.state = new AnimationStateFast();
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = Sentry.class,
            paramtypez = {},
            method = "takeTurn"
    )
    public static class SpyOnTakeTurnPatch {
        static long startTurn = 0;
        static int nextMove = 0;

        public static SpireReturn Prefix(Sentry _instance) {
            if (shouldGoFast()) {
                startTurn = System.currentTimeMillis();
                nextMove = _instance.nextMove;
                return SpireReturn.Continue();
            }
            return SpireReturn.Continue();
        }

        public static SpireReturn Postfix(Sentry _instance) {
            if (shouldGoFast()) {
                if (BattleAiMod.battleAiController != null) {
                    BattleAiMod.battleAiController
                            .addRuntime("Sentry Turn " + nextMove, System
                                    .currentTimeMillis() - startTurn);
                }
                return SpireReturn.Continue();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = MakeTempCardInDiscardAction.class,
            paramtypez = {AbstractCard.class, int.class},
            method = SpirePatch.CONSTRUCTOR
    )
    public static class SpyOnMakeDazePatch {
        static long startConstructor = 0;

        public static SpireReturn Prefix(MakeTempCardInDiscardAction _instance, AbstractCard card, int amount) {
            if (shouldGoFast()) {
                startConstructor = System.currentTimeMillis();
                ReflectionHacks
                        .setPrivate(_instance, MakeTempCardInDiscardAction.class, "numCards", amount);
                _instance.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
                ReflectionHacks
                        .setPrivate(_instance, AbstractGameAction.class, "duration", .0001F);
                ReflectionHacks
                        .setPrivate(_instance, AbstractGameAction.class, "startDuration", .0001F);
                ReflectionHacks
                        .setPrivate(_instance, MakeTempCardInDiscardAction.class, "c", card);
                ReflectionHacks
                        .setPrivate(_instance, MakeTempCardInDiscardAction.class, "sameUUID", false);


                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        public static SpireReturn Postfix(MakeTempCardInDiscardAction _instance, AbstractCard card, int amount) {
            if (shouldGoFast()) {
                if (BattleAiMod.battleAiController != null) {
                    BattleAiMod.battleAiController
                            .addRuntime("Make Temp Card", System
                                    .currentTimeMillis() - startConstructor);
                }
                return SpireReturn.Continue();
            }
            return SpireReturn.Continue();
        }
    }
}

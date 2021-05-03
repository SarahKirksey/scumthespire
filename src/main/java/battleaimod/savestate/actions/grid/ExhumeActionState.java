package battleaimod.savestate.actions.grid;

import basemod.ReflectionHacks;
import battleaimod.savestate.CardState;
import battleaimod.savestate.PlayerState;
import battleaimod.savestate.actions.ActionState;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.ExhumeAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static battleaimod.patches.MonsterPatch.shouldGoFast;

public class ExhumeActionState implements ActionState
{
    private final ArrayList<CardState> exhumes;
    private final boolean upgrade;
    
    public ExhumeActionState(AbstractGameAction action) {
        this((ExhumeAction) action);
    }

    public ExhumeActionState(ExhumeAction action) {
        exhumes = PlayerState.toCardStateArray(ReflectionHacks.getPrivate(action, ExhumeAction.class, "exhumes"));
        upgrade = ReflectionHacks.getPrivate(action, ExhumeAction.class, "upgrade");
    }

    @Override
    public ExhumeAction loadAction() {
        ExhumeAction result = new ExhumeAction(upgrade);
        ArrayList<AbstractCard> cards = this.exhumes.stream().map(CardState::loadCard).collect(Collectors.toCollection(ArrayList::new));
        ReflectionHacks.setPrivate(result, ExhumeAction.class, "exhumes", cards);
        
        // This should make the action only trigger the second half of the update
        ReflectionHacks.setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = ExhumeAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {boolean.class}
    )
    public static class NoFxConstructorPatchOther {
        public static void Postfix(ExhumeAction _instance, boolean upgrade) {
            // I don't know if this patch is necessary.
            if (shouldGoFast()) {
                ReflectionHacks
                        .setPrivate(_instance, AbstractGameAction.class, "duration", Settings.ACTION_DUR_FAST);
            }
        }
    }

    @SpirePatch(
            clz = ExhumeAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleExhumePatch {
        public static void Postfix(ExhumeAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.isScreenUp) {   // TODO I guess? Not really sure.
                _instance.isDone = false;
            }
        }
    }
}

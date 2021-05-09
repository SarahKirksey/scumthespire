package battleaimod.savestate.actions;

import battleaimod.fastobjects.actions.DiscardCardActionFast;
import battleaimod.savestate.actions.untested.AttackFromDeckToHandActionState;
import battleaimod.savestate.actions.untested.DiscardPileToTopOfDeckActionState;
import battleaimod.savestate.actions.untested.ExhumeActionState;
import battleaimod.savestate.actions.untested.SeekActionState;
import battleaimod.savestate.actions.untested.SkillFromDeckToHandActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.defect.SeekAction;
import com.megacrit.cardcrawl.actions.unique.ArmamentsAction;
import com.megacrit.cardcrawl.actions.unique.AttackFromDeckToHandAction;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.actions.unique.DualWieldAction;
import com.megacrit.cardcrawl.actions.unique.ExhumeAction;
import com.megacrit.cardcrawl.actions.unique.SkillFromDeckToHandAction;

import java.util.function.Function;

public enum CurrentAction {
    ARMAMENTS_ACTION(ArmamentsAction.class, ArmamentsActionState::new),
    DISCARD_ACTION(DiscardAction.class, action -> new DiscardActionState((DiscardAction) action)),
    DISCARD_ACTION_FAST(DiscardCardActionFast.class, DiscardActionState::new),
    DREDGE_ACTION(DiscardPileToTopOfDeckAction .class, DiscardPileToTopOfDeckActionState::new),
    DUAL_WIELD_ACTION(DualWieldAction.class, DualWieldActionState::new),
    EXHAUST_ACTION(ExhaustAction.class, ExhaustActionState::new),
    EXHUME_ACTION(ExhumeAction.class, ExhumeActionState::new),
    FETCH_ATTACK_ACTION(AttackFromDeckToHandAction.class, AttackFromDeckToHandActionState::new),
    FETCH_SKILL_ACTION(SkillFromDeckToHandAction.class, SkillFromDeckToHandActionState::new),
    SEEK_ACTION(SeekAction.class, SeekActionState::new);

    public Function<AbstractGameAction, CurrentActionState> factory;
    public Class<? extends AbstractGameAction> actionClass;

    CurrentAction() {
    }

    CurrentAction(Function<AbstractGameAction, CurrentActionState> factory) {
        this.factory = factory;
    }

    CurrentAction(Class<? extends AbstractGameAction> actionClass, Function<AbstractGameAction, CurrentActionState> factory) {
        this.factory = factory;
        this.actionClass = actionClass;
    }

}

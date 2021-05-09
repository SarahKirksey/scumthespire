package battleaimod.savestate.selectscreen;

import basemod.ReflectionHacks;
import battleaimod.BattleAiMod;
import battleaimod.savestate.CardState;
import battleaimod.savestate.PlayerState;
import battleaimod.savestate.actions.ActionState;
import battleaimod.savestate.actions.CurrentActionState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SelectScreenState {
    private final int numCardsToSelect;
    private final ArrayList<CardState> selectedCards;
    private final ArrayList<ActionState> actionQueue;
    private final boolean wereCardsRetrieved;
    private final boolean canPickZero;
    private final boolean upTo;
    private final boolean anyNumber;
    private final boolean forTransform;
    private final boolean forUpgrade;
    private final int numSelected;
    private final CurrentActionState currentActionState;
    private final boolean isDisabled;
//    private final CardQueueItemState queueItemState;
    
    
    
    private final ArrayList<CardState> selectedCardsGrid;
    private final CardGroup targetGroupGrid;
    private final int numCardsGrid;
    private final int cardSelectAmountGrid;
    private final boolean canCancelGrid;
    private final boolean forUpgradeGrid;
    private final boolean forTransformGrid;
    private final boolean forPurgeGrid;
    private final boolean confirmScreenUpGrid;
    private final boolean isJustForConfirmingGrid;
    private final int prevDeckSizeGrid;
    private final boolean cancelWasOnGrid;
    private final boolean anyNumberGrid;
    private final boolean forClarityGrid;
    private final boolean isDisabledGrid;

    public SelectScreenState() {
        selectedCards = PlayerState.toCardStateArray(AbstractDungeon.handCardSelectScreen.selectedCards.group);
        selectedCardsGrid = PlayerState.toCardStateArray(AbstractDungeon.gridSelectScreen.selectedCards);

        this.numCardsToSelect = AbstractDungeon.handCardSelectScreen.numCardsToSelect;
        this.wereCardsRetrieved = AbstractDungeon.handCardSelectScreen.wereCardsRetrieved;
        this.canPickZero = AbstractDungeon.handCardSelectScreen.canPickZero;
        this.upTo = AbstractDungeon.handCardSelectScreen.upTo;
        this.anyNumber = ReflectionHacks
                .getPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "anyNumber");
        this.forTransform = ReflectionHacks
                .getPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "forTransform");
        this.forUpgrade = ReflectionHacks
                .getPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "forUpgrade");
        this.numSelected = AbstractDungeon.handCardSelectScreen.numSelected;
    
        GridCardSelectScreen grid = AbstractDungeon.gridSelectScreen;
        this.targetGroupGrid = AbstractDungeon.gridSelectScreen.targetGroup;
        this.forUpgradeGrid = grid.forUpgrade;
        this.forTransformGrid = grid.forTransform;
        this.forPurgeGrid = grid.forPurge;
        this.confirmScreenUpGrid = grid.confirmScreenUp;
        this.isJustForConfirmingGrid = grid.isJustForConfirming;
        
        this.numCardsGrid = ReflectionHacks.getPrivate(grid, GridCardSelectScreen.class, "numCards");
        this.cardSelectAmountGrid = ReflectionHacks.getPrivate(grid, GridCardSelectScreen.class, "cardSelectAmount");
        this.canCancelGrid = ReflectionHacks.getPrivate(grid, GridCardSelectScreen.class, "canCancel");
        this.prevDeckSizeGrid = ReflectionHacks.getPrivate(grid, GridCardSelectScreen.class, "prevDeckSize");
        this.cancelWasOnGrid = ReflectionHacks.getPrivate(grid, GridCardSelectScreen.class, "cancelWasOn");
        this.anyNumberGrid = ReflectionHacks.getPrivate(grid, GridCardSelectScreen.class, "anyNumber");
        this.forClarityGrid = ReflectionHacks.getPrivate(grid, GridCardSelectScreen.class, "forClarity");
        
        AbstractGameAction currentAction = AbstractDungeon.actionManager.currentAction;

        isDisabled = AbstractDungeon.handCardSelectScreen.button.isDisabled;
        isDisabledGrid = grid.confirmButton.isDisabled;

        if (currentAction != null) {
            if (BattleAiMod.currentActionByClassMap.containsKey(currentAction.getClass())) {
                currentActionState = BattleAiMod.currentActionByClassMap
                        .get(currentAction.getClass()).factory.apply(currentAction);
            } else {
                throw new IllegalStateException("No State Factory for Current Action " + AbstractDungeon.actionManager.currentAction);
            }


            actionQueue = new ArrayList<>();

            for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                if (BattleAiMod.actionByClassMap.containsKey(action.getClass())) {
                    actionQueue.add(BattleAiMod.actionByClassMap.get(action.getClass()).factory
                            .apply(action));
                } else {
                    // These are visual effects that are not worth encoding
                    if (!ActionState.IGNORED_ACTIONS.contains(action.getClass())) {
                        throw new IllegalArgumentException("Unknown action type found in action manager: " + action);
                    }
                }
            }

            if (actionQueue.isEmpty()) {
                throw new IllegalStateException("this shouldn't happen " + AbstractDungeon.actionManager.actions);
            }
        } else {
            currentActionState = null;
            actionQueue = null;
        }

    }

    public void loadHandSelectScreenState() {
        AbstractDungeon.handCardSelectScreen.button.isDisabled = isDisabled;

        AbstractDungeon.handCardSelectScreen.selectedCards.group = this.selectedCards.stream()
                                                                                     .map(CardState::loadCard)
                                                                                     .collect(Collectors
                                                                                             .toCollection(ArrayList::new));

        AbstractDungeon.handCardSelectScreen.numSelected = numSelected;
        AbstractDungeon.handCardSelectScreen.numCardsToSelect = numCardsToSelect;
        AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = wereCardsRetrieved;
        AbstractDungeon.handCardSelectScreen.canPickZero = canPickZero;
        AbstractDungeon.handCardSelectScreen.upTo = upTo;

        ReflectionHacks.setPrivate(
            AbstractDungeon.handCardSelectScreen,
            HandCardSelectScreen.class,
            "anyNumber",
            anyNumber);
        ReflectionHacks.setPrivate(
            AbstractDungeon.handCardSelectScreen,
            HandCardSelectScreen.class,
            "forTransform",
            forTransform);
        ReflectionHacks.setPrivate(
            AbstractDungeon.handCardSelectScreen,
            HandCardSelectScreen.class,
            "forUpgrade",
            forUpgrade);

        AbstractDungeon.handCardSelectScreen.numSelected = numSelected;

        if (currentActionState != null) {
            AbstractDungeon.actionManager.currentAction = currentActionState.loadCurrentAction();
            AbstractDungeon.actionManager.phase = GameActionManager.Phase.EXECUTING_ACTIONS;

            actionQueue.forEach(
                action -> AbstractDungeon.actionManager.actions.add(action.loadAction()));

            if (AbstractDungeon.actionManager.actions.isEmpty()) {
                throw new IllegalStateException("this too shouldn't happen");
            }

        }
    }
    
    public void loadGridSelectScreenState() {
        GridCardSelectScreen grid = AbstractDungeon.gridSelectScreen;
        grid.confirmButton.isDisabled = isDisabledGrid;
        
        grid.targetGroup = targetGroupGrid;
        grid.selectedCards = selectedCardsGrid
                                 .stream()
                                 .map(CardState::loadCard)
                                 .collect(Collectors.toCollection(ArrayList::new));
        
        grid.forUpgrade = forUpgradeGrid;
        grid.forTransform = forTransformGrid;
        grid.forPurge = forPurgeGrid;
        grid.confirmScreenUp = confirmScreenUpGrid;
        grid.isJustForConfirming = isJustForConfirmingGrid;
        grid.cancelWasOn = cancelWasOnGrid;
        grid.anyNumber = anyNumberGrid;
        grid.forClarity = forClarityGrid;
        
        
        ReflectionHacks.setPrivate(
            grid,
            GridCardSelectScreen.class,
            "numCards",
            numCardsGrid);
        ReflectionHacks.setPrivate(
            grid,
            GridCardSelectScreen.class,
            "cardSelectAmount",
            cardSelectAmountGrid);
        ReflectionHacks.setPrivate(
            grid,
            GridCardSelectScreen.class,
            "canCancel",
            canCancelGrid);
        ReflectionHacks.setPrivate(
            grid,
            GridCardSelectScreen.class,
            "prevDeckSize",
            prevDeckSizeGrid);
    }
}

package battleaimod.savestate.actions.grid;

import basemod.ReflectionHacks;
import battleaimod.savestate.CardState;
import battleaimod.savestate.PlayerState;
import battleaimod.savestate.actions.ActionState;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.actions.unique.ExhumeAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static battleaimod.patches.MonsterPatch.shouldGoFast;

public class DiscardPileToTopOfDeckActionState implements ActionState
{
    public final AbstractCreature source;
    
    public DiscardPileToTopOfDeckActionState(AbstractGameAction action) {
        this((DiscardPileToTopOfDeckAction) action);
    }

    public DiscardPileToTopOfDeckActionState(DiscardPileToTopOfDeckAction action) {
        source = action.source;
    }

    @Override
    public DiscardPileToTopOfDeckAction loadAction() {
        return new DiscardPileToTopOfDeckAction(source);
    }
}

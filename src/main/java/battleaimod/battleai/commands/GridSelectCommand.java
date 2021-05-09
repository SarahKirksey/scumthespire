package battleaimod.battleai.commands;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;

import java.util.ArrayList;

public class GridSelectCommand implements Command {
    private final int cardIndex;

    public GridSelectCommand(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public GridSelectCommand(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.cardIndex = parsed.get("card_index").getAsInt();
    }

    @Override
    public void execute() {
//        if (!shouldGoFast()) {
//            System.err.println("executing grid select command");
//            makeGridSelectScreenChoice(cardIndex);
//        } else {
//
        makeGridSelectScreenChoice(cardIndex);
        
//        AbstractDungeon.gridSelectScreen.update();

//        }
    }
    
    @Override
    public String encode() {
        JsonObject cardCommandJson = new JsonObject();

        cardCommandJson.addProperty("type", "GRID_SELECT");
        cardCommandJson.addProperty("card_index", cardIndex);


        return cardCommandJson.toString();
    }

    public static void makeGridSelectScreenChoice(int choice) {
        ReflectionHacks.setPrivate(
            AbstractDungeon.gridSelectScreen,
            GridCardSelectScreen.class,
            "hoveredCard",
            AbstractDungeon.gridSelectScreen.targetGroup.group.get(choice));
        setAndSelectHoveredCard(AbstractDungeon.gridSelectScreen, AbstractDungeon.gridSelectScreen.targetGroup.group.get(choice));
    }

    @Override
    public String toString() {
        return "GridSelectCommand" + cardIndex;
    }
    
    private static void setAndSelectHoveredCard(final GridCardSelectScreen gridSelectScreen, final AbstractCard abstractCard)
    {
        selectHoveredCard(gridSelectScreen, abstractCard, gridSelectScreen.selectedCards);
    }
    
    /*
     * This function is lifted pretty much whole-cloth from GridCardSelectScreen.update().
     * Since this is only part of the larger update function, this had to be pulled aside.
     */
    private static void selectHoveredCard(GridCardSelectScreen screen, AbstractCard hoveredCard, ArrayList<AbstractCard> selectedCards)
    {
        if (hoveredCard != null)
        {
            int cardSelectAmountLocal = ReflectionHacks.getPrivate(
                screen,
                GridCardSelectScreen.class,
                "cardSelectAmount");
            
            int numCardsLocal = ReflectionHacks.getPrivate(
                screen,
                GridCardSelectScreen.class,
                "numCards");
            
            hoveredCard.hb.clicked = false;
            if (!selectedCards.contains(hoveredCard)) {
                if (screen.forClarity && selectedCards.size() > 0) {
                    selectedCards.get(0).stopGlowing();
                    selectedCards.clear();
                    cardSelectAmountLocal--;
                    ReflectionHacks.setPrivate(
                        screen,
                        GridCardSelectScreen.class,
                        "cardSelectAmount",
                        cardSelectAmountLocal);
                }
            
                selectedCards.add(hoveredCard);
                hoveredCard.beginGlowing();
                hoveredCard.targetDrawScale = 0.75F;
                hoveredCard.drawScale = 0.875F;
                cardSelectAmountLocal++;
                ReflectionHacks.setPrivate(
                    screen,
                    GridCardSelectScreen.class,
                    "cardSelectAmount",
                    cardSelectAmountLocal);
                CardCrawlGame.sound.play("CARD_SELECT");
                if (numCardsLocal == cardSelectAmountLocal)
                {
                    if (screen.forUpgrade)
                    {
                        hoveredCard.untip();
                        screen.confirmScreenUp = true;
                        screen.upgradePreviewCard = hoveredCard.makeStatEquivalentCopy();
                        screen.upgradePreviewCard.upgrade();
                        screen.upgradePreviewCard.displayUpgrades();
                        screen.upgradePreviewCard.drawScale = 0.875F;
                        hoveredCard.stopGlowing();
                        selectedCards.clear();
                        AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
                        screen.confirmButton.show();
                        screen.confirmButton.isDisabled = false;
                        return;
                    }
                
                    if (screen.forTransform)
                    {
                        hoveredCard.untip();
                        screen.confirmScreenUp = true;
                        screen.upgradePreviewCard = hoveredCard.makeStatEquivalentCopy();
                        screen.upgradePreviewCard.drawScale = 0.875F;
                        hoveredCard.stopGlowing();
                        selectedCards.clear();
                        AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
                        screen.confirmButton.show();
                        screen.confirmButton.isDisabled = false;
                        return;
                    }
                
                    if (screen.forPurge)
                    {
                        if (numCardsLocal == 1)
                        {
                            hoveredCard.untip();
                            hoveredCard.stopGlowing();
                            screen.confirmScreenUp = true;
                            hoveredCard.current_x = (float) Settings.WIDTH / 2.0F;
                            hoveredCard.target_x = (float)Settings.WIDTH / 2.0F;
                            hoveredCard.current_y = (float)Settings.HEIGHT / 2.0F;
                            hoveredCard.target_y = (float)Settings.HEIGHT / 2.0F;
                            hoveredCard.update();
                            hoveredCard.targetDrawScale = 1.0F;
                            hoveredCard.drawScale = 1.0F;
                            selectedCards.clear();
                            screen.confirmButton.show();
                            screen.confirmButton.isDisabled = false;
                            AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
                        } else {
                            AbstractDungeon.closeCurrentScreen();
                        }
    
                        for(final AbstractCard selectedCard : selectedCards)
                        {
                            selectedCard.stopGlowing();
                        }
                    
                        return;
                    }
                
                    if (!screen.anyNumber)
                    {
                        AbstractDungeon.closeCurrentScreen();
                        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.SHOP)
                        {
                            AbstractDungeon.overlayMenu.cancelButton.hide();
                        } else {
                            AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[3]);
                        }
    
                        for(final AbstractCard selectedCard : selectedCards)
                        {
                            selectedCard.stopGlowing();
                        }
                    
                        if (screen.targetGroup.type == CardGroup.CardGroupType.DISCARD_PILE)
                        {
                            for(final AbstractCard c : screen.targetGroup.group)
                            {
                                c.drawScale = 0.12F;
                                c.targetDrawScale = 0.12F;
                                c.teleportToDiscardPile();
                                c.lighten(true);
                            }
                        }
                    
                        return;
                    }
                
                    if (cardSelectAmountLocal < screen.targetGroup.size() && screen.anyNumber) {
                        AbstractDungeon.closeCurrentScreen();
                        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.SHOP) {
                            AbstractDungeon.overlayMenu.cancelButton.hide();
                        } else {
                            AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[3]);
                        }
    
                        for(final AbstractCard c : selectedCards)
                        {
                            c.stopGlowing();
                        }
                    
                        if (screen.targetGroup.type == CardGroup.CardGroupType.DISCARD_PILE)
                        {
                            for(final AbstractCard c : screen.targetGroup.group)
                            {
                                c.drawScale = 0.12F;
                                c.targetDrawScale = 0.12F;
                                c.teleportToDiscardPile();
                                c.lighten(true);
                            }
                        }
                    }
                }
            }
            else if (selectedCards.contains(hoveredCard))
            {
                hoveredCard.stopGlowing();
                selectedCards.remove(hoveredCard);
                cardSelectAmountLocal--;
                ReflectionHacks.setPrivate(
                    screen,
                    GridCardSelectScreen.class,
                    "cardSelectAmount",
                    cardSelectAmountLocal);
            }
        }
    }
}

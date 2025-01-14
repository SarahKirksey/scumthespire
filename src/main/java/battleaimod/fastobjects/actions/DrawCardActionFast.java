package battleaimod.fastobjects.actions;

import battleaimod.BattleAiMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.PlayerTurnEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static battleaimod.patches.MonsterPatch.shouldGoFast;

/**
 * Draws all the cards in one frame
 */
public class DrawCardActionFast extends AbstractGameAction {
    private static final Logger logger = LogManager.getLogger(DrawCardAction.class.getName());
    private boolean shuffleCheck;
    private boolean clearDrawHistory;
    private AbstractGameAction followUpAction;
    private boolean alreadyDrawing = false;

    public DrawCardActionFast(AbstractCreature source, int amount, boolean endTurnDraw) {
        this.shuffleCheck = false;
        this.clearDrawHistory = true;
        this.followUpAction = null;
        if (endTurnDraw && !shouldGoFast()) {
            AbstractDungeon.topLevelEffects.add(new PlayerTurnEffect());
        }

        this.setValues(AbstractDungeon.player, source, amount);
        this.actionType = ActionType.DRAW;
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FASTER;
        }

    }

    public DrawCardActionFast(AbstractCreature source, int amount) {
        this(source, amount, false);
    }

    public DrawCardActionFast(int amount, boolean clearDrawHistory) {
        this(amount);
        this.clearDrawHistory = clearDrawHistory;
    }

    public DrawCardActionFast(int amount) {
        this(null, amount, false);
    }

    public DrawCardActionFast(int amount, AbstractGameAction action) {
        this(amount, action, true);
    }

    public DrawCardActionFast(int amount, AbstractGameAction action, boolean clearDrawHistory) {
        this(amount, clearDrawHistory);
        this.followUpAction = action;
    }

    public void update() {
        long startDrawUpdate = System.currentTimeMillis();

        if (isDone) {
            return;
        }

        if (alreadyDrawing) {
            return;
        }

        if (this.clearDrawHistory) {
            this.clearDrawHistory = false;
        }

        if (AbstractDungeon.player.hasPower("No Draw")) {
            AbstractDungeon.player.getPower("No Draw").flash();
            this.endActionWithFollowUp();
        } else if (this.amount <= 0) {
            this.endActionWithFollowUp();
        } else {
            int deckSize = AbstractDungeon.player.drawPile.size();
            int discardSize = AbstractDungeon.player.discardPile.size();

            if (BattleAiMod.battleAiController != null) {
                BattleAiMod.battleAiController
                        .addRuntime("Draw Update 1", System.currentTimeMillis() - startDrawUpdate);
            }

            if (!SoulGroup.isActive() || shouldGoFast()) {
                if (deckSize + discardSize == 0) {
                    this.endActionWithFollowUp();
                } else if (AbstractDungeon.player.hand.size() == 10) {
                    AbstractDungeon.player.createHandIsFullDialog();
                    this.endActionWithFollowUp();
                } else {

                    if (BattleAiMod.battleAiController != null) {
                        BattleAiMod.battleAiController
                                .addRuntime("Draw Update 2", System.currentTimeMillis() - startDrawUpdate);
                    }

                    if (!this.shuffleCheck) {
                        int tmp;
                        if (this.amount + AbstractDungeon.player.hand.size() > 10) {
                            tmp = 10 - (this.amount + AbstractDungeon.player.hand.size());
                            this.amount += tmp;
                            AbstractDungeon.player.createHandIsFullDialog();
                        }

                        if (this.amount > deckSize) {
                            alreadyDrawing = true;
                            tmp = this.amount - deckSize;
                            this.addToTop(new DrawCardActionFast(tmp, this.followUpAction, false));
                            this.addToTop(new EmptyDeckShuffleActionFast());
                            if (deckSize != 0) {
                                this.addToTop(new DrawCardActionFast(deckSize, false));
                            }

                            this.amount = 0;
                            this.isDone = true;
                            return;
                        }

                        this.shuffleCheck = true;
                    }

                    if (BattleAiMod.battleAiController != null) {
                        BattleAiMod.battleAiController
                                .addRuntime("Draw Update 3", System.currentTimeMillis() - startDrawUpdate);
                    }

                    while (this.amount != 0) {
                        alreadyDrawing = true;
                        --this.amount;
                        if (!AbstractDungeon.player.drawPile.isEmpty()) {
                            AbstractDungeon.player.draw();
                            if (!shouldGoFast()) {
                                AbstractDungeon.player.hand.refreshHandLayout();
                            }
                        } else {
                            logger.warn("Player attempted to draw from an empty drawpile mid-DrawAction?MASTER DECK: " + AbstractDungeon.player.masterDeck
                                    .getCardNames());
                            this.endActionWithFollowUp();
                        }

                        if (this.amount == 0) {
                            this.endActionWithFollowUp();
                        }


                    }


                    if (BattleAiMod.battleAiController != null) {
                        BattleAiMod.battleAiController
                                .addRuntime("Draw Update 4", System.currentTimeMillis() - startDrawUpdate);
                    }

                    if (this.amount == 0) {
                        this.endActionWithFollowUp();
                    }

                }
            }
        }
    }

    private void endActionWithFollowUp() {
        this.isDone = true;
        if (this.followUpAction != null) {
            this.addToTop(this.followUpAction);
        }

    }
}

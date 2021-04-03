package battleai;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import communicationmod.ChoiceScreenUtils;
import savestate.SaveState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

public class BattleAiController {
    public static PriorityQueue<TurnNode> turns = new PriorityQueue<>();
    public static StateNode root = null;

    public int minDamage = 5000;
    public static StateNode bestEnd = null;
    public static StateNode bestEndSoFar = null;

    public static int startingHealth;
    public boolean isDone = false;
    public SaveState startingState;
    private boolean initialized = false;
    private Iterator<Command> bestPathRunner;
    private TurnNode curTurn;

    private int turnsLoaded = 0;
    public TurnNode furthestSoFar = null;

    public boolean runCommandMode = false;
    public boolean runPartialMode = false;

    public BattleAiController(SaveState state) {
        minDamage = 5000;
        bestEnd = null;
        startingState = state;
        initialized = false;
        startingState.loadState();
    }

    public BattleAiController(Collection<Command> commands) {
        runCommandMode = true;
        bestPathRunner = commands.iterator();
    }

    public static boolean shouldStep() {
        return shouldCheckForPlays() || isEndCommandAvailable() || !ChoiceScreenUtils
                .getCurrentChoiceList().isEmpty();
    }

    public static boolean isInDungeon() {
        return CardCrawlGame.mode == CardCrawlGame.GameMode.GAMEPLAY && AbstractDungeon
                .isPlayerInDungeon() && AbstractDungeon.currMapNode != null;
    }

    private static boolean shouldCheckForPlays() {
        return isInDungeon() && (AbstractDungeon
                .getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.isScreenUp);
    }

    private static boolean isEndCommandAvailable() {
        return isInDungeon() && AbstractDungeon
                .getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.isScreenUp;
    }

    public void step() {
        if (isDone) {
            return;
        }
        if (!runCommandMode && !runPartialMode) {
            if (minDamage == 0) {
                System.err.println("we are done");
                runCommandMode = true;

                ArrayList<Command> commands = new ArrayList<>();
                StateNode iterator = bestEnd;
                while (iterator != null) {
                    if (iterator.lastCommand != null) {
                        commands.add(0, iterator.lastCommand);
                    }
                    System.err.println(iterator.lastCommand);
                    iterator = iterator.parent;
                }

                startingState.loadState();
                bestPathRunner = commands.iterator();
                return;
            }

            if (turnsLoaded >= 100 && curTurn == null) {
                System.err.println("should go into partial rerun");
                runPartialMode = true;
                turnsLoaded = 0;

                ArrayList<Command> commands = new ArrayList<>();
                StateNode iterator = bestEndSoFar;
                while (iterator != root.parent) {
                    if (iterator.lastCommand != null) {
                        commands.add(0, iterator.lastCommand);
                    }
                    System.err.println(iterator.lastCommand);
                    iterator = iterator.parent;
                }

                System.err.println("loading for patial state");
                startingState.loadState();
                System.err.println("loaded for patial state");
                bestPathRunner = commands.iterator();
                return;
            }

            GameActionManager s;
            long currentTime = System.nanoTime();

            if (!initialized) {
                StateNode.turnLabel = 0;
                initialized = true;
                runCommandMode = false;
                StateNode firstStateContainer = new StateNode(null, null, this);
                startingHealth = startingState.getPlayerHealth();
                root = firstStateContainer;
                firstStateContainer.saveState = startingState;
                turns = new PriorityQueue<>();
                turns.add(new TurnNode(firstStateContainer, this));
            }

            while (!turns.isEmpty() && (curTurn == null || curTurn.isDone)) {
                curTurn = turns.peek();
                System.err.println("the best turn has damage " + curTurn + " " + turns
                        .size() + " " + (++turnsLoaded));
                if (curTurn.isDone) {
                    System.err.println("finished turn");
                    turns.poll();
                }
            }

            if (curTurn.isDone && turns.isEmpty()) {
                runCommandMode = true;

                ArrayList<Command> commands = new ArrayList<>( );
                StateNode iterator = bestEnd;
                while (iterator != null) {
                    if (iterator.lastCommand != null) {
                        commands.add(0, iterator.lastCommand);
                    }
                    System.err.println(iterator.lastCommand);
                    iterator = iterator.parent;
                }

                startingState.loadState();
                bestPathRunner = commands.iterator();
                return;
            } else {
                boolean reachedNewTurn = curTurn.step();
                if (reachedNewTurn) {
                    curTurn = null;
                }
            }

        }
        if (runPartialMode) {
            System.err.println("starting partial rerun");
            boolean foundCommand = false;
            while (bestPathRunner.hasNext() && !foundCommand) {
                Command command = bestPathRunner.next();
                if (command != null) {
                    foundCommand = true;
                    command.execute();
                } else {
                    foundCommand = true;
                    startingState.loadState();
                }
            }

            if (!bestPathRunner.hasNext()) {
                turns = new PriorityQueue<>();
                root = bestEndSoFar;
                StateNode rootClone = new StateNode(root.parent, root.lastCommand, this);
                turns.add(new TurnNode(rootClone, this));
                runPartialMode = false;
                curTurn = null;
            }
        } else if (runCommandMode) {
            boolean foundCommand = false;
            while (bestPathRunner.hasNext() && !foundCommand) {
                Command command = bestPathRunner.next();
                if (command != null) {
                    foundCommand = true;
                    command.execute();
                } else {
                    foundCommand = true;
                    startingState.loadState();
                }
            }

            if (!bestPathRunner.hasNext()) {
                turns = new PriorityQueue<>();
                root = null;
                minDamage = 5000;
                bestEnd = null;
                isDone = true;
            }
        }
    }
}

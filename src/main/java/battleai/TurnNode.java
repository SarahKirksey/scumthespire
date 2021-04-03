package battleai;

import communicationmod.CommunicationMod;
import savestate.SaveState;

import java.util.HashSet;
import java.util.Stack;

public class TurnNode implements Comparable<TurnNode> {
    private final BattleAiController controller;
    public static Stack<StateNode> states;
    private final HashSet<String> completedTurns = new HashSet<>();
    public boolean runningCommands = false;
    public boolean isDone = false;
    public StateNode startingState;
    private boolean initialized = false;
    int turnIndex = 0;

    public TurnNode(StateNode statenode, BattleAiController controller) {
        startingState = statenode;
        this.controller = controller;
    }

    public boolean step() {
        if (isDone) {
            return true;
        }

        if (!initialized) {
            initialized = true;
            states = new Stack<>();
            states.push(startingState);
        }

        if (states.isEmpty()) {
            CommunicationMod.readyForUpdate = true;
            isDone = true;
            return true;
        }

        StateNode curState = states.peek();
        if (!runningCommands) {
            runningCommands = true;
            curState.saveState.loadState();
        }

        if (curState != startingState && curState.lastCommand instanceof EndCommand) {
            TurnNode toAdd = new TurnNode(curState, controller);
            states.pop();
            runningCommands = false;
            if (curState.saveState == null) {
                //we just ended turn, i dont like this
                curState.saveState = new SaveState();
            }

            if (BattleAiController.bestEndSoFar == null || BattleAiController.bestEndSoFar.saveState.turn < curState.saveState.turn) {
                BattleAiController.bestEndSoFar = curState;
            }

            BattleAiController.turns.add(toAdd);
            turnIndex++;

            CommunicationMod.readyForUpdate = true;
            return true;
        }
        if (curState.isDone()) {
            states.pop();
            if (!states.empty()) {
                states.peek().saveState.loadState();
            }
        } else {
            Command toExecute = curState.step();
            if (toExecute == null) {
                states.pop();
                if (!states.isEmpty()) {
                    states.peek().saveState.loadState();
                }
            } else {
                StateNode toAdd = new StateNode(curState, toExecute, controller);
                if (toExecute instanceof EndCommand) {
                    String dedupeString = toAdd.getTurnString();
                    if (!completedTurns.contains(dedupeString)) {
                        completedTurns.add(dedupeString);
                        states.push(toAdd);
                        toExecute.execute();
                    } else {
                        CommunicationMod.readyForUpdate = true;
                    }
                } else {
                    states.push(toAdd);
                    toExecute.execute();
                }

            }
        }

        if (states.isEmpty()) {
            isDone = true;
        }

        return false;
    }

    @Override
    public String toString() {
        return getPlayerDamage(this) + " " + getTotalMonsterHealth(this) + " " + turnIndex + " " + startingState.saveState.turn;
    }

    public static int getTotalMonsterHealth(TurnNode turnNode) {
        return getTotalMonsterHealth(turnNode.startingState.saveState);
    }

    public static int getPlayerDamage(TurnNode turnNode) {
        return BattleAiController.startingHealth - turnNode.startingState.saveState
                .getPlayerHealth();
    }

    public static int getTotalMonsterHealth(SaveState saveState) {
        return saveState.roomLoader.monsters.stream()
                                            .map(monster -> monster.currentHealth)
                                            .reduce(Integer::sum)
                                            .get();
    }

    public static int getTurnScore(TurnNode turnNode) {
        int playerDamage = getPlayerDamage(turnNode);
        int monsterDamage = getTotalMonsterHealth(turnNode.controller.startingState) - getTotalMonsterHealth(turnNode.startingState.saveState);

        return monsterDamage - 3 * playerDamage;
    }

    @Override
    public int compareTo(TurnNode otherTurn) {
//        int playerDamageThisTurn = getPlayerDamage(this);
//        int playerDamageOtherTurn = getPlayerDamage(otherTurn);
//
//        if (playerDamageThisTurn == playerDamageOtherTurn) {
//            return getTotalMonsterHealth(this) - getTotalMonsterHealth(otherTurn);
//        } else {
//            return playerDamageThisTurn - playerDamageOtherTurn;
//        }


        return getTurnScore(otherTurn) - getTurnScore(this);
    }
}

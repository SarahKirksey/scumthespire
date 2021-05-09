package battleaimod.battleai.commands;

import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class GridSelectConfirmCommand implements Command {
    public static final GridSelectConfirmCommand INSTANCE = new GridSelectConfirmCommand();

    private GridSelectConfirmCommand() {
    }

    @Override
    public void execute() {
        AbstractDungeon.gridSelectScreen.confirmButton.hb.clicked = true;
        AbstractDungeon.gridSelectScreen.update();
    }

    @Override
    public String encode() {
        JsonObject cardCommandJson = new JsonObject();

        cardCommandJson.addProperty("type", "GRID_SELECT_CONFIRM");

        return cardCommandJson.toString();
    }
}

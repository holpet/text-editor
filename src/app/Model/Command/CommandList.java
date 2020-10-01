package app.Model.Command;

import javafx.util.Pair;
import java.util.ArrayList;

public class CommandList {
    // Saves name of the command and the command itself
    ArrayList<Pair<String, Command>> savedStates = new ArrayList<>();
    int commandLimit = 25;
    public int currentIndex;
    public Boolean redo; // last usage of getCommand determines whether it was redo/undo action

    public CommandList() {
        this.currentIndex = 0;
        redo = false;
    }

    public int getLastIndex() {
        int index = savedStates.size()-1;
        if (index < 0) index = 0;
        return index;
    }

    public void addCommand(String name, Command command) {
        if (!redo && (currentIndex < 0)) currentIndex = 0;

        // Remove all commands after the new one has been made
        for (int i = getLastIndex(); i > currentIndex; i--) {
            savedStates.remove(i);
        }
        // Save command at the end of an array
        savedStates.add(new Pair<>(name, command));

        // Save commands only up to the final limit - remove the first added state
        if (savedStates.size() > commandLimit) {
            savedStates.remove(0);
        }
        // Everytime a change is made, automatically update current index to the last added command
        currentIndex = getLastIndex();
        //printCommandList();
    }

    public Command getCommand(Boolean redo) {
        // Redo: to check whether the command was called from redo or undo (relevant for currentIndex)
        this.redo = redo;
        if (savedStates.isEmpty()) return null;
        if (stopIndex()) return null;

        //printCommandList();
        // Return a command at the current index
        return savedStates.get(currentIndex).getValue();
    }

    /* Move back when calling undo */
    public void decreaseCurrentIndex() {
        currentIndex--;
    }

    /* Move forward when calling redo */
    public void increaseCurrentIndex() {
        currentIndex++;
    }

    private Boolean stopIndex() {
        if (currentIndex < 0) {
            currentIndex = -1;
            return true;
        }
        else if (currentIndex > getLastIndex()) {
            currentIndex = getLastIndex();
            return true;
        }
        return false;
    }

    public void printCommandList() {
        System.out.println("PRINTING COMMAND LIST.....");
        for (Pair<String, Command> pair : savedStates) {
            System.out.println(pair.getValue().readCommand());
        }
    }

}

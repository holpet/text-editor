package app.Model.Command;

public interface Command {

    void execute();

    void undo();

    void redo();

    String readCommand();

}

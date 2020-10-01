package app.Controller.Menu;

import app.Controller.KeyEventHandler;
import app.Model.Node;
import app.Model.SelectableNode;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;

public class MenuHandler {

    public KeyEventHandler keyEventHandler;
    private String fileAbsolutePath;

    public MenuHandler(KeyEventHandler keyEventHandler) {
        this.keyEventHandler = keyEventHandler;
        this.fileAbsolutePath = null;
        handleSaveFile();
    }

    /** OPEN & READ FROM FILE **/
    public void readTextFromFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
            int value = 0;
            String fontName = keyEventHandler.cursor.getSampleLetter().getFont().getName();
            int fontSize = (int)keyEventHandler.cursor.getSampleLetter().getFont().getSize();

            // Delete current text in editor
            Node node = keyEventHandler.linkedText.getFirst();
            while (!keyEventHandler.linkedText.isAtEnd(node)) {
                keyEventHandler.textRenderer.textManipulator.deleteFromGroup(node);
                node = node.getNext();
            }
            keyEventHandler.linkedText.clearLL();

            // Read text from file
            while ((value = br.read()) != -1) {
                char c = (char)value;
                // Add new line
                if (value == 10) {
                    keyEventHandler.textRenderer.textManipulator.createLetter( ( "" ), fontName, fontSize );
                }// Add all other characters
                if (value != 10 && value != 13) {
                    keyEventHandler.textRenderer.textManipulator.createLetter( ( "" + c ), fontName, fontSize );
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openChosenFile(Stage stage) {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "Choose a file to open");
        File file = fileChooser.showOpenDialog(stage);

        /** Opens file with preferred editor (not this editor) **/
        if (file != null) {
            readTextFromFile(file.getAbsolutePath());
            fileAbsolutePath = file.getAbsolutePath();
        }
    }

    /** SAVE & WRITE TO FILE **/
    public void writeTextToFile() {
        Node tmp = keyEventHandler.linkedText.getFirst();
        try {
            if (fileAbsolutePath != null) {
                File file = new File(fileAbsolutePath);
                if (!file.exists()) file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);

                while (!keyEventHandler.linkedText.isAtEnd(tmp)) {
                    if (tmp.getData().getText().equals("")) {
                        bw.write(System.lineSeparator());
                    }
                    else {
                        bw.write(tmp.getData().getText());
                    }
                    tmp = tmp.getNext();
                }
                bw.close();
            }
            else {
                saveToChosenFile(keyEventHandler.stage);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToChosenFile(Stage stage) {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "Choose a file to save");
        File file = fileChooser.showSaveDialog(stage);
        fileAbsolutePath = file.getAbsolutePath();
        if (file != null) {
            writeTextToFile();
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser, String title) {
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
    }

    private void handleSaveFile() {
        EventHandler<KeyEvent> saveEventHandler = event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.S && event.isControlDown()) {
                writeTextToFile();
            }
        };
        keyEventHandler.scene.addEventHandler(KeyEvent.KEY_PRESSED, saveEventHandler);
    }


}

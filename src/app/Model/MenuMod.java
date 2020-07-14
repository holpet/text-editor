package app.Model;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuMod {

    private Desktop desktop;

    public MenuMod() {
        this.desktop = Desktop.getDesktop();
    }

    // Additional text editor functionality

    /** Open file of choice **/
    public void openSetFile(String filename) {
        File file = new File(".");
        for(String fileNames : file.list()) System.out.println(fileNames);
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openChosenFile(Stage stage) {
        final FileChooser fileChooser = new FileChooser();
        //final Button openButton = new Button("Open a File...");
        //final Button openMultipleButton = new Button("Open Files...");

        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(stage);
        /** Opens file with preferred editor (not this editor) **/
        if (file != null) {
            openFile(file);
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Choose a file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
    }

    private void openFile(File file) {
        try {
            this.desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(MenuMod.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }

    /**
    public void closeApp() {
        KeyCombination keyCombinationWin = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
                    if (keyCombinationWin.match(event) || event.getCode() == KeyCode.ESCAPE
                    ) {
                        new JavaApp().exit();
                    }
    }**/


    /**
    public void save(TextFile textfile) {
        try {
            Files.write(textFile.getFile(), textFile.getContent(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }**/
    /**
    public IOResult<TextFile> load(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            
        }
    }**/
}

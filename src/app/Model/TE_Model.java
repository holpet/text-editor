package app.Model;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TE_Model {

    private Desktop desktop;

    public TE_Model () {
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
            Logger.getLogger(TE_Model.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }


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

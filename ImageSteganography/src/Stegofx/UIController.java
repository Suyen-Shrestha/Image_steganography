package Stegofx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class UIController {

    Image source;
    Stage secStage;

    @FXML
    private MenuItem exit;

    @FXML
    private Button extract;

    @FXML
    private MenuItem save;

    @FXML
    private ImageView imageDisplay;

    @FXML
    private TextArea textIO;

    @FXML
    private Button embed;

    @FXML
    private MenuItem open;

    @FXML
    protected void embedRequest(ActionEvent event) {
        source = Steganography.embedText(textIO.getText(), source);
        imageDisplay.setImage(source);
        textIO.clear();
    }

    @FXML
    protected void extractRequest(ActionEvent event) {
        String message = Steganography.extractText(source);
        textIO.setText(message);
    }

    @FXML
    protected void openRequest(ActionEvent event) {
        source = FileController.openFile(secStage);
        imageDisplay.setImage(source);
        textIO.clear();
    }

    @FXML
    protected void saveRequest(ActionEvent event){
        FileController.saveFile(secStage,source);
    }

    @FXML
    protected void exitRequest(ActionEvent event){
        Platform.exit();
    }


}

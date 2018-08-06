package Stegofx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FileController {

    public static File imageFile;
    public static Image image;

    public static Image openFile(Stage secondaryStage){
        // Create a new File Chooser so the user can select a file from their file system
        FileChooser explorer = new FileChooser();

        // Set the title of the window that will be opened
        explorer.setTitle("Select File to be encoded/decoded");

        // Create a extension filter that will only allow .jpg .png to be selected when browsing for a fill
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select Image File", new String[] { "*.jpg", "*.png" });

        // Add the filter to the explorer
        explorer.getExtensionFilters().add(filter);

        // Opens the explorer file chooser window
        imageFile = explorer.showOpenDialog(secondaryStage);

        // Set our image to the selected file if the file selected exists
        if(imageFile != null){
            // Converts the file to a path using toURI() and toString() allowing the image to locate the selected image file
            image = new Image(imageFile.toURI().toString());
        }

        return image;

    }

    public static void saveFile(Stage secondaryStage, Image saveImage){
        // Create a new File Chooser so the user can select the location they want to save their image under
        FileChooser explorer = new FileChooser();

        // Set the title of the window that will be opened
        explorer.setTitle("Find Location and Set Name for the Image");

        // Create a extension filter that will only allow .jpg .png to be displayed when saving the file
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select Image File", new String[] { "*.jpg", "*.png" });

        // Add the filter to the explorer
        explorer.getExtensionFilters().add(filter);

        // Opens the explorer window so the user can save the file
        File imageFile = explorer.showSaveDialog(secondaryStage);

        // Makes sure the file explorer opened correctly and user entered file name and selected a location
        if(imageFile != null) {
            try {
                // Writes an image using an arbitrary ImageWriter that supports the given format to a File
                // Snapshots the specified JavaFX Image object and stores a copy of its pixels into a BufferedImage object
                ImageIO.write(SwingFXUtils.fromFXImage(saveImage, null), "png", imageFile);
            } catch (IOException e) {
                // Run of the mill IOException try-catch
                e.printStackTrace();
            }
        }
    }
}

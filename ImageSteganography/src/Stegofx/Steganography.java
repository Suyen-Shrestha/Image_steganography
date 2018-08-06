package Stegofx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.BitSet;

public class Steganography extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Loads FXML file created using scene builder
        Parent root = FXMLLoader.load(getClass().getResource("/Stegofx/UserInterface.fxml"));
        // Load the FXML file into the scene that will be displayed in the stage
        Scene primaryScene = new Scene(root);
        // Set stages title
        primaryStage.setTitle("Stegonagraphy");
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
        // Display the application stage
        primaryStage.show();
    }

    public static Image embedText(String message, Image image){
        BitSet messageBits = stringToBits(message);

        // Get image width and height -- have to cast to int because returns double
        // Will be used for traversing image pixels
        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();

        // Used to store new image bits
        WritableImage encodedImage = new WritableImage(imageWidth, imageHeight);
        // Allows us to get the individual pixels from a picture
        PixelReader readPixels = image.getPixelReader();
        // Allows us to write to an image object pixel by pixel
        PixelWriter writePixels = encodedImage.getPixelWriter();

        int counter = 0;

        for(int j = 0; j < imageHeight; j++){
            for(int i = 0; i < imageWidth; i++){
                // Get the integer value of the pixel at (i,j)
                int pixelValue = readPixels.getArgb(i,j);

                // The 8 rightmost bits are for the blue so set the leftmost 24 bits to 0 leaving just the blue bits correctly set
                int blueValue = pixelValue & 0xFF;
                // Shift green's 8 bits to the right most bits and set the leftmost bits to 0
                int greenValue = pixelValue >> 8 & 0xFF;
                // Shift red's bits to the 8 right most bits and set the leftmost 24 bits to 0
                int redValue = pixelValue >> 16 & 0xFF;
                // Shift the alpha bits to the 8 rightmost and set the leftmost 24 to 0
                int alphaValue = pixelValue >> 24 & 0xFF;

                // Blue Pixel
                // If the current bit is 1 "TRUE"
                if(messageBits.get(counter)){
                    // OR the pixels current value with 0x1 = 00000000000000000000000000000000 (sets the right most bit to 1)
                    blueValue |= 0x1;
                } else {
                    // AND the pixels current value with  0xFFFFFFFE = 11111111111111111111111111111110 (sets the right most bit to 0)
                    blueValue &= 0xFFFFFFFE;
                }

                // Green Pixel
                // If the next bit is 1 "TRUE"
                if(messageBits.get(counter + 1)){
                    // OR the pixels current value with 0x1 = 00000000000000000000000000000000 (sets the right most bit to 1)
                    greenValue |= 0x1;
                } else {
                    // AND the pixels current value with  0xFFFFFFFE = 11111111111111111111111111111110 (sets the right most bit to 0)
                    greenValue &= 0xFFFFFFFE;
                }

                // Red Pixel
                // If the next bit is 1 "TRUE"
                if(messageBits.get(counter + 2)){
                    // OR the pixels current value with 0x1 = 00000000000000000000000000000000 (sets the right most bit to 1)
                    redValue |= 0x1;
                } else {
                    // AND the pixels current value with  0xFFFFFFFE = 11111111111111111111111111111110 (sets the right most bit to 0)
                    redValue &= 0xFFFFFFFE;
                }

                // Creating the new pixel color by placing all the components back to their proper location
                pixelValue = blueValue | greenValue << 8 | redValue << 16 | alphaValue << 24;

                writePixels.setArgb(i,j,pixelValue);
                counter += 3;
            }
        }
        return encodedImage;
    }

    public static String extractText(Image image){
        String binaryMessage = "";

        // Get image width and height -- have to cast to int because returns double
        // Will be used for traversing image pixels
        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();

        PixelReader readPixels = image.getPixelReader();

        for(int j = 0; j < imageHeight; j++){
            for(int i = 0; i < imageWidth; i++){
                int pixelValue = readPixels.getArgb(i,j);

                // The 8 rightmost bits are for the blue so set the leftmost 24 bits to 0 leaving just the blue bits correctly set
                int blueValue = pixelValue & 0xFF;
                // Shift green's 8 bits to the right most bits and set the leftmost bits to 0
                int greenValue = pixelValue >> 8 & 0xFF;
                // Shift red's bits to the 8 right most bits and set the leftmost 24 bits to 0
                int redValue = pixelValue >> 16 & 0xFF;
                // Shift the alpha bits to the 8 rightmost and set the leftmost 24 to 0
                int alphaValue = pixelValue >> 24 & 0xFF;

                // Fills each value back out to 9 bits, without effecting the 8 rightmost bits and then gets the LSB
                String blueBit = Integer.toBinaryString(blueValue | 0x100).substring(1);
                String greenBit = Integer.toBinaryString(greenValue | 0x100).substring(1);
                String redBit = Integer.toBinaryString(redValue | 0x100).substring(1);

                //Combines all LSB to create message
                binaryMessage = binaryMessage + blueBit.charAt(blueBit.length() - 1);
                binaryMessage = binaryMessage + greenBit.charAt(greenBit.length() - 1);
                binaryMessage = binaryMessage + redBit.charAt(redBit.length() - 1);

                if(binaryMessage.length() > 8 && binaryMessage.substring(binaryMessage.length() - 9).contains("00000000")){
                    return binaryToString(binaryMessage);
                }
            }
        }
        return "ERROR";
    }

    public static BitSet stringToBits(String message){
        // String used to store binary conversion
        String messageBinary = "";
        // Convert message to character array for later conversion to binar
        for(char c : message.toCharArray()){
            // Creates binary message by converting each character into its binary ASCII code
            messageBinary += Integer.toBinaryString(c | 0x100).substring(1);
        }

        // Vector of bits that can grow as needed and default value is 0 "FALSE"
        BitSet messageBits = new BitSet(messageBinary.length());

        // Loop through all the characters in the binary array to construct the BitSet
        for(int i = 0; i < messageBinary.length(); i++) {
            // Character at position i will either be 0 or 1
            if (messageBinary.charAt(i) == '1') {
                // Set the bit at the current index to 1 "TRUE"
                messageBits.set(i);
            }
        }
        return messageBits;
    }

    public static String binaryToString(String bitMessage){
        String message = "";
        // Converts each byte to a ASCII character
        for(int i = 0; i + 8 < bitMessage.length(); i += 8){
            // Casts to char and parses the string byte by byte
            message += (char) Integer.parseInt(bitMessage.substring(i,i+8), 2);
        }

        return message;
    }

}

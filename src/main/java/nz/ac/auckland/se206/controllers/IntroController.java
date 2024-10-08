package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.io.File;

public class IntroController {

    @FXML
    private Pane videoContainer; // The Pane you added in Scene Builder

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        // Load the video file (replace "path/to/your/video.mp4" with your video file path)
        String videoPath = new File("src/main/resources/videos/intro_noButton.mp4").toURI().toString();
        Media media = new Media(videoPath);

        // Create a MediaPlayer
        mediaPlayer = new MediaPlayer(media);

        // Create a MediaView and add it to the Pane
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(videoContainer.getWidth()); // Adjust video to fit container
        mediaView.setFitHeight(videoContainer.getHeight());

        videoContainer.getChildren().add(mediaView); // Add MediaView to Pane

        // Play the video
        mediaPlayer.play();
    }
}

package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javazoom.jl.player.Player;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class IntroController {
  private boolean isAudioPlaying = false;
  private MediaPlayer mediaPlayer;

  @FXML private Pane videoContainer; // The Pane you added in Scene Builder
  @FXML private Pane room;
  @FXML private ImageView audioImage;

  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage =
      new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  @FXML
  public void initialize() {
    // // Load the video file (replace "path/to/your/video.mp4" with your video file path)
    // String videoPath =
    //     new File("src/main/resources/videos/intro_noButtonNoSounds.mp4").toURI().toString();
    // Media media = new Media(videoPath);

    // // Create a MediaPlayer
    // mediaPlayer = new MediaPlayer(media);

    // // Create a MediaView and add it to the Pane
    // MediaView mediaView = new MediaView(mediaPlayer);
    // // mediaView.setFitWidth(videoContainer.getWidth()); // Adjust video to fit container
    // // mediaView.setFitHeight(videoContainer.getHeight());

    // videoContainer.getChildren().add(mediaView); // Add MediaView to Pane

    room.setOpacity(0.0);
    makeFadeInTransition();
    // // Play the video
    // mediaPlayer.play();
    loadVideoAsync();
    // play audio for intro video
    playAudio("introSounds");
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }

  @FXML
  private void handleToggleSpeech(MouseEvent event) {
    boolean currentStatus = AudioStatus.INSTANCE.isMuted();
    AudioStatus.INSTANCE.setMuted(!currentStatus);
    updateMuteImage(); // Update Image
  }

  /** Update the image in ImageView according to the speech status */
  private void updateMuteImage() {
    if (!AudioStatus.INSTANCE.isMuted()) {
      audioImage.setImage(soundOnImage); // Show Speaker Icon
    } else {
      audioImage.setImage(soundOffImage); // Show Mute icon
    }
  }

  // Load the video in a background thread using Task
  private void loadVideoAsync() {
    Task<Void> loadVideoTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            String videoPath =
                new File("src/main/resources/videos/intro_noButtonNoSounds.mp4").toURI().toString();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);

            // Wait for the media to be ready before proceeding
            mediaPlayer.setOnReady(
                () -> {
                  Platform.runLater(
                      () -> {
                        MediaView mediaView = new MediaView(mediaPlayer);
                        mediaView.setFitWidth(
                            videoContainer.getWidth()); // Adjust video to fit container
                        mediaView.setFitHeight(videoContainer.getHeight());

                        // Add MediaView to Pane and play the video
                        videoContainer.getChildren().add(mediaView);
                        mediaPlayer.play();
                      });
                });

            return null; // Task doesn't need to return a value
          }
        };

    // Optionally handle task success/failure
    loadVideoTask.setOnFailed(
        e -> {
          System.err.println("Failed to load video: " + loadVideoTask.getException().getMessage());
        });

    // Start the task in a background thread
    new Thread(loadVideoTask).start();
  }

  private void playAudio(String mp3FilePath) {
    if (AudioStatus.INSTANCE.isMuted() || isAudioPlaying) {
      return;
    }

    isAudioPlaying = true;

    // Play the audio file
    try {
      FileInputStream fileInputStream =
          new FileInputStream("src/main/resources/sounds/" + mp3FilePath + ".mp3");
      // Create a new player
      Player player = new Player(fileInputStream);
      new Thread(
              () -> {
                try {
                  player.play();
                } catch (Exception e) {
                  e.printStackTrace();
                } finally {
                  isAudioPlaying = false;
                }
              })
          .start();
    } catch (Exception e) {
      e.printStackTrace();
      isAudioPlaying = false;
    }
  }

  private void makeFadeInTransition() {
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(Duration.millis(3500));
    fadeTransition.setNode(room);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.play();
  }
}

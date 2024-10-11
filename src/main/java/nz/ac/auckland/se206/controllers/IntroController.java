package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

/**
 * This controller manages the introduction screen of the application, including video playback,
 * audio control, and scene transitions. It handles interactions such as toggling audio, going back
 * to the previous scene, and playing a fade-in transition for the room element.
 */
public class IntroController extends BaseController {

  // FXML annotated fields corresponding to UI components in the FXML file
  @FXML private Pane videoContainer; // Container Pane for holding the video
  @FXML private Pane room; // Room pane that will undergo fade-in transition

  /**
   * Initializes the introduction screen. It sets up the fade-in transition for the room, loads the
   * video asynchronously, and starts playing the audio if it's not muted.
   */
  @FXML
  public void initialize() {
    super.initialize(); // Call the base class's initialize method

    setupRoomFadeIn(); // Set up fade-in transition for the room
    loadAndPlayIntroVideo(); // Load and play the introduction video

    // Play the intro audio if not muted
    if (!AudioStatus.INSTANCE.isMuted()) {
      playAudio("introSounds");
    }
  }

  /**
   * Handles the "Go Back" action. It stops the audio player if it's playing and navigates to the
   * CrimeScene view.
   *
   * @param event the event triggered by the "Go Back" button
   * @throws ApiProxyException if there is an API-related issue
   * @throws IOException if an I/O error occurs during scene transition
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    stopAudio(); // Stop the audio if it's currently playing
    App.setRoot("CrimeScene"); // Set the root of the scene to "CrimeScene"
  }

  /** Set up a fade-in transition for the room pane with a duration of 3.5 seconds. */
  private void setupRoomFadeIn() {
    room.setOpacity(0.0); // Set initial opacity of the room to 0
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(3500), room);
    fadeTransition.setFromValue(0); // Start from 0 opacity
    fadeTransition.setToValue(1); // Fade to full opacity
    fadeTransition.play(); // Play the transition
  }

  /** Asynchronously loads the introduction video and plays it in the video container pane. */
  private void loadAndPlayIntroVideo() {
    Task<Void> loadVideoTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Load the video from the specified path
            String videoPath =
                new File("src/main/resources/videos/IntroVideo_Caption.mp4").toURI().toString();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(
                () ->
                    Platform.runLater(
                        () -> {
                          // Set up the MediaView for video playback and add it to the video
                          // container
                          MediaView mediaView = new MediaView(mediaPlayer);
                          videoContainer.getChildren().add(mediaView);
                          mediaPlayer.play(); // Start video playback
                        }));
            return null; // Task does not need to return a value
          }
        };

    // Handle failure in video loading
    loadVideoTask.setOnFailed(
        e ->
            System.err.println(
                "Failed to load video: " + e.getSource().getException().getMessage()));

    new Thread(loadVideoTask).start(); // Start the video loading task in a new thread
  }
}

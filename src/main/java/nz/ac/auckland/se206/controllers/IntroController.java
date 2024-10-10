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

/**
 * This controller manages the introduction screen of the application, including video playback,
 * audio control, and scene transitions. It handles interactions such as toggling audio, going back
 * to the previous scene, and playing a fade-in transition for the room element.
 */
public class IntroController {
  private boolean isAudioPlaying = false;
  private MediaPlayer mediaPlayer;
  private Player mp3Player; // For the introSounds MP3 player

  @FXML private Pane videoContainer; // The Pane you added in Scene Builder
  @FXML private Pane room;
  @FXML private ImageView audioImage;

  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage =
      new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  /**
   * Initializes the introduction screen. It sets up the fade-in transition for the room, loads the
   * video asynchronously, and starts playing the audio if it's not muted.
   */
  @FXML
  public void initialize() {
    room.setOpacity(0.0);
    makeFadeInTransition();
    loadVideoAsync();

    updateMuteImage();
    // Play audio for intro video
    if (!AudioStatus.INSTANCE.isMuted()) {
      playAudio("introSounds");
    }
  }

  /**
   * Handles the "Go Back" action. It stops the MP3 player if it's playing and navigates to the
   * CrimeScene view.
   *
   * @param event the event triggered by the "Go Back" button
   * @throws ApiProxyException if there is an API-related issue
   * @throws IOException if an I/O error occurs during scene transition
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    if (mp3Player != null) {
      mp3Player.close(); // Stop the mp3Player
    }
    App.setRoot("CrimeScene");
  }

  /**
   * Handles the mouse click event to toggle the mute status of the audio. It updates the mute
   * status and the image representing the audio status.
   *
   * @param event the mouse click event on the audio icon
   */
  @FXML
  private void handleToggleSpeech(MouseEvent event) {
    boolean currentStatus = AudioStatus.INSTANCE.isMuted();
    AudioStatus.INSTANCE.setMuted(!currentStatus);
    updateMuteImage(); // Update Image
    toggleAudioMute(); // Mute or unmute the playing audio
  }

  /** Update the image in ImageView according to the speech status */
  private void updateMuteImage() {
    if (!AudioStatus.INSTANCE.isMuted()) {
      audioImage.setImage(soundOnImage); // Show Speaker Icon
    } else {
      audioImage.setImage(soundOffImage); // Show Mute icon
    }
  }

  /**
   * Mute or unmute the audio. If the audio is playing, it will be muted. If the audio is muted, it
   * will be unmuted.
   */
  private void toggleAudioMute() {
    if (mediaPlayer != null) {
      mediaPlayer.setMute(AudioStatus.INSTANCE.isMuted()); // Mute/unmute the MediaPlayer
    }
    if (mp3Player != null) {
      // Since the Player class doesn't have built-in mute, stop the sound if muted
      if (AudioStatus.INSTANCE.isMuted()) {
        mp3Player.close(); // Stop the mp3Player
        // } else {
        //   playAudio("introSounds"); // Resume playing the audio
      }
    }
  }

  /**
   * Asynchronously loads the introduction video and plays it. It uses a background task to load the
   * media file and sets up a MediaPlayer to handle video playback in the videoContainer Pane.
   */
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

                        // Add MediaView to Pane and play the video
                        videoContainer.getChildren().add(mediaView);
                        mediaPlayer.play();
                      });
                });

            return null; // Task doesn't need to return a value
          }
        };

    loadVideoTask.setOnFailed(
        e -> {
          System.err.println("Failed to load video: " + loadVideoTask.getException().getMessage());
        });

    new Thread(loadVideoTask).start();
  }

  /**
   * Plays the specified MP3 audio file. The file path should be relative to the resources/sounds
   * directory.
   *
   * @param mp3FilePath the file path of the audio file (without the ".mp3" extension)
   */
  private void playAudio(String mp3FilePath) {
    if (AudioStatus.INSTANCE.isMuted() || isAudioPlaying) {
      return;
    }

    isAudioPlaying = true;

    try {
      FileInputStream fileInputStream =
          new FileInputStream("src/main/resources/sounds/" + mp3FilePath + ".mp3");
      // Create a new player
      mp3Player = new Player(fileInputStream);
      new Thread(
              () -> {
                try {
                  mp3Player.play();
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

  /** Plays a fade-in transition for the room Pane with a duration of 3.5 seconds. */
  private void makeFadeInTransition() {
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(Duration.millis(3500));
    fadeTransition.setNode(room);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.play();
  }
}

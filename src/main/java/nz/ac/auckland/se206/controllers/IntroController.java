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
  private boolean isAudioPlaying = false; // Flag to indicate whether the audio is currently playing
  private MediaPlayer mediaPlayer; // MediaPlayer object for video playback
  private Player mp3Player; // For the introSounds MP3 player

  // FXML annotated fields corresponding to UI components in the FXML file
  @FXML private Pane videoContainer; // Container Pane for holding the video
  @FXML private Pane room; // Room pane that will undergo fade-in transition
  @FXML private ImageView audioImage; // ImageView to display the audio status icon

  // Image assets for audio control
  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage =
      new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  /**
   * Initializes the introduction screen. It sets up the fade-in transition for the room, loads the
   * video asynchronously, and starts playing the audio if it's not muted.
   */
  @FXML
  public void initialize() {
    // Set initial opacity of the room to 0 and apply fade-in transition
    room.setOpacity(0.0);
    makeFadeInTransition();
    loadVideoAsync();

    updateMuteImage(); // Update the mute image icon based on the audio status

    // Play audio for the introduction video if the application is not muted
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
      mp3Player.close(); // Stop the mp3Player if it's playing
    }
    // Set the root of the scene to "CrimeScene"
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
    // Toggle the current mute status
    boolean currentStatus = AudioStatus.INSTANCE.isMuted();
    AudioStatus.INSTANCE.setMuted(!currentStatus);
    updateMuteImage(); // Update the image icon based on the new mute status
    toggleAudioMute(); // Mute or unmute the playing audio based on the updated status
  }

  /** Update the image in ImageView according to the speech status. */
  private void updateMuteImage() {
    if (!AudioStatus.INSTANCE.isMuted()) {
      audioImage.setImage(soundOnImage); // Show Speaker Icon when audio is on
    } else {
      audioImage.setImage(soundOffImage); // Show Mute icon when audio is off
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
      // Since the Player class doesn't have built-in mute functionality, stop the sound if muted
      if (AudioStatus.INSTANCE.isMuted()) {
        mp3Player.close(); // Stop the mp3Player when muting
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
            // Load the video from the specified path
            String videoPath =
                new File("src/main/resources/videos/intro_noButtonNoSounds.mp4").toURI().toString();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);

            // When the media is ready, set up the MediaView and start playback
            mediaPlayer.setOnReady(
                () -> {
                  Platform.runLater(
                      () -> {
                        MediaView mediaView = new MediaView(mediaPlayer);

                        // Add MediaView to the video container Pane and play the video
                        videoContainer.getChildren().add(mediaView);
                        mediaPlayer.play(); // Start video playback
                      });
                });

            return null; // Task does not need to return a value
          }
        };

    // Handle failure in video loading
    loadVideoTask.setOnFailed(
        e -> {
          System.err.println("Failed to load video: " + loadVideoTask.getException().getMessage());
        });

    new Thread(loadVideoTask).start(); // Start the video loading task in a new thread
  }

  /**
   * Plays the specified MP3 audio file. The file path should be relative to the resources/sounds
   * directory.
   *
   * @param mp3FilePath the file path of the audio file (without the ".mp3" extension)
   */
  private void playAudio(String mp3FilePath) {
    // Check if audio is muted or already playing
    if (AudioStatus.INSTANCE.isMuted() || isAudioPlaying) {
      return;
    }

    isAudioPlaying = true; // Set audio status to playing

    try {
      // Load the specified MP3 file and create a new player
      FileInputStream fileInputStream =
          new FileInputStream("src/main/resources/sounds/" + mp3FilePath + ".mp3");
      mp3Player = new Player(fileInputStream);

      // Start a new thread to play the audio
      new Thread(
              () -> {
                try {
                  mp3Player.play(); // Play the audio file
                } catch (Exception e) {
                  e.printStackTrace();
                } finally {
                  isAudioPlaying = false; // Reset audio status once playback is complete
                }
              })
          .start();
    } catch (Exception e) {
      e.printStackTrace();
      isAudioPlaying = false; // Reset audio status if an exception occurs
    }
  }

  /** Plays a fade-in transition for the room Pane with a duration of 3.5 seconds. */
  private void makeFadeInTransition() {
    // Set up a FadeTransition with a duration of 3.5 seconds
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(Duration.millis(3500));
    fadeTransition.setNode(room);
    fadeTransition.setFromValue(0); // Start from 0 opacity
    fadeTransition.setToValue(1); // Fade to full opacity
    fadeTransition.play(); // Play the transition
  }
}

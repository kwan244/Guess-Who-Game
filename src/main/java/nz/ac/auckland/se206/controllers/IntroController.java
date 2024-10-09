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
  private Player mp3Player; // For the introSounds MP3 player

  @FXML private Pane videoContainer; // The Pane you added in Scene Builder
  @FXML private Pane room;
  @FXML private ImageView audioImage;

  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage =
      new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  @FXML
  public void initialize() {
    room.setOpacity(0.0);
    makeFadeInTransition();
    loadVideoAsync();
    // Play audio for intro video
    playAudio("introSounds");
  }

  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }

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

  private void toggleAudioMute() {
    if (mediaPlayer != null) {
      mediaPlayer.setMute(AudioStatus.INSTANCE.isMuted()); // Mute/unmute the MediaPlayer
    }
    if (mp3Player != null) {
      // Since the Player class doesn't have built-in mute, stop the sound if muted
      if (AudioStatus.INSTANCE.isMuted()) {
        mp3Player.close(); // Stop the mp3Player
      } else {
        playAudio("introSounds"); // Resume playing the audio
      }
    }
  }

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

    loadVideoTask.setOnFailed(
        e -> {
          System.err.println("Failed to load video: " + loadVideoTask.getException().getMessage());
        });

    new Thread(loadVideoTask).start();
  }

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

  private void makeFadeInTransition() {
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(Duration.millis(3500));
    fadeTransition.setNode(room);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.play();
  }
}

package nz.ac.auckland.se206.controllers;

import java.io.FileInputStream;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.player.Player;

/**
 * An abstract base controller class that provides common functionality for managing audio playback
 * and UI elements such as audio status icons. This class serves as a superclass for other
 * controllers that require audio control and icon management.
 */
public abstract class BaseController {

  protected MediaPlayer mediaPlayer; // MediaPlayer object for video/audio playback
  protected Player mp3Player; // Player object for handling MP3 file playback
  protected boolean isAudioPlaying = false; // Flag to track if audio is currently playing

  @FXML protected ImageView audioImage; // ImageView to display the audio status icon

  // Image objects representing the audio on/off states
  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage =
      new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  /**
   * Initializes the controller by updating the mute status icon according to the current audio
   * state.
   */
  @FXML
  public void initialize() {
    updateMuteImage(); // Update the audio status icon when the controller is initialized
  }

  /**
   * Updates the display image of the audio icon based on the current mute status. If the audio is
   * not muted, the icon will show the sound-on image; otherwise, it will show the sound-off image.
   */
  protected void updateMuteImage() {
    if (!AudioStatus.INSTANCE.isMuted()) {
      audioImage.setImage(soundOnImage); // Show the sound-on icon
    } else {
      audioImage.setImage(soundOffImage); // Show the sound-off icon
    }
  }

  /**
   * Toggles the audio mute status and updates the audio icon accordingly. If the audio is currently
   * playing, it will be muted; otherwise, it will be unmuted.
   */
  @FXML
  protected void handleToggleSpeech() {
    boolean currentStatus = AudioStatus.INSTANCE.isMuted();
    AudioStatus.INSTANCE.setMuted(!currentStatus); // Toggle the mute status
    updateMuteImage(); // Update the mute image to reflect the new status
    toggleAudioMute(); // Apply mute/unmute to any currently playing audio
  }

  /**
   * Mutes or unmutes the currently playing audio. If the audio is muted, it will stop the playback
   * for MP3 files, as the Player class does not have a built-in mute function.
   */
  protected void toggleAudioMute() {
    if (mediaPlayer != null) {
      mediaPlayer.setMute(AudioStatus.INSTANCE.isMuted()); // Mute or unmute the MediaPlayer
    }
    if (mp3Player != null && AudioStatus.INSTANCE.isMuted()) {
      mp3Player.close(); // Stop the MP3 player when muted, as there is no built-in mute support
    }
  }

  /**
   * Plays the specified MP3 file from the "resources/sounds" directory. This method checks if the
   * audio is muted or already playing before starting playback.
   *
   * @param mp3FilePath The file path of the audio file (without the ".mp3" extension).
   */
  protected void playAudio(String mp3FilePath) {
    // Do not play audio if it is muted or already playing
    if (AudioStatus.INSTANCE.isMuted() || isAudioPlaying) {
      return;
    }

    isAudioPlaying = true; // Set the status to indicate audio is playing

    try {
      // Load the specified MP3 file from the resources directory
      FileInputStream fileInputStream =
          new FileInputStream("src/main/resources/sounds/" + mp3FilePath + ".mp3");
      mp3Player = new Player(fileInputStream);

      // Start a new thread to play the audio file
      new Thread(
              () -> {
                try {
                  mp3Player.play(); // Play the MP3 file
                } catch (Exception e) {
                  e.printStackTrace();
                } finally {
                  isAudioPlaying = false; // Reset the playing status when playback is complete
                }
              })
          .start();
    } catch (Exception e) {
      e.printStackTrace();
      isAudioPlaying = false; // Reset the playing status if an error occurs
    }
  }

  /**
   * Stops the currently playing audio. This method stops both MediaPlayer and MP3Player if they are
   * active, and resets the audio playing status.
   */
  protected void stopAudio() {
    if (mediaPlayer != null) {
      mediaPlayer.stop(); // Stop MediaPlayer playback
      mediaPlayer = null;
    }
    if (mp3Player != null) {
      mp3Player.close(); // Stop MP3 Player playback
      mp3Player = null;
    }
    isAudioPlaying = false; // Reset the audio playing status
  }
}

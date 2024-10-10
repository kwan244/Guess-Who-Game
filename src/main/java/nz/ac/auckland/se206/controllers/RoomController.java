package nz.ac.auckland.se206.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javazoom.jl.player.Player;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController implements TimerListener {

  public static boolean isFirstTimeInit = true;
  public static GameStateContext context = new GameStateContext();
  private MediaPlayer mediaPlayer;
  private Player mp3Player; // For the introSounds MP3 player

  private boolean isAudioPlaying = false;
  @FXML private Pane room;
  @FXML private Rectangle rectComputer;
  @FXML private Rectangle rectPerson1;
  @FXML private Rectangle rectPerson2;
  @FXML private Rectangle rectPerson3;
  @FXML private Rectangle rectPaper;
  @FXML private Rectangle rectShoeprint;
  @FXML private Button btnGuess;
  @FXML private Button btnBackground;
  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private ImageView audioImage;
  @FXML private ImageView firstClue;
  @FXML private ImageView secondClue;
  @FXML private ImageView thirdClue;
  @FXML private ImageView firstSuspect;
  @FXML private ImageView secondSuspect;
  @FXML private ImageView thirdSuspect;
  @FXML private Text clueProgression;
  @FXML private Text suspectProgression;

  private int suspectcount = 0;
  private int cluecount = 0;
  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage =
      new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  @Override
  public void onTimerFinished() {
    // Open the guess view
    try {
      Stage currentStage = (Stage) timerLabel.getScene().getWindow();
      App.openGuess(currentStage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {

    updateMuteImage();

    // Check if clue has been interacted with and update the image
    if (GuessCondition.INSTANCE.isComputerClicked()) {
      firstClue.setImage(
          new Image(getClass().getResourceAsStream("/images/se206_lightsGreen.png")));
      cluecount++;
      clueProgression.setText(cluecount + "/1");
    }
    if (GuessCondition.INSTANCE.isShoeprintClicked()) {
      secondClue.setImage(
          new Image(getClass().getResourceAsStream("/images/se206_lightsGreen.png")));
      cluecount++;
      clueProgression.setText(cluecount + "/1");
    }
    if (GuessCondition.INSTANCE.isPaperClicked()) {
      thirdClue.setImage(
          new Image(getClass().getResourceAsStream("/images/se206_lightsGreen.png")));
      cluecount++;
      clueProgression.setText(cluecount + "/1");
    }
    if (GuessCondition.INSTANCE.isFemaleCustomerClicked()) {
      firstSuspect.setImage(
          new Image(getClass().getResourceAsStream("/images/se206_lightsGreen.png")));
      suspectcount++;
      suspectProgression.setText(suspectcount + "/3");
    }
    if (GuessCondition.INSTANCE.isManagerClicked()) {
      secondSuspect.setImage(
          new Image(getClass().getResourceAsStream("/images/se206_lightsGreen.png")));
      suspectcount++;
      suspectProgression.setText(suspectcount + "/3");
    }
    if (GuessCondition.INSTANCE.isThiefClicked()) {
      thirdSuspect.setImage(
          new Image(getClass().getResourceAsStream("/images/se206_lightsGreen.png")));
      suspectcount++;
      suspectProgression.setText(suspectcount + "/3");
    }

    if (isFirstTimeInit) {
      playAudio("GameStarted");
      isFirstTimeInit = false;
    }
    // lblProfession.setText(context.getProfessionToGuess());
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();

    // Check if condition is met for guessing
    if ((GuessCondition.INSTANCE.isComputerClicked()
            || GuessCondition.INSTANCE.isShoeprintClicked()
            || GuessCondition.INSTANCE.isPaperClicked())
        && GuessCondition.INSTANCE.isFemaleCustomerClicked()
        && GuessCondition.INSTANCE.isManagerClicked()
        && GuessCondition.INSTANCE.isThiefClicked()) {
      GuessCondition.INSTANCE.setConditionMet(true);
    }
  }

  /** Stops the timer. This method can be called to stop the timer when it is no longer needed. */
  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop();
    }
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {}

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {}

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
        // } else {
        //   playAudio("introSounds"); // Resume playing the audio
      }
    }
  }

  /**
   * Handles mouse clicks on rectangles representing people in the room.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    context.handleRectangleClick(event, clickedRectangle.getId());
  }

  @FXML
  private void handleIntroClick(ActionEvent event) throws IOException {
    // make fade out
    makeFadeOut();
    btnBackground.setDisable(true);
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    // Save the original images for clues and suspects
    Image originalFirstClue =
        new Image(getClass().getResourceAsStream("/images/se206_lightsBlue.png"));
    Image originalSecondClue =
        new Image(getClass().getResourceAsStream("/images/se206_lightsBlue.png"));
    Image originalThirdClue =
        new Image(getClass().getResourceAsStream("/images/se206_lightsBlue.png"));

    if ((GuessCondition.INSTANCE.isComputerClicked()
            || GuessCondition.INSTANCE.isShoeprintClicked()
            || GuessCondition.INSTANCE.isPaperClicked())
        && GuessCondition.INSTANCE.isFemaleCustomerClicked()
        && GuessCondition.INSTANCE.isManagerClicked()
        && GuessCondition.INSTANCE.isThiefClicked()) {

      // If the condition is met, handle the guess
      context.handleGuessClick();
      Stage currentStage = (Stage) btnGuess.getScene().getWindow();
      App.openGuess(currentStage);

    } else {

      // If all condition are not met, flash red image for all suspects and one clue
      if (!GuessCondition.INSTANCE.isFemaleCustomerClicked()
          && !GuessCondition.INSTANCE.isManagerClicked()
          && !GuessCondition.INSTANCE.isThiefClicked()
          && !GuessCondition.INSTANCE.isComputerClicked()
          && !GuessCondition.INSTANCE.isShoeprintClicked()
          && !GuessCondition.INSTANCE.isPaperClicked()) {
        flashRedThenReset(firstSuspect, originalFirstClue);
        flashRedThenReset(secondSuspect, originalSecondClue);
        flashRedThenReset(thirdSuspect, originalThirdClue);
        flashRedThenReset(firstClue, originalFirstClue);
      } else if (!GuessCondition.INSTANCE.isFemaleCustomerClicked()) {
        if (!GuessCondition.INSTANCE.isManagerClicked()) {
          flashRedThenReset(secondSuspect, originalFirstClue);
        }
        if (!GuessCondition.INSTANCE.isThiefClicked()) {
          flashRedThenReset(thirdSuspect, originalSecondClue);
        }
        flashRedThenReset(firstSuspect, originalFirstClue);
      } else if (!GuessCondition.INSTANCE.isManagerClicked()) {
        if (!GuessCondition.INSTANCE.isThiefClicked()) {
          flashRedThenReset(thirdSuspect, originalSecondClue);
        }
        if (!GuessCondition.INSTANCE.isFemaleCustomerClicked()) {
          flashRedThenReset(firstSuspect, originalFirstClue);
        }
        flashRedThenReset(secondSuspect, originalSecondClue);
      } else if (!GuessCondition.INSTANCE.isThiefClicked()) {
        if (!GuessCondition.INSTANCE.isFemaleCustomerClicked()) {
          flashRedThenReset(firstSuspect, originalFirstClue);
        }
        if (!GuessCondition.INSTANCE.isManagerClicked()) {
          flashRedThenReset(secondSuspect, originalSecondClue);
        }
        flashRedThenReset(thirdSuspect, originalThirdClue);
      }
      if (!GuessCondition.INSTANCE.isComputerClicked()
          && !GuessCondition.INSTANCE.isShoeprintClicked()
          && !GuessCondition.INSTANCE.isPaperClicked()) {
        // flash unchecked clue
        if (!GuessCondition.INSTANCE.isShoeprintClicked()) {
          flashRedThenReset(firstClue, originalSecondClue);
        } else if (!GuessCondition.INSTANCE.isPaperClicked()) {
          flashRedThenReset(secondClue, originalThirdClue);
        } else if (!GuessCondition.INSTANCE.isComputerClicked()) {
          flashRedThenReset(thirdClue, originalFirstClue);
        }
      }
    }
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

  private void makeFadeOut() {
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(Duration.millis(1000));
    fadeTransition.setNode(room);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);

    fadeTransition.setOnFinished(
        (ActionEvent event) -> {
          loadIntro();
        });
    fadeTransition.play();
  }

  private void loadIntro() {
    try {
      Stage currentStage = (Stage) btnGuess.getScene().getWindow();
      App.openIntro(currentStage);
    }  catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void flashRedThenReset(ImageView imageView, Image originalImage) {
    // Set the image to red (or the "lightsRed.png" version)
    imageView.setImage(new Image(getClass().getResourceAsStream("/images/se206_lightsRed.png")));

    // Create a PauseTransition for 1 second
    PauseTransition pause = new PauseTransition(Duration.seconds(1));

    // After 1 second, revert back to the original image
    pause.setOnFinished(event -> imageView.setImage(originalImage));

    // Start the pause timer
    pause.play();
  }
}

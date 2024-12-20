package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.io.IOException;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController extends BaseController implements TimerListener {

  public static boolean isFirstTimeInit = true;
  public static GameStateContext context = new GameStateContext();

  @FXML private MediaView mediaView;
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

  private MediaPlayer mediaPlayer; // Media player to control playback
  private int suspectcount = 0;
  private int cluecount = 0;

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

    super.initialize();

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

  private void playVideo(String videoPath) {
    String videoSrc;
    // Play the intro audio if not muted
    if (!AudioStatus.INSTANCE.isMuted()) {
      videoSrc = new File("src/main/resources/videos/IntroVideo_withSounds.mp4").toURI().toString();
    } else {
      videoSrc = new File("src/main/resources/videos/IntroVideo_Caption.mp4").toURI().toString();
    }
    // Media media = new Media(getClass().getResource("/videos/" + videoPath).toExternalForm());
    Media media = new Media(videoSrc);
    mediaPlayer = new MediaPlayer(media);

    mediaView.setMediaPlayer(mediaPlayer);
    mediaPlayer.play();

    mediaPlayer.setOnEndOfMedia(
        () -> mediaView.setVisible(false)); // Hide MediaView); // Loop video
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
    updateMuteImage(); // Update Image according to the status of the audio
    toggleAudioMute(); // Mute or unmute the playing audio
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
    // make fade out transition and disable the button to prevent multiple clicks
    // makeFadeOut();
    mediaView.setVisible(true);
    playVideo("videos/IntroVideo_withSounds.mp4"); // Adjust the path as necessary
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

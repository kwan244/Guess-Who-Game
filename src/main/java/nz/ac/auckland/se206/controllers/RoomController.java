package nz.ac.auckland.se206.controllers;

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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;
import nz.ac.auckland.se206.speech.FreeTextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController implements TimerListener {

  public static boolean isFirstTimeInit = true;
  public static GameStateContext context = new GameStateContext();

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
  @FXML private Text guessCondition;
  @FXML private Text canGuess;
  @FXML private ImageView audioImage;
  @FXML private ImageView backgroundImg;

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
    if (isFirstTimeInit) {
      FreeTextToSpeech.speak("Chat with the suspects, and guess who is the thief");
      canGuess.setVisible(false);
      isFirstTimeInit = false;
      updateMuteImage();
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
      canGuess.setVisible(true);
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
    FreeTextToSpeech.toggleEnabled(); // Toggle voice status
    updateMuteImage(); // Update Image
  }

  /** Update the image in ImageView according to the speech status */
  private void updateMuteImage() {
    if (FreeTextToSpeech.isEnabled()) {
      audioImage.setImage(soundOnImage); // Show Speaker Icon
    } else {
      audioImage.setImage(soundOffImage); // Show Mute icon
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
    FreeTextToSpeech.speak(
        "You are a  detective solving a case of a stolen ring inside this jewelry store. There are"
            + " three suspects to chat with and three clues to interact with.");
    backgroundImg.setVisible(true);
    // 8.5 second delay for the user to read guess condition
    PauseTransition pause = new PauseTransition(Duration.seconds(8.5));
    pause.setOnFinished(e -> backgroundImg.setVisible(false));

    // Start delay
    pause.play();
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    if ((GuessCondition.INSTANCE.isComputerClicked()
            || GuessCondition.INSTANCE.isShoeprintClicked()
            || GuessCondition.INSTANCE.isPaperClicked())
        && GuessCondition.INSTANCE.isFemaleCustomerClicked()
        && GuessCondition.INSTANCE.isManagerClicked()
        && GuessCondition.INSTANCE.isThiefClicked()) {
      context.handleGuessClick();

      Stage currentStage = (Stage) btnGuess.getScene().getWindow();
      App.openGuess(currentStage);

    } else {
      // Show guess condition
      guessCondition.setVisible(true);
      // 2.5 second delay for the user to read guess condition
      PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
      pause.setOnFinished(e -> guessCondition.setVisible(false));

      // Start delay
      pause.play();
    }
  }
}

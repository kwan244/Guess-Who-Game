package nz.ac.auckland.se206.controllers;

import java.io.FileInputStream;
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
import javafx.scene.text.Font;
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

  private boolean isAudioPlaying = false;

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

    updateMuteImage();
    //Font.loadFont(getClass().getResource("/fonts/DigitalDismay.otf").toExternalForm(), 24.0);
    // timerLabel.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());


    if (isFirstTimeInit) {
      playAudio("GameStarted");
      canGuess.setVisible(false);
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
      canGuess.setVisible(true);
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
    playAudio("GameIntro");
    backgroundImg.setVisible(true);
    // 8.5 second delay for the user to read guess condition
    PauseTransition pause = new PauseTransition(Duration.seconds(9.5));
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
}

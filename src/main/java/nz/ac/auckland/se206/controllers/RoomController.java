package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;
import nz.ac.auckland.se206.speech.FreeTextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController implements TimerListener {

  @FXML private Rectangle rectComputer;
  @FXML private Rectangle rectPerson1;
  @FXML private Rectangle rectPerson2;
  @FXML private Rectangle rectPerson3;
  @FXML private Rectangle rectPaper;
  @FXML private Rectangle rectShoeprint;
  @FXML private Label lblProfession;
  @FXML private Button btnGuess;
  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = new GameStateContext();

  @Override
  public void onTimerFinished() {}

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    if (isFirstTimeInit) {
      FreeTextToSpeech.speak("Chat with the three customers, and guess who is the thief?");
      isFirstTimeInit = false;
    }
    lblProfession.setText(context.getProfessionToGuess());
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();
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
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
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

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    context.handleGuessClick();
  }

  /**
   * Handles the exit button click event.
   *
   * @param event the action event triggered by clicking the exit button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleToolClick1(ActionEvent event) throws IOException {}

  /**
   * Handles the exit button click event.
   *
   * @param event the action event triggered by clicking the exit button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleToolClick2(ActionEvent event) throws IOException {}

  /**
   * Handles the exit button click event.
   *
   * @param event the action event triggered by clicking the exit button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleToolClick3(ActionEvent event) throws IOException {}
}

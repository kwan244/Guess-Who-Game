package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;

/**
 * The {@code ComputerController} class handles the interactions and game logic for the computer
 * section of the game, including wire connections and timer events.
 *
 * <p>This class implements the {@link TimerListener} interface to respond to timer events,
 * transitioning the game to the guessing phase when the timer finishes. It manages the state of
 * various visual elements representing wires and their connections. Users can interact with these
 * elements, and the class tracks the state of wire connections, ensuring that the game logic is
 * followed.
 *
 * <p>Key functionalities include:
 *
 * <ul>
 *   <li>Managing the timer and responding to its completion.
 *   <li>Handling user interactions with wires, including mouse events for clicking, entering, and
 *       exiting the wires.
 *   <li>Tracking the state of glove usage and wire connections to determine if the game conditions
 *       are met.
 *   <li>Transitioning the game view based on user actions and game state.
 * </ul>
 *
 * <p>Usage Example:
 *
 * <pre>
 *     // Example usage
 *     ComputerController controller = new ComputerController();
 *     controller.initialize();
 * </pre>
 */
public class ComputerController implements TimerListener {
  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private ImageView leftRedGlow;
  @FXML private ImageView rightRedGlow;
  @FXML private ImageView leftBlackGlow;
  @FXML private ImageView rightBlackGlow;
  @FXML private ImageView leftYellowGlow;
  @FXML private ImageView rightYellowGlow;
  @FXML private ImageView glove;
  @FXML private ImageView yellowConnect;
  @FXML private ImageView redConnect;
  @FXML private ImageView blackConnect;
  private boolean glovewearing = false;
  private boolean wireR1 = false;
  private boolean wireR2 = false;
  private boolean redConnected = false;
  private boolean wireB1 = false;
  private boolean wireB2 = false;
  private boolean blackConnected = false;
  private boolean wireY1 = false;
  private boolean wireY2 = false;
  private boolean yellowConnected = false;
  private int wireCount = 0;

  /**
   * This method is triggered when the timer finishes. It transitions the game to the guess phase by
   * opening the guessing view.
   */
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
   * Initializes the controller by setting up event handlers, initializing the timer, and applying
   * the initial visual effects.
   *
   * @throws ApiProxyException if there is an error with the API proxy configuration
   */
  public void initialize() throws ApiProxyException {
    //
    glove.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 12, 0.5, 0, 0);");
    glove.setOnMouseClicked(this::handleGloveClick);
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();
  }

  /**
   * Sets the mouse event handlers for a given wire ImageView.
   *
   * @param wire the wire ImageView to apply handlers to
   */
  private void setWireHandlers(ImageView wire) {
    wire.setOnMouseEntered(this::handleMouseEnterWire);
    wire.setOnMouseExited(this::handleMouseExitWire);
    wire.setOnMouseClicked(this::handleWireClick);
  }

  /** Stops the timer. This method can be called to stop the timer when it is no longer needed. */
  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop();
    }
  }

  /**
   * Handles the click event on the glove icon.
   *
   * @param event the mouse event that triggered the method
   */
  private void handleGloveClick(MouseEvent event) {
    // if glove is clicked
    glovewearing = true;
    // show glow effect
    glove.setStyle("-fx-effect:null;");
    glove.setDisable(true);
    setWireHandlers(leftRedGlow);
    setWireHandlers(rightRedGlow);
    setWireHandlers(leftBlackGlow);
    setWireHandlers(rightBlackGlow);
    setWireHandlers(leftYellowGlow);
    setWireHandlers(rightYellowGlow);
  }

  /**
   * Handles mouse enter event for a wire, highlighting the wire with a glow effect.
   *
   * @param event the mouse enter event
   */
  public void handleMouseEnterWire(MouseEvent event) {
    ImageView wire = (ImageView) event.getSource();
    wire.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  /**
   * Handles mouse exit event for a wire, removing the glow effect.
   *
   * @param event the mouse exit event
   */
  public void handleMouseExitWire(MouseEvent event) {
    ImageView wire = (ImageView) event.getSource();
    wire.setStyle("-fx-effect:null;");
  }

  /**
   * Handles the click event on a wire, connecting the appropriate wires based on the game logic.
   *
   * @param event the mouse click event
   */
  private void handleWireClick(MouseEvent event) {
    // if glove is clicked and wires are not connected
    if (glovewearing && wireCount < 2) {
      try {
        // Each action for each colour wire
        if (event.getSource().equals(leftRedGlow) && redConnected == false) {
          wireR1 = true;
          leftRedGlow.setVisible(false);
          wireCount++;
          // check same colour wires is connected
          if (wireR1 && wireR2) {
            // correct wire connection
            // show correct wire connection
            redConnected = true;
            redConnect.setVisible(true);
            wireCount = 0;
            checkAllConnected();
          }
          // Check wire count if there are 2 wires reset
          if (checkWireCount()) {
            // reset image, wire count and wire status
            wireR1 = false;
            leftRedGlow.setVisible(true);
          }
        } else if (event.getSource().equals(rightRedGlow) && redConnected == false) {
          wireR2 = true;
          rightRedGlow.setVisible(false);
          wireCount++;
          // Check same colour wires is connected
          if (wireR1 && wireR2) {
            // correct wire connection
            // show correct wire connection
            redConnected = true;
            redConnect.setVisible(true);
            wireCount = 0;
            checkAllConnected();
          }
          // Check wire count if there are 2 wires reset
          if (checkWireCount()) {
            // reset image, wire count and wire status
            wireR2 = false;
            rightRedGlow.setVisible(true);
          }

        } else if (event.getSource().equals(leftBlackGlow) && blackConnected == false) {
          wireB1 = true;
          leftBlackGlow.setVisible(false);
          wireCount++;
          // check same colour wires is connected
          if (wireB1 && wireB2) {
            // correct wire connection
            // show correct wire connection
            blackConnected = true;
            blackConnect.setVisible(true);
            wireCount = 0;
            checkAllConnected();
          }
          // Check wire count if there are 2 wires reset
          if (checkWireCount()) {
            // reset image, wire count and wire status
            wireB1 = false;
            leftBlackGlow.setVisible(true);
          }
        } else if (event.getSource().equals(rightBlackGlow) && blackConnected == false) {
          wireB2 = true;
          rightBlackGlow.setVisible(false);
          wireCount++;
          // check same colour wires is connected
          if (wireB1 && wireB2) {
            // correct wire connection
            // show correct wire connection
            blackConnected = true;
            blackConnect.setVisible(true);
            wireCount = 0;
            checkAllConnected();
          }
          // Check wire count if there are 2 wires reset
          if (checkWireCount()) {
            // reset image, wire count and wire status
            wireB2 = false;
            rightBlackGlow.setVisible(true);
          }
        } else if (event.getSource().equals(leftYellowGlow) && yellowConnected == false) {
          wireY1 = true;
          leftYellowGlow.setVisible(false);
          wireCount++;
          // check same colour wires is connected
          if (wireY1 && wireY2) {
            // correct wire connection
            // show correct wire connection
            yellowConnected = true;
            yellowConnect.setVisible(true);
            wireCount = 0;
            checkAllConnected();
          }
          // Check wire count if there are 2 wires reset
          if (checkWireCount()) {
            // reset image, wire count and wire status
            wireY1 = false;
            leftYellowGlow.setVisible(true);
          }
        } else if (event.getSource().equals(rightYellowGlow) && yellowConnected == false) {
          wireY2 = true;
          rightYellowGlow.setVisible(false);
          wireCount++;
          // check same colour wires is connected
          if (wireY1 && wireY2) {
            // correct wire connection
            // show correct wire connection
            yellowConnected = true;
            yellowConnect.setVisible(true);
            wireCount = 0;
            checkAllConnected();
          }
          // Check wire count if there are 2 wires reset
          if (checkWireCount()) {
            // reset image, wire count and wire status
            wireY2 = false;
            rightYellowGlow.setVisible(true);
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Checks if all wires are connected, and if so, transitions the game to the next phase.
   *
   * @throws IOException if there is an error transitioning to the next view
   */
  private void checkAllConnected() throws IOException {
    if (redConnected && blackConnected && yellowConnected) {
      // all wires connected
      GuessCondition.INSTANCE.setWireCompleted(true);
      // Add a 1-second delay using PauseTransition
      PauseTransition delay = new PauseTransition(Duration.seconds(0.05));
      delay.setOnFinished(
          event -> {
            try {
              Stage currentStage = (Stage) leftYellowGlow.getScene().getWindow();
              App.openComputer(currentStage);
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
      delay.play();
    }
  }

  /**
   * Checks the wire count and resets the state if more than 2 wires are clicked.
   *
   * @return true if the wire count is 2, false otherwise
   */
  private boolean checkWireCount() {
    if (wireCount == 2) {
      wireCount = 1;
      return true;
    }
    return false;
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }
}

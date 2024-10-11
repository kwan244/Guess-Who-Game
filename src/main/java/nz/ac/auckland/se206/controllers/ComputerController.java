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

  public void initialize() throws ApiProxyException {
    //
    glove.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 12, 0.5, 0, 0);");
    glove.setOnMouseClicked(this::handleGloveClick);
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();
  }

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

  public void handleMouseEnterWire(MouseEvent event) {
    ImageView wire = (ImageView) event.getSource();
    wire.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  public void handleMouseExitWire(MouseEvent event) {
    ImageView wire = (ImageView) event.getSource();
    wire.setStyle("-fx-effect:null;");
  }

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

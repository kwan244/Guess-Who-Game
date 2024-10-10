package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;

public class PasswordController implements TimerListener {
  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private TextField txtInput;
  @FXML private TableColumn<?, ?> checkinTime;
  @FXML private TableColumn<?, ?> checkoutTime;
  @FXML private TableColumn<?, ?> hostName;
  @FXML private ImageView visitorLogIcon;
  @FXML private TableColumn<?, ?> visitorName;

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
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) throws ApiProxyException, IOException {
    if (event.getCode() == KeyCode.ENTER) {
      String message = txtInput.getText().trim();
      if (message.isEmpty()) {
        return;
      } else if (message.equals("14101994")) {
        System.out.println("Correct password");
      }
    }
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

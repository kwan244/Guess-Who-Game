package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;
import javafx.util.Duration;
import javafx.scene.control.cell.PropertyValueFactory;

public class PasswordController implements TimerListener {
  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private TextField txtInput;
  @FXML private TextField txtUserName;
  @FXML private TableView<Visitor> table;
  @FXML private Text iconSubtitle;
  @FXML private Text passwordHint;
  @FXML private ImageView visitorLogIcon;
  @FXML private TextArea firstRow;
  @FXML private TextArea secondRow;
  @FXML private TextArea thirdRow;
  @FXML private Rectangle hintBackground;
  // @FXML private TableColumn<Visitor, Integer> checkinTime;
  // @FXML private TableColumn<Visitor, Integer> checkoutTime;
  // @FXML private TableColumn<Visitor, String> hostName;
  
  // @FXML private TableColumn<Visitor, String> visitorName;

  // ObservableList<Visitor> list = FXCollections.observableArrayList(
  //   new Visitor("Donald and Kamala", 930, 1015, "Sophia"),
  //   new Visitor("TotalRandomPerson", 1010, 1040, "SupernumeraryRole"),
  //   new Visitor("RandomPerson2", 1210, 1215, "Sophia"),
  //   new Visitor("Donald", 1315, 1450, "Sophia")
  // );

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

// visitorName.setCellValueFactory(new PropertyValueFactory<Visitor, String>("visitorName"));
// checkinTime.setCellValueFactory(new PropertyValueFactory<Visitor, Integer>("checkinTime"));
// checkoutTime.setCellValueFactory(new PropertyValueFactory<Visitor, Integer>("checkoutTime"));
// hostName.setCellValueFactory(new PropertyValueFactory<Visitor, String>("hostName"));


    // table.setItems(list);
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
  public void onKeyReleased(KeyEvent event1) throws ApiProxyException, IOException {
    if (event1.getCode() == KeyCode.ENTER) {
      String message = txtInput.getText().trim();
      if (message.isEmpty()) {
        return;
      } else if (message.equals("14101994")) {
        txtUserName.setVisible(false);
        txtInput.setVisible(false);
        passwordHint.setVisible(false);
        visitorLogIcon.setVisible(true);
        iconSubtitle.setVisible(true);
        hintBackground.setVisible(false);
      } else {
        txtInput.setText("Incorrect Password! Please Try Again!");
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> txtInput.clear());
        pause.play();
      }
    }
  }

    @FXML
  public void onIconClicked(MouseEvent event) throws ApiProxyException, IOException {
        firstRow.setVisible(true);
        secondRow.setVisible(true);
        thirdRow.setVisible(true);
        table.setVisible(true);
        visitorLogIcon.setDisable(true);
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

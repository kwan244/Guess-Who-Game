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
import javafx.scene.layout.VBox;
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
  @FXML private TableColumn<Visitor, Integer> id;
  @FXML private TableColumn<Visitor, String> checkinTime;
  @FXML private TableColumn<Visitor, String> checkoutTime;
  @FXML private TableColumn<Visitor, String> hostName;
  @FXML private TableColumn<Visitor, String> visitorName;
      @FXML
    private VBox inputVbox;

    @FXML
    private TextField inputId;

    @FXML
    private TextField inputName;

    @FXML
    private TextField inputCheckinTime;

    @FXML
    private TextField inputCheckoutTime;

    @FXML
    private TextField inputHost;

  ObservableList<Visitor> list = FXCollections.observableArrayList(
    new Visitor(1, "Donald and Kamala", "09:30", "10:15", "Sophia"),
    new Visitor(2, "TotalRandomPerson", "10:10", "10;40", "SupernumeraryRole"),
    new Visitor(3, "RandomPerson2", "12:10", "12:15", "Sophia"),
    new Visitor(4, "Donald", "13:15", "14:50", "Sophia")
  );

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

id.setCellValueFactory(new PropertyValueFactory<Visitor, Integer>("id"));
visitorName.setCellValueFactory(new PropertyValueFactory<Visitor, String>("name"));
checkinTime.setCellValueFactory(new PropertyValueFactory<Visitor, String>("checkinTime"));
checkoutTime.setCellValueFactory(new PropertyValueFactory<Visitor, String>("checkoutTime"));
hostName.setCellValueFactory(new PropertyValueFactory<Visitor, String>("host"));


    table.setItems(list);
  }

  @FXML
  void onSubmit(ActionEvent event) {
    ObservableList<Visitor> currentTableData = table.getItems();
    int currentID = Integer.parseInt(inputId.getText());

    for (Visitor visitor : currentTableData){
      if(visitor.getId() == currentID){
        visitor.setName(inputName.getText());
        visitor.setCheckinTime(inputCheckinTime.getText());
        visitor.setCheckoutTime(inputCheckoutTime.getText());
        visitor.setHost(inputHost.getText());

        table.setItems(currentTableData);
        table.refresh();
        break;
      }
    }
  }

@FXML
void onRowClicked(MouseEvent event){
  Visitor clickedVisitor = table.getSelectionModel().getSelectedItem();
  inputId.setText(String.valueOf(clickedVisitor.getId()));
  inputName.setText(String.valueOf(clickedVisitor.getName()));
  inputCheckinTime.setText(String.valueOf(clickedVisitor.getCheckinTime()));
  inputCheckoutTime.setText(String.valueOf(clickedVisitor.getCheckoutTime()));
  inputHost.setText(String.valueOf(clickedVisitor.getHost()));
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

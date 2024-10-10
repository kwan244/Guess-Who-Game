package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;

/**
 * Controller class for managing the password-protected visitor log view. This class handles
 * interactions related to visitor information input, updates to the table view, and password-based
 * access control for sensitive areas of the application.
 */
public class PasswordController implements TimerListener {

  @FXML private Label timerLabel; // Label to display the countdown timer
  @FXML private SharedTimer sharedTimer; // Shared timer object for timing operations
  @FXML private TextField txtInput; // TextField for password input
  @FXML private TextField txtUserName; // TextField for username input
  @FXML private TableView<Visitor> table; // TableView to display visitor information
  @FXML private Text iconSubtitle; // Text field for displaying subtitle information
  @FXML private Text passwordHint; // Text field to display password hints
  @FXML private ImageView visitorLogIcon; // Icon to access visitor log view
  @FXML private Rectangle hintBackground; // Background for the password hint
  @FXML private TableColumn<Visitor, Integer> id; // Table column for visitor ID
  @FXML private TableColumn<Visitor, String> checkinTime; // Table column for check-in time
  @FXML private TableColumn<Visitor, String> checkoutTime; // Table column for check-out time
  @FXML private TableColumn<Visitor, String> hostName; // Table column for host name
  @FXML private TableColumn<Visitor, String> visitorName; // Table column for visitor name
  @FXML private Rectangle window; // Visual element for window frame
  @FXML private VBox inputVbox; // VBox container for input fields

  // TextField objects for user inputs
  @FXML private TextField inputId;
  @FXML private TextField inputName;
  @FXML private TextField inputCheckinTime;
  @FXML private TextField inputCheckoutTime;
  @FXML private TextField inputHost;

  // Observable list to hold visitor data for TableView
  ObservableList<Visitor> list =
      FXCollections.observableArrayList(
          new Visitor(1, "Donald and Kamala", "09:30", "10:15", "Sophia"),
          new Visitor(2, "TotalRandomPerson", "10:10", "10;40", "SupernumeraryRole"),
          new Visitor(3, "RandomPerson2", "12:10", "12:15", "Sophia"),
          new Visitor(4, "Donald", "13:15", "14:50", "Sophia"));

  /**
   * This method is called when the timer finishes. It navigates the application to the guess view
   * when the timer reaches zero.
   */
  @Override
  public void onTimerFinished() {
    try {
      Stage currentStage = (Stage) timerLabel.getScene().getWindow();
      App.openGuess(currentStage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the PasswordController by setting up the shared timer and configuring the TableView
   * columns. This method is automatically called after the FXML file has been loaded.
   *
   * @throws ApiProxyException if an error occurs when interacting with the API
   */
  public void initialize() throws ApiProxyException {
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();

    // Set up the columns in the TableView using PropertyValueFactory
    id.setCellValueFactory(new PropertyValueFactory<Visitor, Integer>("id"));
    visitorName.setCellValueFactory(new PropertyValueFactory<Visitor, String>("name"));
    checkinTime.setCellValueFactory(new PropertyValueFactory<Visitor, String>("checkinTime"));
    checkoutTime.setCellValueFactory(new PropertyValueFactory<Visitor, String>("checkoutTime"));
    hostName.setCellValueFactory(new PropertyValueFactory<Visitor, String>("host"));

    // Populate the TableView with initial data
    table.setItems(list);
  }

  /**
   * Handles the submit button click event. It updates the table with the new visitor information or
   * adds a new visitor to the list if the ID is not found.
   *
   * @param event the action event triggered by clicking the submit button
   */
  @FXML
  void onSubmit(ActionEvent event) {
    ObservableList<Visitor> currentTableData = table.getItems();
    int currentID;

    // Try to parse the ID from the input field to update an existing Visitor or create a new one
    try {
      currentID = Integer.parseInt(inputId.getText());

      // If ID is valid, update the existing Visitor
      for (Visitor visitor : currentTableData) {
        if (visitor.getId() == currentID) {
          visitor.setName(inputName.getText());
          visitor.setCheckinTime(inputCheckinTime.getText());
          visitor.setCheckoutTime(inputCheckoutTime.getText());
          visitor.setHost(inputHost.getText());
          table.setItems(currentTableData); // Update the TableView data
          table.refresh(); // Refresh the TableView to show updated data
          return; // Exit after updating
        }
      }

    } catch (NumberFormatException e) {
      // If ID is not a number, create a new Visitor with a unique ID
      int newId = currentTableData.size() + 1;
      inputId.setText(String.valueOf(newId)); // Set the new ID to the input field

      // Create a new Visitor with the given inputs
      Visitor newVisitor =
          new Visitor(
              newId,
              inputName.getText(),
              inputCheckinTime.getText(),
              inputCheckoutTime.getText(),
              inputHost.getText());

      // Add the new Visitor to the list and update the TableView
      currentTableData.add(newVisitor);
      table.setItems(currentTableData); // Update the TableView
      return; // Exit after adding the new Visitor
    }
  }

  /**
   * Handles the event when a row in the TableView is clicked. It populates the input fields with
   * the data from the selected row.
   *
   * @param event the mouse event triggered by clicking a row in the TableView
   */
  @FXML
  void onRowClicked(MouseEvent event) {
    Visitor clickedVisitor = table.getSelectionModel().getSelectedItem();
    if (clickedVisitor != null) {
      // Populate input fields with data from the selected visitor
      inputId.setText(String.valueOf(clickedVisitor.getId()));
      inputName.setText(clickedVisitor.getName());
      inputCheckinTime.setText(clickedVisitor.getCheckinTime());
      inputCheckoutTime.setText(clickedVisitor.getCheckoutTime());
      inputHost.setText(clickedVisitor.getHost());
    } else {
      clearInputFields(); // Clear input fields if no row is selected
    }
  }

  /** Clears all input fields in the form. */
  private void clearInputFields() {
    inputId.clear();
    inputName.clear();
    inputCheckinTime.clear();
    inputCheckoutTime.clear();
    inputHost.clear();
  }

  /** Stops the timer. This method can be called to stop the timer when it is no longer needed. */
  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop(); // Stop the shared timer
    }
  }

  /**
   * Handles the key released event. This method is used to validate the password input and unlock
   * features if the correct password is entered.
   *
   * @param event the key event triggered when a key is released in the input field
   * @throws ApiProxyException if an error occurs with the API proxy
   * @throws IOException if an I/O error occurs
   */
  @FXML
  public void onKeyReleased(KeyEvent event1) throws ApiProxyException, IOException {
    if (event1.getCode() == KeyCode.ENTER) {
      String message = txtInput.getText().trim();
      if (message.isEmpty()) {
        return;
      } else if (message.equals("14101994")) {
        // Correct password entered, unlock visitor log view
        txtUserName.setVisible(false);
        txtInput.setVisible(false);
        passwordHint.setVisible(false);
        visitorLogIcon.setVisible(true);
        iconSubtitle.setVisible(true);
        hintBackground.setVisible(false);
      } else {
        // Display incorrect password message
        txtInput.setText("Incorrect Password! Please Try Again!");
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> txtInput.clear());
        pause.play();
      }
    }
  }

  /**
   * Handles the event when the visitor log icon is clicked. This event displays the visitor log
   * table and additional input fields.
   *
   * @param event the mouse event triggered by clicking the visitor log icon
   * @throws ApiProxyException if an error occurs with the API proxy
   * @throws IOException if an I/O error occurs
   */
  @FXML
  public void onIconClicked(MouseEvent event) throws ApiProxyException, IOException {
    table.setVisible(true); // Show the TableView
    visitorLogIcon.setDisable(true); // Disable the visitor log icon
    inputVbox.setVisible(true); // Show input fields
    window.setVisible(true); // Show window background
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error during the view transition
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }
}

package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class ComputerController {
  @FXML private ImageView LeftRedGlow;
  @FXML private ImageView RightRedGlow;
  @FXML private ImageView LeftBlackGlow;
  @FXML private ImageView RightBlackGlow;
  @FXML private ImageView LeftYellowGlow;
  @FXML private ImageView RightYellowGlow;
  @FXML private ImageView glove;
  @FXML private ImageView YellowConnect;
  @FXML private ImageView RedConnect;
  @FXML private ImageView BlackConnect;
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

  public void initialize() throws ApiProxyException {
    //
    glove.setOnMouseClicked(this::handleGloveClick);
    LeftBlackGlow.setOnMouseClicked(this::handleWireClick);
    RightBlackGlow.setOnMouseClicked(this::handleWireClick);
    LeftRedGlow.setOnMouseClicked(this::handleWireClick);
    RightRedGlow.setOnMouseClicked(this::handleWireClick);
    LeftYellowGlow.setOnMouseClicked(this::handleWireClick);
    RightYellowGlow.setOnMouseClicked(this::handleWireClick);
    LeftRedGlow.setVisible(false);
    RightRedGlow.setVisible(false);
    LeftBlackGlow.setVisible(false);
    RightBlackGlow.setVisible(false);
    LeftYellowGlow.setVisible(false);
    RightYellowGlow.setVisible(false);
  }

  private void handleGloveClick(MouseEvent event) {
    glovewearing = true;
    glove.setStyle("-fx-effect:null;");
    LeftRedGlow.setVisible(true);
    RightRedGlow.setVisible(true);
    LeftBlackGlow.setVisible(true);
    RightBlackGlow.setVisible(true);
    LeftYellowGlow.setVisible(true);
    RightYellowGlow.setVisible(true);
  }

  private void handleWireClick(MouseEvent event) {
    // if glove is clicked and wires are not connected
    if (glovewearing && wireCount < 2) {
      // Each action for each colour wire
      if (event.getSource().equals(LeftRedGlow)) {
        wireR1 = true;
        LeftRedGlow.setVisible(false);
        wireCount++;
        // check same colour wires is connected
        if (wireR1 && wireR2) {
          // correct wire connection
          // show correct wire connection
          redConnected = true;
          RedConnect.setVisible(true);
          checkAllConnected();
        }
        checkWireCount();
      } else if (event.getSource().equals(RightRedGlow)) {
        wireR2 = true;
        RightRedGlow.setVisible(false);
        wireCount++;
        // Check same colour wires is connected
        if (wireR1 && wireR2) {
          // correct wire connection
          // show correct wire connection
          redConnected = true;
          RedConnect.setVisible(true);
          checkAllConnected();
        }
        checkWireCount();
      } else if (event.getSource().equals(LeftBlackGlow)) {
        wireB1 = true;
        LeftBlackGlow.setVisible(false);
        wireCount++;
        // check same colour wires is connected
        if (wireB1 && wireB2) {
          // correct wire connection
          // show correct wire connection
          blackConnected = true;
          BlackConnect.setVisible(true);
          checkAllConnected();
        }
        checkWireCount();
      } else if (event.getSource().equals(RightBlackGlow)) {
        wireB2 = true;
        RightBlackGlow.setVisible(false);
        wireCount++;
        // check same colour wires is connected
        if (wireB1 && wireB2) {
          // correct wire connection
          // show correct wire connection
          blackConnected = true;
          BlackConnect.setVisible(true);
          checkAllConnected();
        }
        checkWireCount();
      } else if (event.getSource().equals(LeftYellowGlow)) {
        wireY1 = true;
        LeftYellowGlow.setVisible(false);
        wireCount++;
        // check same colour wires is connected
        if (wireY1 && wireY2) {
          // correct wire connection
          // show correct wire connection
          yellowConnected = true;
          YellowConnect.setVisible(true);
          checkAllConnected();
        }
        checkWireCount();
      } else if (event.getSource().equals(RightYellowGlow)) {
        wireY2 = true;
        RightYellowGlow.setVisible(false);
        wireCount++;
        // check same colour wires is connected
        if (wireY1 && wireY2) {
          // correct wire connection
          // show correct wire connection
          yellowConnected = true;
          YellowConnect.setVisible(true);
          checkAllConnected();
          wireCount = 0;
        }
        checkWireCount();
      }
    }
  }

  private void checkAllConnected() {
    if (redConnected && blackConnected && yellowConnected) {
      // all wires connected
      // show clue
    }
  }

  private void checkWireCount() {
    if (wireCount == 2) {
      wireCount = 0;
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

package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class ShoeprintController {

  @FXML private ImageView Shoeprint;
  @FXML private ImageView Magnifier;
  private int ShoeprintClick = 0;
  private int MagnifierClick = 0;

  // private final String Shoeprints = "/images/Shoeprint.jpg";

  @FXML
  public void initialize() {
    Magnifier.setOnMouseEntered(this::onMagnifierHover);
    Magnifier.setOnMouseExited(this::onMagnifierExit);
    Magnifier.setOnMousePressed(this::handleMagnifierClick);

    Shoeprint.setOnMouseEntered(this::onShoeprintHover);
    Shoeprint.setOnMouseExited(this::onShoeprintExit);
    Shoeprint.setOnMousePressed(this::handleShoeprintClick);
  }

  @FXML
  private void handleShoeprintClick(MouseEvent event) {
    ShoeprintClick++;
    System.out.println("Shoeprint clicked " + ShoeprintClick + " times.");
    // Add your logic here for handling shoeprint click
  }

  @FXML
  private void handleMagnifierClick(MouseEvent event) {
    Magnifier.setCursor(Cursor.CLOSED_HAND);
    MagnifierClick++;
    System.out.println("Magnifier clicked " + MagnifierClick + " times.");
    // Add your logic here for handling magnifier click
  }

  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }

  @FXML
  private void onShoeprintHover(MouseEvent event) {
    Shoeprint.setCursor(Cursor.OPEN_HAND);
    Shoeprint.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  @FXML
  private void onShoeprintExit(MouseEvent event) {
    Shoeprint.setCursor(Cursor.DEFAULT);
    Shoeprint.setStyle("-fx-effect: null;");
  }

  @FXML
  private void onMagnifierHover(MouseEvent event) {
    Magnifier.setCursor(Cursor.OPEN_HAND);
    Magnifier.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  @FXML
  private void onMagnifierExit(MouseEvent event) {
    Magnifier.setCursor(Cursor.DEFAULT);
    Magnifier.setStyle("-fx-effect: null;");
  }
}

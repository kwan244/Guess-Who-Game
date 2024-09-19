package nz.ac.auckland.se206.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javazoom.jl.player.Player;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class GuessController implements TimerListener {

  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private ImageView WinLoseImage;
  @FXML private ImageView FemaleImage;
  @FXML private ImageView MaleImage;
  @FXML private ImageView ManagerImage;
  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;

  private ChatCompletionRequest chatCompletionRequest;
  private String profession;
  private boolean isGuessed = false;
  private final String[] WinLoseImages = {
    "/images/you_win.png", "/images/you_lose.png",
  };

  @Override
  public void onTimerFinished() {}

  @FXML
  public void initialize() {
    WinLoseImage.setOpacity(0);
    FemaleImage.setOnMouseEntered(this::handleMouseEnterFemale);
    MaleImage.setOnMouseEntered(this::handleMouseEnterMale);
    ManagerImage.setOnMouseEntered(this::handleMouseEnterManager);
    FemaleImage.setOnMouseExited(this::handleMouseExitFemale);
    MaleImage.setOnMouseExited(this::handleMouseExitMale);
    ManagerImage.setOnMouseExited(this::handleMouseExitManager);

    handleGuess();

    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();
  }

  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop();
    }
  }

  private void handleGuess() {
    MaleImage.setOnMouseClicked(event -> handleGuessMale(event));
    ManagerImage.setOnMouseClicked(event -> handleGuessManager(event));
    FemaleImage.setOnMouseClicked(event -> handleGuessFemale(event));
  }

  private void handleGuessMale(MouseEvent event) {
    if (!isGuessed) {
      WinLoseImage.setImage(new Image(getClass().getResourceAsStream(WinLoseImages[0])));
      WinLoseImage.setOpacity(1);
      isGuessed = true;
    } else {
      playAudio("Guessed");
    }
  }

  private void handleGuessManager(MouseEvent event) {
    if (!isGuessed) {
      WinLoseImage.setImage(new Image(getClass().getResourceAsStream(WinLoseImages[1])));
      WinLoseImage.setOpacity(1);
      isGuessed = true;
    } else {
      playAudio("Guessed");
    }
  }

  private void handleGuessFemale(MouseEvent event) {
    if (!isGuessed) {
      WinLoseImage.setImage(new Image(getClass().getResourceAsStream(WinLoseImages[1])));
      WinLoseImage.setOpacity(1);
      isGuessed = true;
    } else {
      playAudio("Guessed");
    }
  }

  @FXML
  private void onPlayAgain(ActionEvent event) throws IOException {
    GuessCondition.INSTANCE.setComputerClicked(false);
    GuessCondition.INSTANCE.setShoeprintClicked(false);
    GuessCondition.INSTANCE.setPaperClicked(false);
    GuessCondition.INSTANCE.setManagerClicked(false);
    GuessCondition.INSTANCE.setThiefClicked(false);
    GuessCondition.INSTANCE.setFemaleCustomerClicked(false);
    RoomController.context.setState(RoomController.context.getGameStartedState());
    RoomController.isFirstTimeInit = true;
    App.setRoot("CrimeScene");
  }

  private void handleMouseEnterFemale(MouseEvent event) {
    FemaleImage.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  private void handleMouseEnterMale(MouseEvent event) {
    MaleImage.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  private void handleMouseEnterManager(MouseEvent event) {
    ManagerImage.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  private void handleMouseExitFemale(MouseEvent event) {
    FemaleImage.setStyle("-fx-effect: null;");
  }

  private void handleMouseExitMale(MouseEvent event) {
    MaleImage.setStyle("-fx-effect: null;");
  }

  private void handleMouseExitManager(MouseEvent event) {
    ManagerImage.setStyle("-fx-effect: null;");
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", profession);
    return PromptEngineering.getPrompt("/prompts/" + profession + ".txt", map);
  }

  /**
   * Sets the profession for the chat context and initializes the ChatCompletionRequest.
   *
   * @param profession the profession to set
   */
  public void setProfession(String profession) {
    this.profession = profession;
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
      runGpt(new ChatMessage("system", getSystemPrompt()));
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    txtaChat.appendText(profession + ": " + msg.getContent() + "\n\n");
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    if (chatCompletionRequest == null) {
      throw new IllegalStateException(
          "ChatCompletionRequest is not initialized. Make sure to call setProfession first.");
    }
    chatCompletionRequest.addMessage(msg);
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    String message = txtInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    txtInput.clear();
    ChatMessage msg = new ChatMessage("player", message);
    appendChatMessage(msg);
    runGpt(msg);
  }

  private void playAudio(String mp3FilePath) {
    try {
      FileInputStream fileInputStream =
          new FileInputStream("src/main/resources/sounds/" + mp3FilePath + ".mp3");
      Player player = new Player(fileInputStream);
      player.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

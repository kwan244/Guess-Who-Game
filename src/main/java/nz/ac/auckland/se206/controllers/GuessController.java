package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class GuessController {
  @FXML private ImageView WinLoseImage;
  @FXML private ImageView FemaleImage;
  @FXML private ImageView MaleImage;
  @FXML private ImageView ManagerImage;
  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;

  private ChatCompletionRequest chatCompletionRequest;
  private String profession;
  private final String[] WinLoseImages = {
    "/images/you_win.png", "/images/you_lose.png",
  };

  @FXML
  public void initialize() {
    WinLoseImage.setOpacity(0);
    FemaleImage.setOnMouseEntered(this::handleMouseEnterFemale);
    // MaleImage.setOnMouseEntered(this::handleMouseEnter);
    // ManagerImage.setOnMouseEntered(this::handleMouseEnter);
    FemaleImage.setOnMouseExited(this::handleMouseExitFemale);

    handleGuess();
  }

  private void handleGuess() {
    MaleImage.setOnMouseClicked(event -> handleGuessMale(event));
  }

  private void handleGuessMale(MouseEvent event) {
    WinLoseImage.setImage(new Image(getClass().getResourceAsStream(WinLoseImages[0])));
    WinLoseImage.setOpacity(1);
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

  private void handleMouseEnterFemale(MouseEvent event) {
    FemaleImage.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  private void handleMouseExitFemale(MouseEvent event) {
    FemaleImage.setStyle("-fx-effect: null;");
  }
}

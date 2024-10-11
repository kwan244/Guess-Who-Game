package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;
import nz.ac.auckland.se206.prompts.PromptEngineering;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController {

  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;
  @FXML private Label timerLabel;
  @FXML private ProgressIndicator progressIndicator;

  private SharedTimer sharedTimer;

  private ChatCompletionRequest chatCompletionRequest;
  private String profession;

  /**
   * Initializes the chat view.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {
    // Any required initialization code can be placed here
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(
        new TimerListener() {
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
        });
    sharedTimer.start();
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", profession);
    return PromptEngineering.getPrompt("prompts/" + profession + ".txt", map);
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
      runGptAsync(new ChatMessage("system", getSystemPrompt()));
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
    String sender;
    if (msg.getRole().equals("user")) {
      sender = "You";
    } else {
      if (this.profession.equals("FemaleCustomer")) {
        sender = "Kamala";
      } else if (this.profession.equals("MaleCustomer")) {
        sender = "Donald";
      } else {
        sender = "Sophia";
      }
    }
    Platform.runLater(() -> txtaChat.appendText(sender + ": " + msg.getContent() + "\n\n"));
  }

  /**
   * Runs the GPT model asynchronously with a given chat message using CompletableFuture.
   *
   * @param msg the chat message to process
   */
  private void runGptAsync(ChatMessage msg) {
    progressIndicator.setVisible(true); // Show the loading indicator
    // Add the message to the request
    chatCompletionRequest.addMessage(msg);

    // Run the request asynchronously to avoid blocking the UI thread
    CompletableFuture.supplyAsync(
            () -> {
              try {
                // Execute the request and get the result
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                return chatCompletionResult.getChoices().iterator().next();
              } catch (ApiProxyException e) {
                e.printStackTrace();
                return null;
              }
            })
        .thenAccept(
            choice -> {
              if (choice != null) {
                // Append chat message on the UI thread
                Platform.runLater(
                    () -> {
                      chatCompletionRequest.addMessage(choice.getChatMessage());
                      appendChatMessage(choice.getChatMessage());
                      progressIndicator.setVisible(false); // Hide the loading indicator
                    });
              }
            });
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
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    runGptAsync(msg);
  }

  public void sendMessage() throws ApiProxyException, IOException {
    onSendMessage(null);
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

  @FXML
  private void handleFemaleRoomClicked(MouseEvent event) {
    try {
      GuessCondition.INSTANCE.setFemaleCustomerClicked(true);
      App.openChat(event, "FemaleCustomer");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleMaleRoomClicked(MouseEvent event) {
    try {
      GuessCondition.INSTANCE.setThiefClicked(true);
      App.openChat(event, "MaleCustomer");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleManagerOfficeClicked(MouseEvent event) {
    try {
      GuessCondition.INSTANCE.setManagerClicked(true);
      App.openChat(event, "Thief");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {}

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) throws ApiProxyException, IOException {
    if (event.getCode() == KeyCode.ENTER) {
      sendMessage();
    }
  }
}

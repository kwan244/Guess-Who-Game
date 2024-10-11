package nz.ac.auckland.se206;

import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;

public class GptHelper {

  /**
   * Runs the GPT model asynchronously with a given chat message using CompletableFuture.
   *
   * @param msg the chat message to process
   * @param chatCompletionRequest the request object for chat completion
   * @param progressIndicator the progress indicator to show loading status
   * @param appendChatMessage a callback to append the chat message on the UI
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  public static void runGptAsync(
      ChatMessage msg,
      ChatCompletionRequest chatCompletionRequest,
      ProgressIndicator progressIndicator,
      AppendChatCallback appendChatMessage) {

    // Set progress indicator to visible
    progressIndicator.setVisible(true);
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
                      appendChatMessage.appendMessage(choice.getChatMessage());
                      progressIndicator.setVisible(false); // Hide the loading indicator
                    });
              }
            });
  }

  /**
   * Runs the initial GPT message asynchronously.
   *
   * @param systemMessage the system message to send
   * @param chatCompletionRequest the request object for chat completion
   * @param progressIndicator the progress indicator to show loading status
   * @param appendChatMessage a callback to append the chat message on the UI
   */
  public static void runInitialGptMessage(
      ChatMessage systemMessage,
      ChatCompletionRequest chatCompletionRequest,
      ProgressIndicator progressIndicator,
      AppendChatCallback appendChatMessage) {

    // Use the helper to run GPT asynchronously
    runGptAsync(systemMessage, chatCompletionRequest, progressIndicator, appendChatMessage);
  }

  /** Callback interface for appending chat messages */
  public interface AppendChatCallback {
    void appendMessage(ChatMessage message);
  }
}

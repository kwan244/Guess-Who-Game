package nz.ac.auckland.se206.controllers;

import java.io.FileInputStream;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.player.Player;

public abstract class BaseController {

  protected MediaPlayer mediaPlayer;
  protected Player mp3Player; // 用于播放 MP3 文件
  protected boolean isAudioPlaying = false;

  @FXML protected ImageView audioImage; // 音频状态图标

  // 音频图标图片
  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage = new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  /**
   * 初始化控制器。主要处理音频图标的更新。
   */
  @FXML
  public void initialize() {
    updateMuteImage();
  }

  /**
   * 更新音频图标的显示状态。
   */
  protected void updateMuteImage() {
    if (!AudioStatus.INSTANCE.isMuted()) {
      audioImage.setImage(soundOnImage); // 显示开启音频图标
    } else {
      audioImage.setImage(soundOffImage); // 显示关闭音频图标
    }
  }

  /**
   * 切换静音状态并更新图标。
   */
  @FXML
  protected void handleToggleSpeech() {
    boolean currentStatus = AudioStatus.INSTANCE.isMuted();
    AudioStatus.INSTANCE.setMuted(!currentStatus);
    updateMuteImage();
    toggleAudioMute();
  }

  /**
   * 静音或取消静音当前播放的音频。
   */
  protected void toggleAudioMute() {
    if (mediaPlayer != null) {
      mediaPlayer.setMute(AudioStatus.INSTANCE.isMuted());
    }
    if (mp3Player != null && AudioStatus.INSTANCE.isMuted()) {
      mp3Player.close();
    }
  }

  /**
   * 播放指定的 MP3 文件。
   *
   * @param mp3FilePath 不包含扩展名的文件路径。
   */
  protected void playAudio(String mp3FilePath) {
    if (AudioStatus.INSTANCE.isMuted() || isAudioPlaying) {
      return;
    }

    isAudioPlaying = true;

    try {
      FileInputStream fileInputStream = new FileInputStream("src/main/resources/sounds/" + mp3FilePath + ".mp3");
      mp3Player = new Player(fileInputStream);

      new Thread(() -> {
        try {
          mp3Player.play(); 
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          isAudioPlaying = false; 
        }
      }).start();
    } catch (Exception e) {
      e.printStackTrace();
      isAudioPlaying = false;
    }
  }

  /**
   * 停止当前播放的音频。
   */
  protected void stopAudio() {
    if (mediaPlayer != null) {
      mediaPlayer.stop(); // 停止 MediaPlayer 播放
      mediaPlayer = null;
    }
    if (mp3Player != null) {
      mp3Player.close(); // 关闭 MP3 Player
      mp3Player = null;
    }
    isAudioPlaying = false;
  }
}

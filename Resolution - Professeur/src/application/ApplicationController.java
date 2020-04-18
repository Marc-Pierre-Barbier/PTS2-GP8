package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

public class ApplicationController extends Main{
	
	@FXML
	private Slider volume;
	@FXML
	private MediaView mediaView;
	@FXML
	private Button interactionVideoBtn;
	
	private boolean videoCharger = false;
	
	public void nouvelleExercice() throws IOException {
		System.out.println("Création d'un exercice");
		super.setHauteur(720);
		super.setLargeur(910);
		System.out.println("Titre : " + super.getStage().getTitle());
		super.chargerUnePage("NouvelleExercice.fxml");
	}
	
	public void chargerUneVideo() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp4", "*.mp4"), new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("All", "*"));
		fileChooser.setTitle("Ouvrir une vidéo/audio");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(super.getStage());
		if (selectedFile != null) {
			System.out.println(selectedFile.getAbsolutePath());
			Media media = new Media(new File(selectedFile.getAbsolutePath()).toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			mediaView.setVisible(true);
			volume.setValue(mediaPlayer.getVolume()*100);
			volume.valueProperty().addListener(new InvalidationListener() {
				
				@Override
				public void invalidated(javafx.beans.Observable observable) {
						mediaPlayer.setVolume(volume.getValue() / 100);			
				}
			});
			videoCharger = true;
		}
	}
	
	public void interactionVideo() {
		if(videoCharger) {
			if(interactionVideoBtn.getText().equalsIgnoreCase("Jouer")) {
				mediaView.getMediaPlayer().pause();
				interactionVideoBtn.setText("Pause");
			}else {
				mediaView.getMediaPlayer().play();
				interactionVideoBtn.setText("Jouer");
			}
		}
	}
	
	public void stopVideo() {
		interactionVideoBtn.setText("Jouer");
		mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStopTime());
		mediaView.getMediaPlayer().stop();
	}


	
	public void chargerExercice() {
		System.out.println("Chargement d'un exercice");
	}
	
	public int onSelectNumberSection() {
		return 0;
	}
	
}

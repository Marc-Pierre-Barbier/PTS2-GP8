package application;

import java.io.File;
import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

public class ApplicationController extends Main{
	
	private final String DEFAULT_EXTENSION = ".res";
	private final String DEFAULT_NAME_EXTENSION = "Résolution";
	
	@FXML
	private TextField titre;
	@FXML
	private TextArea texte;
	@FXML
	private TextArea aide;
	@FXML
	private CheckBox sensibiliteCase;
	@FXML
	private RadioButton modeApprentissage;
	@FXML
	private CheckBox motIncomplet;
	@FXML
	private CheckBox affichageSolution;
	@FXML
	private RadioButton modeEvaluation;
	@FXML
	private TextField consigne;
	@FXML
	private Slider volume;
	@FXML
	private MediaView mediaView;
	@FXML
	private Button interactionVideoBtn;
	@FXML
	private Label aucuneVideoChargee;
	
	private boolean videoChargee = false;
	
	public void nouvelleExercice() throws IOException {
		System.out.println("Création d'un exercice");
		super.setHauteur(720);
		super.setLargeur(910);
		super.chargerUnePage("NouvelleExercice.fxml");
	}
	
	public void chargerUneVideo() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp4", "*.mp4"), new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("All", "*"));
		fileChooser.setTitle("Ouvrir une vidéo/audio");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(super.getStage());
		if (selectedFile != null) {
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
			aucuneVideoChargee.setVisible(false);
			mediaView.setFitHeight(300);
			videoChargee = true;
		}
	}
	
	public void interactionVideo() {
		if(videoChargee) {
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
	
	public void sauvegarderExercice() {
		final FileChooser dialog = new FileChooser(); 
		dialog.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Fichiers " + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION)); 
	    final File file = dialog.showSaveDialog(super.getStage()); 
	    if (file != null) { 
	    	if(videoChargee) {
	    		JsonController.JSONCreation(file.getAbsoluteFile().toString(), titre.getText(), texte.getText(), aide.getText(), sensibiliteCase.isSelected(), modeApprentissage.isSelected(), motIncomplet.isSelected(), affichageSolution.isSelected(), modeEvaluation.isSelected(), consigne.getText(), mediaView.getMediaPlayer().getMedia().getSource().toString(), 0, 0);
	    	}else {
	    		JsonController.JSONCreation(file.getAbsoluteFile().toString(), titre.getText(), texte.getText(), aide.getText(), sensibiliteCase.isSelected(), modeApprentissage.isSelected(), motIncomplet.isSelected(), affichageSolution.isSelected(), modeEvaluation.isSelected(), consigne.getText(), null, 0, 0);
	    	}
	    }
	}


	
	public void chargerExercice() {
		System.out.println("Chargement d'un exercice");
	}
	
	public int onSelectNumberSection() {
		return 0;
	}
	
}

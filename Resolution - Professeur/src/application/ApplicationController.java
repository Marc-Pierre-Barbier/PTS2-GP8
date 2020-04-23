package application;

import java.io.File;
import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.util.Duration;

public class ApplicationController extends Main{
	
	private final String DEFAULT_EXTENSION = ".res";
	private final String DEFAULT_NAME_EXTENSION = "R�solution";	
	
	@FXML
	private TextField titre;
	@FXML
	private TextField timefieldh;
	@FXML
	private TextField timefieldm;
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
	private CheckBox checklimite;
	private boolean checklimitestatus=true;
	@FXML
	private RadioButton modeEvaluation;
	@FXML
	private TextField consigne;
	@FXML
	private Slider volume;
	@FXML
	private Slider progression;
	@FXML
	private MediaView mediaView;
	@FXML
	private Button interactionVideoBtn;
	@FXML
	private Label aucuneVideoChargee;
	@FXML
	private MediaPlayer mediaPlayer;
	@FXML
	private Button chosevid;
	
	private boolean videoChargee = false;
	
	public void nouvelleExercice() throws IOException {
		System.out.println("Cr�ation d'un exercice");
		super.setHauteur(720);
		super.setLargeur(910);
		super.chargerUnePage("NouvelleExercice.fxml");
	}
	
	public void chargerUneVideo() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp4", "*.mp4"), new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("All", "*"));
		fileChooser.setTitle("Ouvrir une vid�o/audio");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(super.getStage());
		if (selectedFile != null) {
			Media media = new Media(new File(selectedFile.getAbsolutePath()).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			mediaView.setVisible(true);
			mediaView.setPreserveRatio(false);
			
			volume.setValue(mediaPlayer.getVolume()*100);
			volume.valueProperty().addListener(new InvalidationListener() {
				
				@Override
				public void invalidated(javafx.beans.Observable observable) {
						mediaPlayer.setVolume(volume.getValue() / 100);			
				}
			});			
			aucuneVideoChargee.setVisible(false);
			mediaView.setFitHeight(250);
			videoChargee = true;
			mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
		        if (!progression.isValueChanging() && !progression.isPressed()) {
		        	progression.setValue(newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds()*100);
		        }
		    });
			
			
			progression.setOnMouseReleased(new EventHandler<Event>() {
		        @Override
		        public void handle(Event event) {
		        	double newValue = progression.getValue();
					mediaPlayer.seek(new Duration(1000*(double)newValue/100*mediaPlayer.getTotalDuration().toSeconds()));
		        }
		    });
			
			checklimite.cursorProperty().addListener((obs,oldVal,newVal) -> {
				checklimitestatus=!checklimitestatus;
				timefieldh.setDisable(checklimitestatus);
				timefieldm.setDisable(checklimitestatus);
			});
			
		}
	}
	
	public void interactionVideo() {
		if(videoChargee) {
			if(interactionVideoBtn.getText().equalsIgnoreCase("Pause")) {
				mediaView.getMediaPlayer().pause();
				interactionVideoBtn.setText("Lire");
			}else {
				mediaView.getMediaPlayer().play();
				interactionVideoBtn.setText("Pause");
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

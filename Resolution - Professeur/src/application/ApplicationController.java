package application;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class ApplicationController extends Main{
	
	private final String DEFAULT_EXTENSION = ".res";
	private final String DEFAULT_NAME_EXTENSION = "Résolution";	
	private String time = "00:00:00";
	private List<Section> sections;
	
	@FXML
	private TextField titre;
	@FXML
	private TextField timefieldh;
	@FXML
	private TextField timefieldm;
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
	@FXML
	private RadioButton modeEvaluation;
	@FXML
	private TextField consigne;
	@FXML
	private TabPane sectionsTabPane;
	@FXML
	private TabPane sectionsTimeCodePane;
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
	
	private Tab sectionsTabNEW = new Tab("+");
	private Button btnNewSection = new Button("nouvelle section");
	
	public void nouvelleExercice() throws IOException {
		System.out.println("Création d'un exercice");
		super.setHauteur(720);
		super.setLargeur(910);
		super.chargerUnePage("NouvelleExercice.fxml");
	}
	
	public void chargerUneVideo() {
		sections = new ArrayList<>();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp4", "*.mp4"), new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("All", "*"));
		fileChooser.setTitle("Ouvrir une vidéo/audio");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(super.getStage());
		if (selectedFile != null) {
			Media media = new Media(new File(selectedFile.getAbsolutePath()).toURI().toString());
			System.out.println(selectedFile.getAbsolutePath());
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
			sectionsTabNEW.setContent(btnNewSection);
			btnNewSection.setOnAction(e -> sections.add(new Section(sectionsTabPane,sectionsTimeCodePane)));
			sectionsTabPane.getTabs().add(sectionsTabNEW);
			sections.add(new Section(sectionsTabPane,sectionsTimeCodePane));	
			
			progression.setOnMouseReleased(new EventHandler<Event>() {
		        @Override
		        public void handle(Event event) {
		        	double newValue = progression.getValue();
					mediaPlayer.seek(new Duration(1000*(double)newValue/100*mediaPlayer.getTotalDuration().toSeconds()));
		        }
		    });
		}
	}

	public void timeHandle() {
		timefieldh.setDisable(!checklimite.isSelected());
		timefieldm.setDisable(!checklimite.isSelected());
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
	
	public void handleRadialA() {
		modeApprentissage.setSelected(true);
		modeEvaluation.setSelected(false);
	}
	public void handleRadialE() {
		modeApprentissage.setSelected(false);
		modeEvaluation.setSelected(true);
	}
	
	public void stopVideo() {
		interactionVideoBtn.setText("Jouer");
		mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStopTime());
		mediaView.getMediaPlayer().stop();
	}
	

	public void sauvegarderExercice() {
		if(!checklimite.isSelected())time = "00:00:00";
		else {
			if(timefieldh.getText().length() == 1)timefieldh.setText("0"+timefieldh.getText());
			if(timefieldm.getText().length() == 1)timefieldm.setText("0"+timefieldm.getText());
			if(timefieldh.getText().length() == 0)timefieldh.setText("00");
			if(timefieldm.getText().length() == 0)timefieldm.setText("00");
			if(timefieldm.getText().length() > 2) {
				timefieldm.setStyle("-fx-text-inner-color: red;"); 
				return;
			}
			if(timefieldh.getText().length() > 2) {
				timefieldh.setStyle("-fx-text-inner-color: red;");  
				return;
			}
			time = timefieldh.getText()+":"+timefieldm.getText()+":00";
		}
		
		if(!videoChargee) {
			erreur("aucune video trouver", "vous devez mettre une video avant de sauvegarder");
    		return;
		}
		final FileChooser dialog = new FileChooser(); 
		dialog.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Fichiers " + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION)); 
	    final File file = dialog.showSaveDialog(super.getStage()); 
	    if (file != null) { 
	    	if(videoChargee) {
	    		String videoformat = "";
	    		final int medialength = mediaView.getMediaPlayer().getMedia().getSource().length();
	    		for (int i=medialength-4 ; i<medialength ;i++)videoformat += mediaView.getMediaPlayer().getMedia().getSource().charAt(i); //on sauvegarde que le format car on copie la video avec le fichier pour rendre le tout transportable
	    		JsonController.JSONCreation(fixMyPath(file.getAbsoluteFile().toString(),".res"), titre.getText(), sections, sensibiliteCase.isSelected(), modeApprentissage.isSelected(), motIncomplet.isSelected(), affichageSolution.isSelected(),consigne.getText(), videoformat, time);
	    	
				try {
					File source = new File(new URI(mediaView.getMediaPlayer().getMedia().getSource()));
					File dest = new File(fixMyPath(file.getAbsoluteFile().toString(), videoformat));
					Files.copy(source.toPath(), dest.toPath(),StandardCopyOption.REPLACE_EXISTING);

				} catch (URISyntaxException e1) {
					erreur("erreur URI", "le chemin d'acces de la vidéo est éroné veuiller le redéfinir");
				} catch (IOException e) {
					erreur("erreur copie","une erreur dans la copie est survenue veuiller verifier si vous avez les droit dans le dossier de destination et verifier sur vous avez sufisament d'espace libre (le poids de votre video defini la place nessecaire)");
				}
	    	}/*else {

	    		//je le remplace par un msg d'erreur sa ne fait pas de sens d'enregistrer le doc sans video et par consequent aucune section
	    		//JsonController.JSONCreation(fixMyPath(file.getAbsoluteFile().toString(),".res"), titre.getText(), null, sensibiliteCase.isSelected(), modeApprentissage.isSelected(), motIncomplet.isSelected(), affichageSolution.isSelected(), modeEvaluation.isSelected(), consigne.getText(), null, time);
	    	}*///le msg se trouve avant le file chooser

	    }
	}


	/**
	 * ajoute .res a la fin si il n'est pas deja present
	 * j'ai euh le probleme sur linux ou sa enregistrais sans extention
	 * sa ne deverais pas poser probleme sur windows
	 * j'y ai ajouter aussi la fonction pour renomer les fichier en mp4
	 */
	private String fixMyPath(String chemin, String format) {
		int i = chemin.length();
		String str = "";
		for(int j = i-4 ; j < i ; j++)
			str += chemin.charAt(j);
		
		if(str.equals(".res")) {
			chemin=chemin.replace(".res", format);
			return chemin;
		}else {
			return chemin + format;
		}
		
	}

	public void chargerExercice() {
		System.out.println("Chargement d'un exercice");
	}
	
	public int onSelectNumberSection() {
		return 0;
	}
	
	public static void erreur(String entete,String contenu) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erreur");
		alert.setHeaderText(entete);
		alert.setContentText(contenu);
		alert.showAndWait();
	}
	
}

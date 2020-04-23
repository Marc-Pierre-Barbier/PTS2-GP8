package application;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.util.Timer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class ApplicationController extends Main{
	
	/*10/02/2019 G8 Programmation de l'application VERSION ETUDIANTE*/
	
	private final String DEFAULT_EXTENSION_NAME  = "R�solution";
	private final String DEFAULT_EXTENSION_FILE = ".res";
	private final String CARACTERE_OCULTATION = "*";
	
	@FXML
	private Text titre;
	@FXML
	private TextArea texte;
	@FXML
	private TextArea proposition;
	@FXML
	private TextArea aide;
	@FXML
	private TextField consigne;
	@FXML
	private Slider volume;
	@FXML
	private MediaView mediaView;
	@FXML
	private Button interactionVideoBtn;
	@FXML
	private Text tempsText;
	@FXML
	private Slider progression;
	
	private boolean videoChargee = false;
	private String texteATrouver = "";
	private String texteCache = "";
	private LocalTime tempsTotal = LocalTime.parse("00:01:52");
	public void ouvrirUnExercice() throws ParseException, InterruptedException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Fichier " + DEFAULT_EXTENSION_NAME, "*" + DEFAULT_EXTENSION_FILE));
		fileChooser.setTitle("Ouvrir un fichier de Resolution");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
		File selectedFile = fileChooser.showOpenDialog(Main.stage);
		if(selectedFile != null) {
			System.out.println("Chargement de l'exercice");
			JSONParser parser = new JSONParser();
	        try (Reader reader = new FileReader(selectedFile.getAbsolutePath().toString())) {

	            JSONObject jsonObject = (JSONObject) parser.parse(reader);
	            titre.setText((String) jsonObject.get("titre"));
	            texteATrouver = (String) jsonObject.get("texte");
	            for (int i = 0; i < texteATrouver.length(); i++) {
	            	if(texteATrouver.charAt(i) != ' ') {
	            		texteCache += CARACTERE_OCULTATION;
	            	}else {
	            		texteCache += " ";
	            	}
				}
	            texte.setText(texteCache);
	            consigne.setText((String) jsonObject.get("consigne"));
	            String cheminVideo = (String) jsonObject.get("cheminVideo");
	            String cheminModifie = "";
	            for (int i = 6; i < cheminVideo.length(); i++) {
	            	cheminModifie += cheminVideo.charAt(i);
				}	
	            chargerUneVideo(new File(cheminModifie));
	            String limiteTemps = (String) jsonObject.get("limiteTemps");
	            int hours = Integer.parseInt(limiteTemps.charAt(0) + "");
	            int minutes = Integer.parseInt(limiteTemps.charAt(2) + limiteTemps.charAt(3) + "");
	            demarrerExercice();
	            //byte[] decodedBytes = Base64.getDecoder().decode(Test);
	            //String decodedString = new String(decodedBytes);
	            
	            //System.out.println("Fichier d�cod� : " + decodedString);
	        }catch(IOException e){
	        	
	        }
		}
	}
	
	public void chargerUneVideo(File f) {
		System.out.println("Chemin vid�o : " + f.getAbsolutePath());
		if (f != null) {
			Media media = new Media(new File(f.getAbsolutePath()).toURI().toString());
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
		}
	}
	
	public void stopVideo() {
		interactionVideoBtn.setText("Jouer");
		mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStopTime());
		mediaView.getMediaPlayer().stop();
	}
		
	public void interactionVideo() {
		if(videoChargee) {
			if(interactionVideoBtn.getText().equalsIgnoreCase("Pause")) {
				mediaView.getMediaPlayer().pause();
				interactionVideoBtn.setText("Jouer");
			}else {
				mediaView.getMediaPlayer().play();
				interactionVideoBtn.setText("Pause");
			}
		}
	}
	
	public LocalTime Timer(LocalTime temps) {
		Timer time = new Timer();
		CustomTimer custom = new CustomTimer(temps, tempsText);
		time.schedule(custom, 1000, 1000);
		return custom.getTimeObject();
	}
	
	public void chercherMot() {
		System.out.println("Lancement d'une recherche");
		System.out.println(texteATrouver);
		System.out.println(texteCache.length());
		texteCache += " ";
		for (int i = 0; i < texteCache.length(); i++) {
			if(i + proposition.getText().length() > texteCache.length()) {
			break;
			}
			for (int j = 0; j < proposition.getText().length(); j++) {

				char letter = texteATrouver.charAt(i + j);
				char letterTextTry = proposition.getText().charAt(j);
				if(letter == letterTextTry) {
					
				 if(j == proposition.getText().length() - 1 && texteCache.charAt(i+j+1) == ' '){
					StringBuilder TextAtFoundHideB = new StringBuilder(texteCache);
					for (int k = 0; k < proposition.getText().length(); k++) {
						TextAtFoundHideB.setCharAt(i + k, proposition.getText().charAt(k));
					}
					texteCache = TextAtFoundHideB.toString();
					texte.setText(texteCache);
				 }
				}else {
					break;
				}
			}
		}
	}
		
	
	public void demarrerExercice() {
		System.out.println(tempsText.getText());
		Timer(tempsTotal);
	}
	
	public void quitter() {
		System.exit(0);
	}
	

}

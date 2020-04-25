package application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class ApplicationController extends Main implements KeyListener{
	
	/*10/02/2019 G8 Programmation de l'application VERSION ETUDIANTE*/
	
	private final String DEFAULT_EXTENSION_NAME  = "Résolution";
	private final String DEFAULT_EXTENSION_FILE = ".res";
	private final String CARACTERE_OCULTATION = "*";
	private final String CARACTERE_NON_OCULTER= ";.,!? ";
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
	        try (Reader reader = new FileReader(selectedFile.getAbsolutePath())) {

	            JSONObject jsonObject = (JSONObject) parser.parse(reader);
	            titre.setText((String) jsonObject.get("titre"));
	            texteATrouver = (String) jsonObject.get("texte");
	            for (int i = 0; i < texteATrouver.length(); i++) {
	            	if(!CARACTERE_NON_OCULTER.contains(texteATrouver.charAt(i)+"")) {
	            		texteCache += CARACTERE_OCULTATION;
	            	}else {
	            		texteCache += texteATrouver.charAt(i);
	            	}
				}
	            texte.setText(texteCache);
	            consigne.setText((String) jsonObject.get("consigne"));
	            String formatvideo = (String) jsonObject.get("cheminVideo");
	            tempsTotal = LocalTime.parse((String)jsonObject.get("limiteTemps"));
	            String cheminVideo = selectedFile.getAbsolutePath().replace(".res", formatvideo);
	            
	            System.out.println(cheminVideo);
	            
	            chargerUneVideo(new File(cheminVideo));//File(cheminModifie)
	            
	            String limiteTemps = (String) jsonObject.get("limiteTemps");
	            int hours = Integer.parseInt(limiteTemps.charAt(0) + "");
	            //int minutes = Integer.parseInt(limiteTemps.charAt(2) + limiteTemps.charAt(3) + "");
	            demarrerExercice();

	        }catch(IOException e){
	        	System.out.println("fichier introuvable");
	        } 
		}
	}
	
	public void chargerUneVideo(File f) {
		System.out.println("Chemin vidéo : " + f.getAbsolutePath());
		if (f != null) {
			Media media = new Media(new File(f.getAbsolutePath()).toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(media);
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
		System.out.println(texteCache.length());
		System.out.println(texteATrouver);
		System.out.println(proposition.getText());
		proposition.setText(proposition.getText().trim());//supprime les retour a la ligne dans la recherche car sa casse tout
		//séparation de la fonction pour recherche mot par mots indépendament
		String mot = "";
		String objectif = proposition.getText() + " "; // cette variable permet de memoriser le contenu de la proposition car elle est modifié aprés
		
		for(int i=0 ; i < objectif.length();i++) {
			if(objectif.charAt(i) != ' ') {
				mot += objectif.charAt(i);
			}else {
				if(mot != "") {
					proposition.setText(mot);
					chercherMotSplt();
					mot="";
				}
			}
		}
		proposition.setText("");//on suprime le champs aprés validation
	}
	
	public void chercherMotSplt() {
		texteCache += " ";
		for (int i = 0; i < texteCache.length(); i++) {
			if(i + proposition.getText().length() > texteCache.length()) {
				break;
			}
			for (int j = 0; j < proposition.getText().length(); j++) {
				if( i+j >= texteATrouver.length()) break;
				char letter = texteATrouver.charAt(i+j);
				char letterTextTry = proposition.getText().charAt(j);
				if(letter == letterTextTry) {
					
				 if(j == proposition.getText().length() - 1 && CARACTERE_NON_OCULTER.contains(""+texteCache.charAt(i+j+1))){
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
		
	
	public static void lockAll() {
		for(Node parent : root.getChildren()) {
			VBox vbox = (VBox)parent; //tout les enfant de la gridpane root sont des vbox si on change le fxml sa va tout casser 
			for(Node n : vbox.getChildren()) {
				if(!(n instanceof MenuBar)) {
					n.setDisable(true);
					System.out.println(n);
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

	// le code pour la touche enter
	@Override
	public void keyPressed(KeyEvent key) {
		if(key.equals(KeyEvent.VK_ENTER))chercherMot();
	}
	//osef maison dois le garder pour le implements
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	

}

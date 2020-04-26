package application;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class ApplicationController extends Main{
	
	/*10/02/2019 G8 Programmation de l'application VERSION ETUDIANTE*/
	
	private final String DEFAULT_EXTENSION_NAME  = "Résolution";
	private final String DEFAULT_EXTENSION_FILE = ".res";
	protected static final String CARACTERE_OCULTATION = "*";
	protected static final String CARACTERE_NON_OCULTER= ";.,!? ";
	private boolean sensibiliteCase = false;
	private List<Section> sections;
	@FXML
	private Text titre;
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
	@FXML
	private TabPane TabPaneExo;
	@FXML
	private Button validerbtn;
	@FXML 
	private Button solutionBoutton;
	private boolean solutionVisible = false;
	private String solutionStringMem = "";
	private String CachéStringMem = "";
	private boolean videoChargee = false;
	private int indexSoluce = 0;
	private LocalTime tempsTotal = LocalTime.parse("00:00:00");
	private boolean chronometrer = false;
	private boolean motincomplet = false;
	
	
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
	            consigne.setText((String) jsonObject.get("consigne"));
	            String formatvideo = (String) jsonObject.get("cheminVideo");
	            sensibiliteCase = (boolean) jsonObject.get("sensibiliteCase");
	            String limiteTemps = (String)jsonObject.get("limiteTemps");
	            motincomplet = (boolean)jsonObject.get("motIncomplet");
	            if(!(boolean)jsonObject.get("affichageSolution")) {
	            	solutionBoutton.setDisable(true);
	            }
	            solutionBoutton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						solutionVisible=!solutionVisible;
						validerbtn.setDisable(solutionVisible);
						proposition.setDisable(solutionVisible);
						//TabPaneExo.setDisable(solutionVisible);
						Section s = sections.get(TabPaneExo.getSelectionModel().getSelectedIndex());
						if(solutionVisible) {
							indexSoluce=TabPaneExo.getSelectionModel().getSelectedIndex();
							solutionStringMem = s.getTextATrouver();
							CachéStringMem = s.getTextvideo().getText();
							s.getTextvideo().setText(solutionStringMem);
							solutionStringMem="";
						}else {
							TabPaneExo.getSelectionModel().clearAndSelect(indexSoluce);
							s.getTextvideo().setText(CachéStringMem);
							CachéStringMem="";
						}
					}
				});
	            if(!(limiteTemps.equals("00:00:00"))) {
	            	tempsTotal = LocalTime.parse(limiteTemps);
	            	//int hours = Integer.parseInt(limiteTemps.charAt(0) + limiteTemps.charAt(1) +"");
		            //int minutes = Integer.parseInt(limiteTemps.charAt(3) + limiteTemps.charAt(4) + ""); //le char 0 est les disaines d'eur le char 1 les heure le char 2 le : etc..
	            	chronometrer=true;
	            }else {
	            	tempsTotal = LocalTime.parse("00:00:00");
	            	chronometrer=false;
	            }
	            
	            proposition.setOnKeyPressed(new EventHandler<KeyEvent>() {
	                public void handle(KeyEvent ke) {
	                    if (ke.getCode() == KeyCode.ENTER) {
	                    	chercherMot();
	                    }
	                }
	            });
	            
	            sections = new ArrayList<>();
	            for (int i = 1 ; i<= (long) jsonObject.get("sections");i++) {
	        	   sections.add(new Section(TabPaneExo,(String)jsonObject.get("SectionAide"+i),(String)jsonObject.get("SectionText"+i),(String)jsonObject.get("SectionTimeCode"+i)));
	           }

	            String cheminVideo = selectedFile.getAbsolutePath().replace(".res", formatvideo);
	            
	            System.out.println(cheminVideo);
	            
	            chargerUneVideo(new File(cheminVideo));//File(cheminModifie)

	            demarrerExercice();

	        }catch(IOException e){
	        	System.err.println("fichier introuvable");
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
		Section s = sections.get(TabPaneExo.getSelectionModel().getSelectedIndex());
		
		
		System.out.println("Lancement d'une recherche");
		System.out.println(s.getTextATrouver().length());
		System.out.println(s.getTextATrouver());
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
					chercherMotSplt(s);
					mot="";
				}
			}
		}
		proposition.setText("");//on suprime le champs aprés validation
	}
	
	public void chercherMotSplt(Section s) {
		String texteATrouver = s.getTextATrouver();
		String texteCache = s.getTextvideo().getText()+" ";
		int index=0;
		for(char c : texteATrouver.toCharArray()) {			
			if (proposition.getText().charAt(0) == c && proposition.getText().length() <= (texteATrouver.length() - index)) {
				int indexProp=index;
				if(sensibiliteCase) {
					for(char prop : proposition.getText().toCharArray()) {
						if(!(prop == texteATrouver.charAt(indexProp))) break;
						indexProp++;
					}
				}else {
					for(char prop : proposition.getText().toLowerCase().toCharArray()) {
						if(!(prop == texteATrouver.toLowerCase().charAt(indexProp))) break;
						indexProp++;
					}
				}
				
				if(indexProp==index + proposition.getText().length()) System.out.println("trouvé!");
				else {
					System.out.println("prop : " + indexProp + "\ncalc index : "+(index + proposition.getText().length()));
				}
				
				System.out.println("autorisé les mots incomplets ? : "+motincomplet);
				
				if((indexProp==index + proposition.getText().length() && motincomplet) || 
				//verification pour mots complet(donc caractére non oculter a la fin)
				(!motincomplet && texteCache.length() >= indexProp+1 && 
				CARACTERE_NON_OCULTER.contains(texteCache.charAt(indexProp)+"") && 
				indexProp==index + proposition.getText().length())) {
					System.out.println("im in");
					for(int i = index ; i < indexProp ; i++) {
						char[] chars = texteCache.toCharArray();
						chars[i] = texteATrouver.charAt(i);
						texteCache = String.valueOf(chars);
					}
				}
			}
			index++;
		}
		
		s.getTextvideo().setText(texteCache);
	}
		
	
	public static void lockAll() {
		for(Node parent : root.getChildren()) {
			VBox vbox = (VBox)parent; //tout les enfant de la gridpane root sont des vbox si on change le fxml sa va tout casser 
			for(Node n : vbox.getChildren()) {
				if(!(n instanceof MenuBar)) {
					n.setDisable(true);
				}
			}
		}
	}
	
	public void demarrerExercice() {
		System.out.println(tempsText.getText());
		if(chronometrer)Timer(tempsTotal);
	}
	
	public void quitter() {
		System.exit(0);
	}
}

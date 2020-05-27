package application.control;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.json.simple.parser.ParseException;

import application.vue.Main;
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
import javafx.util.Duration;

public class ApplicationController extends Main {

	/* 10/02/2019 G8 Programmation de l'application VERSION ETUDIANTE */

	protected static final String CARACTERE_OCULTATION = "*";
	protected static final String CARACTERE_NON_OCULTER = ";.,!? ";
	protected static boolean sensibiliteCase = false;
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
	@FXML
	private Button aideBtn;
	protected static boolean aideAutorisation=true;
	private boolean videoChargee = false;
	protected static LocalTime tempsTotal = LocalTime.parse("00:00:00");
	protected static boolean chronometrer = false;
	protected static boolean motincomplet = false;
	CustomTimer custom;

	public void ouvrirUnExercice() throws ParseException, InterruptedException {
		sections = new ArrayList<>();
		String cheminVideo = JsonController.JSONReader(titre, consigne, solutionBoutton, sections, TabPaneExo);
		aideBtn.setDisable(!aideAutorisation);
		solutionBoutton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Section s = sections.get(TabPaneExo.getSelectionModel().getSelectedIndex());
				s.lock();
			}
			
		});
		proposition.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                	chercherMot();
                }
            }
        });
		aideBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Section s = sections.get(TabPaneExo.getSelectionModel().getSelectedIndex());
				s.switchHelpStatus();
			}
		});

		System.out.println(cheminVideo);

		chargerUneVideo(new File(cheminVideo));// File(cheminModifie)

		demarrerExercice();

	}
	

	

	public void chargerUneVideo(File f) {

		Media media = new Media(new File(f.getAbsolutePath()).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);
		mediaView.setVisible(true);
		mediaView.setPreserveRatio(false);
		volume.setValue(mediaPlayer.getVolume() * 100);
		volume.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(javafx.beans.Observable observable) {
				mediaPlayer.setVolume(volume.getValue() / 100);
			}
		});
		videoChargee = true;

		mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
			if (!progression.isValueChanging() && !progression.isPressed()) {
				progression.setValue(newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds() * 100);
			}
		});

		progression.setOnMouseReleased(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				double newValue = progression.getValue();
				mediaPlayer.seek(
						new Duration(1000 * (double) newValue / 100 * mediaPlayer.getTotalDuration().toSeconds()));
			}
		});

	}

	public void stopVideo() {
		interactionVideoBtn.setText("Jouer");
		mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStopTime());
		mediaView.getMediaPlayer().stop();
	}

	public void interactionVideo() {
		if (videoChargee) {
			if (interactionVideoBtn.getText().equalsIgnoreCase("Pause")) {
				mediaView.getMediaPlayer().pause();
				interactionVideoBtn.setText("Jouer");
			} else {
				mediaView.getMediaPlayer().play();
				interactionVideoBtn.setText("Pause");
			}
		}
	}

	public LocalTime Timer(LocalTime temps) {
		Timer time = new Timer();
		custom = new CustomTimer(temps, tempsText);
		time.schedule(custom, 1000, 1000);
		return custom.getTimeObject();
	}

	public void chercherMot() {
		Section s = sections.get(TabPaneExo.getSelectionModel().getSelectedIndex());

		if (s.islocked() || s.isHelp())return; // on le cherche pas si c'est lock ou dans l'aide

		System.out.println("Lancement d'une recherche");
		System.out.println(s.getTextATrouver().length());
		System.out.println(s.getTextATrouver());
		System.out.println(proposition.getText());
		proposition.setText(proposition.getText().trim());// supprime les retour a la ligne dans la recherche car sa
															// casse tout
		// séparation de la fonction pour recherche mot par mots indépendament
		String mot = "";
		String objectif = proposition.getText() + " "; // cette variable permet de memoriser le contenu de la
														// proposition car elle est modifié aprés

		for (int i = 0; i < objectif.length(); i++) {
			if (objectif.charAt(i) != ' ') {
				mot += objectif.charAt(i);
			} else {
				if (mot != "") {
					proposition.setText(mot);
					chercherMotSplt(s);
					mot = "";
				}
			}
		}
		proposition.setText("");// on suprime le champs aprés validation
	}

	public void chercherMotSplt(Section s) {
		String texteATrouver = s.getTextATrouver();
		String texteCache = s.getTexteCache() +" ";
		int index = 0;
		for (char c : texteATrouver.toCharArray()) {
			if (proposition.getText().charAt(0) == c
					&& proposition.getText().length() <= (texteATrouver.length() - index)) {
				int indexProp = index;
				if (sensibiliteCase) {
					for (char prop : proposition.getText().toCharArray()) {
						if (!(prop == texteATrouver.charAt(indexProp)))
							break;
						indexProp++;
					}
				} else {
					for (char prop : proposition.getText().toLowerCase().toCharArray()) {
						if (!(prop == texteATrouver.toLowerCase().charAt(indexProp)))
							break;
						indexProp++;
					}
				}

				if (indexProp == index + proposition.getText().length())
					System.out.println("trouvé!");
				else {
					System.out.println(
							"prop : " + indexProp + "\ncalc index : " + (index + proposition.getText().length()));
				}

				System.out.println("autorisé les mots incomplets ? : " + motincomplet);

				if ((indexProp == index + proposition.getText().length() && motincomplet) ||
				// verification pour mots complet(donc caractére non oculter a la fin)
						(!motincomplet && texteCache.length() >= indexProp + 1
								&& CARACTERE_NON_OCULTER.contains(texteCache.charAt(indexProp) + "")
								&& indexProp == index + proposition.getText().length())) {
					System.out.println("im in");
					for (int i = index; i < indexProp; i++) {
						char[] chars = texteCache.toCharArray();
						chars[i] = texteATrouver.charAt(i);
						texteCache = String.valueOf(chars);
					}
				}
			}
			index++;
		}

		s.setTexteCache(texteCache);
	}

	public static void lockAll() {
		for (Node parent : root.getChildren()) {
			VBox vbox = (VBox) parent; // tout les enfant de la gridpane root sont des vbox si on change le fxml sa va
										// tout casser
			for (Node n : vbox.getChildren()) {
				if (!(n instanceof MenuBar)) {
					n.setDisable(true);
				}
			}
		}
	}

	public void demarrerExercice() {
		System.out.println(tempsText.getText());
		if (chronometrer)
			Timer(tempsTotal);
	}
	
	public void quitter() {
		System.exit(0);
	}
}

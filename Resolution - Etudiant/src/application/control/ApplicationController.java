package application.control;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import application.Main;
import application.model.CustomTimer;
import application.model.JsonController;
import application.model.Lang;
import application.model.Section;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import javafx.util.Duration;

public class ApplicationController extends Main {

	/* 10/02/2019 G8 Programmation de l'application VERSION ETUDIANTE */
	private static final int HAUTEUR_FENETRE=600;
	private static final int LARGEUR_FENETRE=1000;
	public static final String CARACTERE_OCULTATION = "*";
	public static final String CARACTERE_NON_OCULTER = ";.,!? ";
	public static boolean sensibiliteCase = false;
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
	//TODO faire le temps des section chaque section contien ses time code mais on ne s'en sert pas 
	@FXML
	private Text tempsTextSection;
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
	public static boolean aideAutorisation=true;
	private boolean videoChargee = false;
	public static LocalTime tempsTotal = LocalTime.parse("00:00:00");
	public static boolean chronometrer = false;
	public static boolean motincomplet = false;
	
	// TODO implementer le modeAprentissage et le mode enseignant
	public static boolean modeAprentissage;
	CustomTimer custom;

	public void ouvrirUnExercice() throws InterruptedException {
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
	
	public void sauvegarderUnExercice() {
		if(!videoChargee)return;
		final FileChooser dialog = new FileChooser();
		dialog.getExtensionFilters()
		.setAll(new FileChooser.ExtensionFilter(Lang.FILE + JsonController.DEFAULT_NAME_EXTENSION, "*" + JsonController.DEFAULT_EXTENSION));
		File file = dialog.showSaveDialog(super.getStage());
		
		String cheminVideo = mediaView.getMediaPlayer().getMedia().getSource();
		String format="";

		for(int i=4;i>0;i--) {
			format += cheminVideo.charAt(cheminVideo.length()-i);
		}
		System.out.println(sections.get(0) + "    "+sections.size());
		JsonController.JSONCreation(fixMyPath(file.toString(),".res"), titre.getText(), sections, aideAutorisation, sensibiliteCase, modeAprentissage, motincomplet, solutionBoutton.isDisabled(), consigne.getText(), format, tempsTotal.toString());
		
		File source;
		try {
			source = new File(new URI(mediaView.getMediaPlayer().getMedia().getSource()));
			File dest = new File(fixMyPath(file.getAbsoluteFile().toString(), format));
			Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			//TODO AFFICHER CETTE MERDE
		} catch (URISyntaxException e) {
			//ERREUR URI CE N'EST PAS REGLABLE PAR L'utilisateur
		} catch (IOException e) {
			//ERREUR ECRITURE
		}
		
	}
	
	/**
	 * ajoute .res a la fin si il n'est pas deja present j'ai euh le probleme sur
	 * linux ou sa enregistrais sans extention sa ne deverais pas poser probleme sur
	 * windows j'y ai ajouter aussi la fonction pour renomer les fichier en mp4
	 */
	private static String fixMyPath(String chemin, String format) {
		int i = chemin.length();
		String str = "";
		for (int j = i - 4; j < i; j++)
			str += chemin.charAt(j);

		if (str.equals(".res")) {
			chemin = chemin.replace(".res", format);
			return chemin;
		} else {
			return chemin + format;
		}
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

		/*progression.setOnMouseReleased(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				double newValue = progression.getValue();
				mediaPlayer.seek(
						new Duration(1000 * (double) newValue / 100 * mediaPlayer.getTotalDuration().toSeconds()));
			}
		});*/
		progression.setOnMouseReleased(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				double newValue = progression.getValue();
				mediaPlayer.seek(
						new Duration(1000 * newValue / 100 * mediaPlayer.getTotalDuration().toSeconds()));
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
			if (interactionVideoBtn.getText().equalsIgnoreCase(Lang.PAUSE)) {
				mediaView.getMediaPlayer().pause();
				interactionVideoBtn.setText(Lang.PLAY);
			} else {
				mediaView.getMediaPlayer().play();
				interactionVideoBtn.setText(Lang.PAUSE);
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

		System.out.println(Lang.LancementRecherche);
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

				System.out.println("autorisé les mots incomplets /usr/bin/SceneBuilder? : " + motincomplet);

				if ((indexProp == index + proposition.getText().length() && motincomplet) ||
				// verification pour mots complet(donc caractére non oculter a la fin)
						(!motincomplet && texteCache.length() >= indexProp + 1
								&& CARACTERE_NON_OCULTER.contains(texteCache.charAt(indexProp) + "")
								&& indexProp == index + proposition.getText().length())) {
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
	
	@FXML
	private void option(ActionEvent event) throws IOException {
		Stage sta = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/vue/MenuHandicap.fxml"));
		Parent OptionRoot = loader.load();
		Option control = loader.getController();
		if(OptionRoot == null)System.exit(1);
		sta.setScene(new Scene(OptionRoot));
		sta.show();
		control.run(sta);
	}
	
	public static void changeResolutionFromPolice(String TaillePolice) {
		int ratio = 8;
		Main.setHauteur(HAUTEUR_FENETRE + (Integer.parseInt(TaillePolice)*ratio)-13);
		Main.setLargeur(LARGEUR_FENETRE + (Integer.parseInt(TaillePolice)*ratio)-13);
	}

	public static void changePoliceSize(String size) {
		for(Node e : Option.getFinalChildren(Main.getRoot())) {
			e.setStyle("-fx-font: "+size+" arial;"); 
		}
	}
	
	
	public void quitter() {
		System.exit(0);
	}
}

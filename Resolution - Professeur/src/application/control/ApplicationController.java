package application.control;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import application.model.ErreurModel;
import application.model.SectionTab;
import application.view.Main;
import application.view.Option;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class ApplicationController extends Main {

	private final String DEFAULT_EXTENSION = ".res";
	private final String DEFAULT_NAME_EXTENSION = "Résolution";
	private String time = "00:00:00";
	private List<Section> sections;
	private boolean videoChargee = false;

	@FXML
	private TextField titre;
	@FXML
	private TextField timefieldh;
	@FXML
	private TextField timefieldm;
	@FXML
	private CheckBox sensibiliteCase;
	@FXML
	private CheckBox aideCheckbox;
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

	private static final int HAUTEUR_FENAITRE=600;
	private static final int LARGEUR_FENAITRE=1000;
	

	public void nouvelleExercice() throws IOException {
		System.out.println("Création d'un exercice");
		//setHauteur(666);
		//setLargeur(964);
		super.chargerUnePage("/application/model/NouvelleExercice.fxml");
	}

	public void nouvelleExerciceMenu() throws IOException {
		Alert alert = ErreurModel.warn("Confirmation", "/!\\ la création/ouverture d'un nouvelle exo va entrainer le suppretion de toutes donné non sauvegarder"
				, "cliquer sur ok pour continuer quand meme");
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			chargerUnePage("/application/model/NouvelleExercice.fxml");
			sections = new ArrayList<>(); // le ramasse miétte s'ocupera du reste
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaView.setMediaPlayer(null);
			}
			Section.reset();
			sectionsTabPane.getTabs().clear();
			sectionsTimeCodePane.getTabs().clear();
			setupbtn();
			videoChargee = false;
			aucuneVideoChargee.setVisible(true);
		}

	}

	
	//TODO deplacer le gros de l'ouverture dans JSONController
	public void ouvrir() throws IOException {
		sections = new ArrayList<>(); // le ramasse miétte s'ocupera du reste
		if (mediaPlayer != null)
			mediaPlayer.pause();
		Section.reset();
		setupbtn();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Fichier " + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION));
		fileChooser.setTitle("Ouvrir un fichier de Resolution");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(Main.stage);
		if (selectedFile != null) {
			System.out.println("Chargement de l'exercice");
			JSONParser parser = new JSONParser();
			try (Reader reader = new FileReader(selectedFile.getAbsolutePath())) {

				JSONObject jsonObject = (JSONObject) parser.parse(reader);
				titre.setText((String) jsonObject.get("titre"));
				consigne.setText((String) jsonObject.get("consigne"));
				String formatvideo = (String) jsonObject.get("cheminVideo");

				sensibiliteCase.setSelected((boolean) jsonObject.get("sensibiliteCase"));
				
				
				//ce try catch permet la conversion de sauvegarde de version precedente pre 0.0.10
				try {
					aideCheckbox.setSelected((boolean) jsonObject.get("aidestatus"));
				} catch (Exception e) {
					aideCheckbox.setSelected(true);
				}
				
				String limiteTemps = (String) jsonObject.get("limiteTemps");
				time = limiteTemps;
				timefieldh.setText(limiteTemps.charAt(0) + "" + limiteTemps.charAt(1));
				timefieldm.setText(limiteTemps.charAt(3) + "" + limiteTemps.charAt(4));

				sections = new ArrayList<>();
				for (int i = 1; i <= (long) jsonObject.get("sections"); i++) {
					sections.add(new Section(sectionsTabPane, sectionsTimeCodePane,
							(String) jsonObject.get("SectionAide" + i), (String) jsonObject.get("SectionText" + i),
							(String) jsonObject.get("SectionTimeCode" + i)));
				}

				String cheminVideo = selectedFile.getAbsolutePath().replace(".res", formatvideo);

				chargerUneVideo(new File(cheminVideo));
				mediaView.setVisible(videoChargee);
				motIncomplet.setSelected((boolean) jsonObject.get("motIncomplet"));
			} catch (Exception e) {
				ErreurModel.erreur("fichier introuvable ou endomager", "votre fichier d'exercice est introuve ou a éte endomager");
			}
		}

	}

	public void chargerUneVideoBTN() {
		sections = new ArrayList<>();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp4", "*.mp4"),
				new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("*.avi", "*.avi"),new FileChooser.ExtensionFilter("All", "*"));
		fileChooser.setTitle("Ouvrir une vidéo/audio");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(super.getStage());
		chargerUneVideo(selectedFile);
		setupbtn();
		sections.add(new Section(sectionsTabPane, sectionsTimeCodePane));
	}

	public void chargerUneVideo(File selectedFile) {
		if (selectedFile != null) {
			Media media = new Media(new File(selectedFile.getAbsolutePath()).toURI().toString());
			System.out.println(selectedFile.getAbsolutePath());
			mediaPlayer = new MediaPlayer(media);
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
			aucuneVideoChargee.setVisible(false);
			mediaView.setFitHeight(250);
			videoChargee = true;
			mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
				if (!progression.isValueChanging() && !progression.isPressed()) {
					progression.setValue(newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds() * 100);
				}
			});
			
			aideCheckbox.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent arg0) {
					for(Section s : sections) {
						if(!aideCheckbox.isSelected())s.disableAide();
						else s.enableAide();
					}
					
				}
			});
			
			progression.setOnMouseReleased(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					double newValue = progression.getValue();
					mediaPlayer.seek(
							new Duration(1000 * newValue / 100 * mediaPlayer.getTotalDuration().toSeconds()));
				}
			});
		}
	}

	public void setupbtn() {
		sectionsTabPane.getTabs().clear();
		SectionTab.newSectionTab(sectionsTabPane, sectionsTimeCodePane, sections);
	}

	public void timeHandle() {
		timefieldh.setDisable(!checklimite.isSelected());
		timefieldm.setDisable(!checklimite.isSelected());
	}

	public void interactionVideo() {
		if (videoChargee) {
			if (interactionVideoBtn.getText().equalsIgnoreCase("‖ Pause")) {
				mediaView.getMediaPlayer().pause();
				interactionVideoBtn.setText("▸ Jouer");
			} else {
				mediaView.getMediaPlayer().play();
				interactionVideoBtn.setText("‖ Pause");
			}
		}
	}

	public void handleRadialA() {
		modeApprentissage.setSelected(true);
		modeEvaluation.setSelected(false);
		motIncomplet.setDisable(false);
		affichageSolution.setDisable(false);
		aideCheckbox.setDisable(false);
	}

	public void handleRadialE() {
		modeApprentissage.setSelected(false);
		modeEvaluation.setSelected(true);
		motIncomplet.setDisable(true);
		affichageSolution.setDisable(true);
		aideCheckbox.setDisable(true);
	}

	public void stopVideo() {
		interactionVideoBtn.setText("Jouer");
		mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStopTime());
		mediaView.getMediaPlayer().stop();
	}

	public void sauvegarderExercice() {
		if (!checklimite.isSelected())
			time = "00:00:00";
		else {
			if (timefieldh.getText().length() == 1)
				timefieldh.setText("0" + timefieldh.getText());
			if (timefieldm.getText().length() == 1)
				timefieldm.setText("0" + timefieldm.getText());
			if (timefieldh.getText().length() == 0)
				timefieldh.setText("00");
			if (timefieldm.getText().length() == 0)
				timefieldm.setText("00");
			if (timefieldm.getText().length() > 2) {
				timefieldm.setStyle("-fx-text-inner-color: red;");
				return;
			}
			if (timefieldh.getText().length() > 2) {
				timefieldh.setStyle("-fx-text-inner-color: red;");
				return;
			}
			time = timefieldh.getText() + ":" + timefieldm.getText() + ":00";
		}

		if (!videoChargee) {
			ErreurModel.erreur("aucune video trouver", "vous devez mettre une video avant de sauvegarder");
			return;
		}
		final FileChooser dialog = new FileChooser();
		dialog.getExtensionFilters()
				.setAll(new FileChooser.ExtensionFilter("Fichiers " + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION));
		final File file = dialog.showSaveDialog(super.getStage());
		if (file != null) {
			if (videoChargee) {
				String videoformat = "";
				final int medialength = mediaView.getMediaPlayer().getMedia().getSource().length();
				for (int i = medialength - 4; i < medialength; i++)
					videoformat += mediaView.getMediaPlayer().getMedia().getSource().charAt(i); 
				// on sauvegarde que le format car on copie la video avec le fichier pour rendre le tout transportable
				JsonController.JSONCreation(fixMyPath(file.getAbsoluteFile().toString(), ".res"), titre.getText(),
						sections,aideCheckbox.isSelected() ,sensibiliteCase.isSelected(), modeApprentissage.isSelected(),
						motIncomplet.isSelected(), affichageSolution.isSelected(), consigne.getText(), videoformat,
						time);

				try {
					File source = new File(new URI(mediaView.getMediaPlayer().getMedia().getSource()));
					File dest = new File(fixMyPath(file.getAbsoluteFile().toString(), videoformat));
					Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				} catch (URISyntaxException e1) {
					ErreurModel.erreur("erreur URI", "le chemin d'acces de la vidéo est éroné veuiller le redéfinir");
				} catch (IOException e) {
					ErreurModel.erreur("erreur copie",
							"une erreur dans la copie est survenue veuiller verifier si vous avez les droit dans le dossier de destination et verifier sur vous avez sufisament d'espace libre (le poids de votre video defini la place nessecaire)");
				}
			} /*
				 * else {
				 * 
				 * //je le remplace par un msg d'erreur sa ne fait pas de sens d'enregistrer le
				 * doc sans video et par consequent aucune section
				 * //JsonController.JSONCreation(fixMyPath(file.getAbsoluteFile().toString(),
				 * ".res"), titre.getText(), null, sensibiliteCase.isSelected(),
				 * modeApprentissage.isSelected(), motIncomplet.isSelected(),
				 * affichageSolution.isSelected(), modeEvaluation.isSelected(),
				 * consigne.getText(), null, time); }
				 */
			// le msg se trouve avant le file chooser

		}
	}

	/**
	 * ajoute .res a la fin si il n'est pas deja present j'ai euh le probleme sur
	 * linux ou sa enregistrais sans extention sa ne deverais pas poser probleme sur
	 * windows j'y ai ajouter aussi la fonction pour renomer les fichier en mp4
	 */
	private String fixMyPath(String chemin, String format) {
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

	
	//TODO chager exo
	public void chargerExercice() throws Exception {
		//nouvelleExercice();
		// ouvrir(); bien sur ce n'est pas possible de les enchainer et je sais pas comment faire
		
	}

	public void optionMenu() {
		try {
			new Option();
		} catch (IOException e) {
			ErreurModel.erreurStack(e);
		}
		System.out.println("option");
	}

	public int onSelectNumber() {
		return 0;
	}
	
	public static void changeResolutionFromPolice(String TaillePolice) {
		int ratio = 8;
		Main.setHauteur(HAUTEUR_FENAITRE + (Integer.parseInt(TaillePolice)*ratio)-13);
		Main.setLargeur(LARGEUR_FENAITRE + (Integer.parseInt(TaillePolice)*ratio)-13);
	}

	public static void changePoliceSize(String size) {
		for(Node e : Option.getFinalChildren(Main.getRoot())) {
			e.setStyle("-fx-font: "+size+" arial;"); 
		}
	}

}

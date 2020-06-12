package application.control;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;

import application.Main;
import application.model.JsonController;
import application.model.Lang;
import application.model.Section;
import application.view.ErreurModel;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ApplicationController extends Main {

	private static String TaillePolice;
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

	private static final int HAUTEUR_FENETRE = 700;
	private static final int LARGEUR_FENETRE = 1000;

	public void nouvelleExercice() throws IOException {
		//setHauteur(800);
		// setLargeur(964);
		super.chargerUnePage("/application/view/NouvelleExercice.fxml");
	}

	public void nouvelleExerciceMenu() throws IOException {
		Alert alert = ErreurModel.warn(Lang.CONFIRMATION, Lang.WARN_NEW_EXO, Lang.OK_TO_CONTINUE);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			chargerUnePage("/application/view/NouvelleExercice.fxml");
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

	// TODO deplacer le gros de l'ouverture dans JSONController
	public void ouvrir() throws IOException {
		if(TaillePolice == null)super.setHauteur(700);
		if (mediaPlayer != null)
			mediaPlayer.pause();
		Section.reset();
		setupbtn();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter(Lang.FILE + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION));
		fileChooser.setTitle(Lang.OUVRIRE_FICHIER);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(Main.stage);

		System.out.println(Lang.CHARGE_EXO);
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			ReponseEtudiant reponseEtudiantController = null;
			Document doc = saxBuilder.build(selectedFile);
			IteratorIterable<?> processDescendants = doc.getDescendants(new ElementFilter("section"));
			if (processDescendants.hasNext() && ((Element) processDescendants.next()).getChildText("reponseEtudiant") != null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ResultatEtudiant.fxml"));
				Stage sta = new Stage();
				BorderPane root = loader.load();
				reponseEtudiantController = loader.getController();
				Scene sc = new Scene(root);
				sta.setScene(sc);
				reponseEtudiantController.init(sta);
			}
			
			//je reset l'iterator car j'avais juste besoin d'une section
			processDescendants = doc.getDescendants(new ElementFilter("section"));

			titre.setText(doc.getRootElement().getChildText("titre"));
			consigne.setText(doc.getRootElement().getChildText("consigne"));
			String formatvideo = doc.getRootElement().getChildText("cheminVideo");

			sensibiliteCase.setSelected(Boolean.parseBoolean(doc.getRootElement().getChildText("sensibiliteCase")));
			aideCheckbox.setSelected(Boolean.parseBoolean(doc.getRootElement().getChildText("aidestatus")));
			motIncomplet.setSelected(Boolean.parseBoolean(doc.getRootElement().getChildText("motIncomplet")));

			String limiteTemps = doc.getRootElement().getChildText("limiteTemps");
			System.out.println(limiteTemps);
			timefieldh.setText(limiteTemps.charAt(0) + "" + limiteTemps.charAt(1));
			timefieldm.setText(limiteTemps.charAt(3) + "" + limiteTemps.charAt(4));

			sections = new ArrayList<>();
			while (processDescendants.hasNext()) {
				Element elem = (Element) processDescendants.next();
				byte[] raw = Base64.getDecoder().decode(elem.getChild("SectionText").getValue());

				Section s =new Section(sectionsTabPane, sectionsTimeCodePane, elem.getChild("SectionAide").getValue(),
						new String(raw), elem.getChild("SectionTimeLimitCode").getValue(),
						elem.getChild("getTimeStart").getValue(), elem.getChild("getTimeStop").getValue());
				sections.add(s);
				if(reponseEtudiantController != null) {
					reponseEtudiantController.addTab(elem);
				}
			}

			String cheminVideo = selectedFile.getAbsolutePath().replace(".res", formatvideo);

			chargerUneVideo(new File(cheminVideo));
			mediaView.setVisible(videoChargee);

			if(reponseEtudiantController != null) {
				reponseEtudiantController.run();
			}
		} catch (JDOMException | IOException e) {
			ErreurModel.erreur(Lang.FICHIER_DMG, Lang.FICHIER_DMG_NEW);
		}

	}

	public void chargerUneVideoBTN() {
		sections = new ArrayList<>();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp4", "*.mp4"),
				new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("*.avi", "*.avi"),
				new FileChooser.ExtensionFilter("All", "*"));
		fileChooser.setTitle(Lang.OUVRIR_VID);
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
					for (Section s : sections) {
						if (!aideCheckbox.isSelected())
							s.disableAide();
						else
							s.enableAide();
					}

				}
			});

			progression.setOnMouseReleased(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					double newValue = progression.getValue();
					mediaPlayer.seek(new Duration(1000 * newValue / 100 * mediaPlayer.getTotalDuration().toSeconds()));
				}
			});
		}
	}

	public void setupbtn() {
		if (sectionsTabPane != null)
			sectionsTabPane.getTabs().clear();
		// SectionTab.newSectionTab(sectionsTabPane, sectionsTimeCodePane, sections);
	}

	public void timeHandle() {
		timefieldh.setDisable(!checklimite.isSelected());
		timefieldm.setDisable(!checklimite.isSelected());
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
		interactionVideoBtn.setText(Lang.PLAY);
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
			ErreurModel.erreur(Lang.NOVID, Lang.SAUV);
			return;
		}
		final FileChooser dialog = new FileChooser();
		dialog.getExtensionFilters()
				.setAll(new FileChooser.ExtensionFilter(Lang.FILE + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION));
		final File file = dialog.showSaveDialog(super.getStage());
		if (file != null) {
			if (videoChargee) {
				String videoformat = "";
				final int medialength = mediaView.getMediaPlayer().getMedia().getSource().length();
				for (int i = medialength - 4; i < medialength; i++)
					videoformat += mediaView.getMediaPlayer().getMedia().getSource().charAt(i);
				// on sauvegarde que le format car on copie la video avec le fichier pour rendre
				// le tout transportable
				JsonController.JSONCreation(fixMyPath(file.getAbsoluteFile().toString(), ".res"), titre.getText(),
						sections, aideCheckbox.isSelected(), sensibiliteCase.isSelected(),
						modeApprentissage.isSelected(), motIncomplet.isSelected(), affichageSolution.isSelected(),
						consigne.getText(), videoformat, time);

				try {
					File source = new File(new URI(mediaView.getMediaPlayer().getMedia().getSource()));
					File dest = new File(fixMyPath(file.getAbsoluteFile().toString(), videoformat));
					Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

				} catch (URISyntaxException e1) {
					ErreurModel.erreur(Lang.URI_ERROR, Lang.FICHIER_LIEN_ERR);
				} catch (IOException e) {
					ErreurModel.erreur(Lang.ERR_COPY, Lang.ERR_COPY_DETAIL);
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

	// TODO chager exo
	public void chargerExercice() throws Exception {
		nouvelleExercice();
		// ouvrir(); bien sur ce n'est pas possible de les enchainer et je sais pas
		// comment faire
		Platform.runLater(() -> {
			try {
				ouvrir();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@FXML
	private void option(ActionEvent event) throws IOException {
		Stage sta = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/MenuHandicap.fxml"));
		Parent OptionRoot = loader.load();
		Option control = loader.getController();
		if (OptionRoot == null)
			System.exit(1);
		sta.setScene(new Scene(OptionRoot));
		sta.show();
		control.run(sta);
	}

	public int onSelectNumber() {
		return 0;
	}

	public static void changeResolutionFromPolice(String TaillePolice) {
		ApplicationController.TaillePolice=TaillePolice;
		int ratio = 8;
		Main.setHauteur(HAUTEUR_FENETRE + (Integer.parseInt(TaillePolice) * ratio) - 13);
		Main.setLargeur(LARGEUR_FENETRE + (Integer.parseInt(TaillePolice) * ratio) - 13);
	}

	public static void changePoliceSize(String size) {
		for (Node e : Option.getFinalChildren(Main.getRoot())) {
			e.setStyle("-fx-font: " + size + " arial;");
		}
	}

	@FXML
	private void runSegmentationMenu(ActionEvent event) {
		System.out.println("chargement Section");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Section.fxml"));
		Parent root;
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			SectionController sController = loader.getController();
			Platform.runLater(() -> {
				if (sections != null)
					sController.run(scene, sections, (int) mediaPlayer.getTotalDuration().toSeconds(), sectionsTabPane,
							sectionsTimeCodePane);
				else
					ErreurModel.erreur(Lang.NO_SECTION, Lang.NEED_VID_LOADED);
			});
		} catch (IOException e) {
			System.err.println("ous sa a crash");
			e.printStackTrace();
		}
	}

}

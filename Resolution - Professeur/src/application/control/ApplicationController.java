package application.control;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jdom2.JDOMException;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ApplicationController extends Main {

	// integer peut être null
	private static Integer taillePolice;
	private final String DEFAULT_EXTENSION = ".res";
	private final String DEFAULT_NAME_EXTENSION = "Résolution";
	private final String ABORT = "ABORT";
	private String time = "00:00:00";
	private List<Section> sections;
	private boolean videoChargee = false;

	@FXML
	private Text timeDisplay;
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

	/**
	 * recharge le fxml affin de mettre tout a 0 
	 * 
	 * @throws IOException
	 */
	public void nouvelleExercice() throws IOException {
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
			clearTab();
			videoChargee = false;
			aucuneVideoChargee.setVisible(true);
		}

	}
	
	/**
	 * cette methode permet d'ouvir un nouveau fichier
	 */
	public void ouvrir(){
		if (taillePolice == null)
			setHauteur(700);
		if (mediaPlayer != null)
			mediaPlayer.pause();
		Section.reset();
		clearTab();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter(Lang.FILE + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION));
		fileChooser.setTitle(Lang.OUVRIRE_FICHIER);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(Main.stage);

		System.out.println(Lang.CHARGE_EXO);
		
		try {
			if(sections != null)sections.clear();
			else sections = new ArrayList<>();
			JsonController jcont = new JsonController();
			String formatvideo = jcont.jsonReader(selectedFile,titre,consigne,sensibiliteCase,aideCheckbox,motIncomplet,timefieldh,timefieldm,sections,sectionsTabPane,sectionsTimeCodePane);
			String cheminVideo = selectedFile.getAbsolutePath().replace(".res", formatvideo);
			chargerUneVideo(new File(cheminVideo));
			mediaView.setVisible(videoChargee);
		} catch (JDOMException | IOException e) {
			ErreurModel.erreur(Lang.FICHIER_DMG, Lang.FICHIER_DMG_NEW);
		}

	}

	/**
	 * gére les action du boutton changer de video
	 */
	public void chargerUneVideoBTN() {
		if(sections ==null)sections = new ArrayList<>();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp4", "*.mp4"),
				new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("*.avi", "*.avi"),
				new FileChooser.ExtensionFilter("All", "*"));
		fileChooser.setTitle(Lang.OUVRIR_VID);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(super.getStage());
		chargerUneVideo(selectedFile);
		
		//si on charge la premiére video il faut crée les premiers onglets
		if(sections.isEmpty()) {
			clearTab();
			sections.add(new Section(sectionsTabPane, sectionsTimeCodePane));
		}
	}
	
	/**
	 * charge la video passer en paramétre
	 * @param selectedFile fichier de la video
	 */
	public void chargerUneVideo(File selectedFile) {
		boolean first = mediaView.getMediaPlayer() != null;
		System.out.println(first);
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
				timeDisplay.setText((long) newTime.toHours() + ":" + (long) newTime.toMinutes() % 60 + ":"
						+ (long) newTime.toSeconds() % 60);
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

	/**
	 * sert a clear les tab
	 */
	public void clearTab() {
		if (sectionsTabPane != null)
			sectionsTabPane.getTabs().clear();
	}

	/**
	 * permet de blocker le chanps de la limite de temps si la case n'est pas coché
	 */
	public void timeHandle() {
		timefieldh.setDisable(!checklimite.isSelected());
		timefieldm.setDisable(!checklimite.isSelected());
	}

	
	/**
	 * a chaque apelle cette methode change le texte du bouton et en fonction de ce texte joue ou pause la video
	 */
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

	/**
	 * cette methode est concu pour déselectionner le mode evaluation et activer les option du mode
	 */
	public void handleRadialA() {
		modeApprentissage.setSelected(true);
		modeEvaluation.setSelected(false);
		motIncomplet.setDisable(false);
		affichageSolution.setDisable(false);
		aideCheckbox.setDisable(false);
	}

	/**
	 * cette methode est concu pour deselectionner le mode apprentisage et desactiver tout les options dedié au modeEvaluation
	 */
	public void handleRadialE() {
		modeApprentissage.setSelected(false);
		modeEvaluation.setSelected(true);
		motIncomplet.setDisable(true);
		affichageSolution.setDisable(true);
		aideCheckbox.setDisable(true);
	}
	
	/**
	 * cette methode stope la video
	 */
	public void stopVideo() {
		//play / pause est basé sur le texte du bouton
		interactionVideoBtn.setText(Lang.PLAY);
		mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getStopTime());
		mediaView.getMediaPlayer().stop();
	}

	/**
	 * @return retourne un string au format HH:MM:SS provenant du time field
	 * peut retourner ABORT si le contenu du time field est non valide
	 */
	private String timeFieldToString(){
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
			return ABORT;
		}
		if (timefieldh.getText().length() > 2) {
			timefieldh.setStyle("-fx-text-inner-color: red;");
			return ABORT;
		}
		return timefieldh.getText() + ":" + timefieldm.getText() + ":00";
	}
	
	
	/**
	 * cette methode gére la sauvegarde de l'exercice
	 * el
	 */
	public void sauvegarderExercice() {
		if (!checklimite.isSelected())
			time = "00:00:00";
		else {
			time = timeFieldToString();
			if(time.equals(ABORT))return;
		}

		if (!videoChargee) {
			ErreurModel.erreur(Lang.NOVID, Lang.SAUV);
			return;
		}
		final FileChooser dialog = new FileChooser();
		dialog.getExtensionFilters()
				.setAll(new FileChooser.ExtensionFilter(Lang.FILE + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION));
		final File file = dialog.showSaveDialog(super.getStage());
		if (file != null && videoChargee) {
			// un StringBuilder est recomander car les strings sont couteux en perfomance
			StringBuilder videoformat = new StringBuilder(4);
			final int medialength = mediaView.getMediaPlayer().getMedia().getSource().length();
			for (int i = medialength - 4; i < medialength; i++)
				videoformat.append(mediaView.getMediaPlayer().getMedia().getSource().charAt(i));
			// on sauvegarde que le format car on copie la video avec le fichier pour rendre
			// le tout transportable
			JsonController.jsonCreation(fixMyPath(file.getAbsoluteFile().toString(), ".res"), titre.getText(), sections,
					aideCheckbox.isSelected(), sensibiliteCase.isSelected(), modeApprentissage.isSelected(),
					motIncomplet.isSelected(), affichageSolution.isSelected(), consigne.getText(),
					videoformat.toString(), time);

			try {
				File source = new File(new URI(mediaView.getMediaPlayer().getMedia().getSource()));
				File dest = new File(fixMyPath(file.getAbsoluteFile().toString(), videoformat.toString()));
				Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

			} catch (URISyntaxException e1) {
				ErreurModel.erreur(Lang.URI_ERROR, Lang.FICHIER_LIEN_ERR);
			} catch (IOException e) {
				ErreurModel.erreur(Lang.ERR_COPY, Lang.ERR_COPY_DETAIL);
			}
		}else {
			ErreurModel.erreur(Lang.PAS_DE_VIDEO, Lang.PAS_DE_VIDEO_DETAIL);
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

	// biensur ce code ne marche pas c'est un example de comment j'aurais aprécié
	// qu'il soit implementer
	// TODO chager exo
	/**
	 * ce methode est cencé être appeler pour ouvrir un exo mais ne fonctionne pas
	 * 
	 * @deprecated cette methode ne marche pas donc elle a été indiqué comme obselette
	 */
	public void chargerExercice() {
		// ouvrir(); bien sur ce n'est pas possible de les enchainer et je sais pas
		// comment faire
		Platform.runLater(() -> {
			try {
				nouvelleExercice();
				ouvrir();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Cette methode fxml permet d'afficher la fenaitre des options
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void option(ActionEvent event) throws IOException {
		Stage sta = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/MenuHandicap.fxml"));
		Parent optionRoot = loader.load();
		Option control = loader.getController();
		if (optionRoot == null)
			System.exit(1);
		sta.setScene(new Scene(optionRoot));
		sta.show();
		control.run(sta);
	}

	/**
	 * permet de redimentionner la fenaitre en fonction de la police passer en
	 * argument
	 * 
	 * @param taillePolice la taille de la police
	 */
	public static void changeResolutionFromPolice(int taillePolice) {
		ApplicationController.taillePolice = taillePolice;
		int ratio = 8;
		Main.setHauteur(HAUTEUR_FENETRE + (taillePolice * ratio) - 13);
		Main.setLargeur(LARGEUR_FENETRE + (taillePolice * ratio) - 13);
		stage.setMinHeight(HAUTEUR_FENETRE + (taillePolice*ratio)-13);
		stage.setMinWidth(LARGEUR_FENETRE + (taillePolice*ratio)-13);
	}

	/**
	 * change la taille de la police de tout les objets de l'interface
	 * 
	 * @param size taille de la police
	 */
	public static void changePoliceSize(int size) {
		for (Node e : Option.getFinalChildren(Main.getRoot())) {
			e.setStyle("-fx-font: " + size + " arial;");
		}
	}

	/**
	 * affiche le menu de segmentation du texte
	 */
	@FXML
	private void runSegmentationMenu() {
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
			ErreurModel.erreurStack(e);
		}
	}
	
	public void credit() {
		Image imgfile = new Image(getClass().getResourceAsStream("/application/view/IUTLOGO.png"));
		ImageView img = new ImageView(imgfile); 
		ErreurModel.infoDialog(" PEAN Adrien \n MOUSSÉ Florian \n SINGLANDE Thomas \n BARBIER Marc \n BOULAY Thibault\n" + 
				"\n\nCopyright © 2020 IUT de LAVAL \nTout droits réservés","crédits",img);
	}
	
	
	/**
	 * ouvre un page web avec le pdf du manuelle d'utilisation
	 */
	public void openDocs() throws IOException {
		String url = "https://www.dropbox.com/s/zrm6wrn3ipqmly7/Manuel%20d%27utilisation.pdf?dl=0";
		String os = System.getProperty("os.name").toLowerCase();
		Runtime runtime = Runtime.getRuntime();
		//ce systeme est essenciel pour supporter openjdk
		if (os.indexOf("win") >= 0 && Desktop.isDesktopSupported()) {
			// Windows
			runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			// mac os
			if(os.indexOf("mac") >= 0) {
				runtime.exec("open " + url);
			//linux
			}else {
				runtime.exec("xdg-open " + url);
			}
			
		}
	}

}

package application.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import application.Main;
import application.control.ApplicationController;
import application.vue.ErreurModel;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class JsonController {
	
	public static final String DEFAULT_NAME_EXTENSION = "Résolution";
	public static final String DEFAULT_EXTENSION = ".res";
	
	/**
	 * directement porté de la version prof
	 *  TODO : faire en sorte que sauvegarder en etudiant peut être ouver par un prof et non pas un eleve si le mode examen est actif
	 * @param cheminEnregistrement
	 * @param title
	 * @param sections
	 * @param aidestatus
	 * @param sensibiliteCase
	 * @param modeApprentissage
	 * @param motIncomplet
	 * @param solution
	 * @param consigne
	 * @param cheminVideo
	 * @param limiteTemps
	 */
	public static void JSONCreation(String cheminEnregistrement, String title, List<Section> sections,
			boolean aidestatus, boolean sensibiliteCase, boolean modeApprentissage, boolean motIncomplet,
			boolean solution, String consigne, String cheminVideo, String limiteTemps) {

		Element root = new Element("exercice");
		Document doc = new Document(root);
		
		for(Section s : sections) {
			Element section = new Element("section");
			
			section.addContent(new Element("SectionAide").setText(s.getAide()));
			section.addContent(new Element("SectionText").setText(Base64.getEncoder().encodeToString(s.getTextATrouver().getBytes())));
			section.addContent(new Element("reponseEtudiant").setText(Base64.getEncoder().encodeToString(s.getTexteCache().getBytes())));
			section.addContent(new Element("SectionTimeLimitCode").setText(s.getTimeLimiteCode()));
			section.addContent(new Element("getTimeStart").setText(s.getTimeStart()));
			section.addContent(new Element("getTimeStop").setText(s.getTimeStop()));
			doc.getRootElement().addContent(section);
		}
	

		root.addContent(new Element("titre").setText(title));
		root.addContent(new Element("sensibiliteCase").setText(sensibiliteCase+""));
		root.addContent(new Element("modeApprentissage").setText(modeApprentissage+""));
		root.addContent(new Element("motIncomplet").setText(motIncomplet+""));
		root.addContent(new Element("solution").setText(solution+""));
		root.addContent(new Element("aidestatus").setText(aidestatus+""));
		root.addContent(new Element("consigne").setText(consigne));
		root.addContent(new Element("cheminVideo").setText(cheminVideo));
		root.addContent(new Element("limiteTemps").setText(limiteTemps));
		
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			xmlOutput.output(doc, new FileWriter(cheminEnregistrement));
			System.out.println(xmlOutput.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * c'est un reader simpler mais /!\ il retourne le chemin video
	 * @param titre
	 * @param consigne
	 * @param solutionBoutton
	 * @param sections
	 * @param TabPaneExo
	 * @return
	 */
	public static String JSONReader(Text titre,TextField consigne,Button solutionBoutton, List<Section> sections,TabPane TabPaneExo) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter(Lang.FILE + DEFAULT_NAME_EXTENSION, "*" + DEFAULT_EXTENSION));
		fileChooser.setTitle(Lang.CHARGER_EXO);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(Main.stage);
		
		System.out.println(Lang.CHARGE_EXO);
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document doc = saxBuilder.build(selectedFile);
			IteratorIterable<?> processDescendants = doc.getDescendants(new ElementFilter("section"));
			
			ApplicationController.modeAprentissage = Boolean.parseBoolean(doc.getRootElement().getChildText("modeApprentissage"));
			//empeche de charger une sauvegarde d'un etudiant
			if (!ApplicationController.modeAprentissage && doc.getRootElement().getChildText("reponseEtudiant") != null)return null;

			
			titre.setText(doc.getRootElement().getChildText("titre"));
			consigne.setText(doc.getRootElement().getChildText("consigne"));
			String formatvideo = doc.getRootElement().getChildText("cheminVideo");
			
			ApplicationController.sensibiliteCase = Boolean.parseBoolean(doc.getRootElement().getChildText("sensibiliteCase"));
			ApplicationController.aideAutorisation = Boolean.parseBoolean(doc.getRootElement().getChildText("aidestatus"));
			ApplicationController.motincomplet = Boolean.parseBoolean(doc.getRootElement().getChildText("motIncomplet"));
			solutionBoutton.setDisable(Boolean.parseBoolean(doc.getRootElement().getChildText("solution")));
			
			String limiteTemps = doc.getRootElement().getChildText("limiteTemps");
				
			if (!(limiteTemps.equals("00:00:00"))) {
				ApplicationController.tempsTotal = LocalTime.parse(limiteTemps);
				ApplicationController.chronometrer = true;
			} else {
				ApplicationController.tempsTotal = LocalTime.parse("00:00:00");
				ApplicationController.chronometrer = false;
			}
			
			sections = new ArrayList<>();
			while(processDescendants.hasNext()) {
				Element elem = (Element) processDescendants.next();
				byte[] raw = Base64.getDecoder().decode(elem.getChild("SectionText").getValue());
				
				sections.add(new Section(TabPaneExo,
					elem.getChild("SectionAide").getValue(),
					new String(raw),
					elem.getChild("SectionTimeLimitCode").getValue(),
					elem.getChild("getTimeStart").getValue(),
					elem.getChild("getTimeStop").getValue()));
			}
			
			return selectedFile.getAbsolutePath().replace(".res", formatvideo);
		}catch (JDOMException | IOException e) {
			ErreurModel.erreur(Lang.FICHIER_DMG, Lang.FICHIER_DMG_NEW);
		}
		//si ce return est utiliser sa veut dire qu'on a euh un crash
		return null;
	}
	

}

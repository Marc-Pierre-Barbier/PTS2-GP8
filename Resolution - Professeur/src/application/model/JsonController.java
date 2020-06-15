package application.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import application.control.ReponseEtudiant;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class JsonController {

	public static void jsonCreation(String cheminEnregistrement, String title, List<Section> sections,
			boolean aidestatus, boolean sensibiliteCase, boolean modeApprentissage, boolean motIncomplet,
			boolean solution, String consigne, String cheminVideo, String limiteTemps) {

		Element root = new Element("exercice");
		Document doc = new Document(root);

		for(Section s : sections) {
			Element section = new Element("section");
			
			section.addContent(new Element("SectionAide").setText(s.getAide()));
			section.addContent(new Element("SectionText").setText(Base64.getEncoder().encodeToString(s.getText().getBytes())));
			
			section.addContent(new Element("SectionTimeLimitCode").setText(s.getTimeLimitCode()));
			System.out.println(s.getTimeLimitCode());
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String jsonReader(File selectedFile,TextField titre,TextField consigne, CheckBox sensibiliteCase, CheckBox aideCheckbox, CheckBox motIncomplet, TextField timefieldh, TextField timefieldm, List<Section> sections, TabPane sectionsTabPane, TabPane sectionsTimeCodePane) throws IOException, JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder();
		ReponseEtudiant reponseEtudiantController = null;
		Document doc = saxBuilder.build(selectedFile);
		IteratorIterable<?> processDescendants = doc.getDescendants(new ElementFilter("section"));
		if (processDescendants.hasNext()
				&& ((Element) processDescendants.next()).getChildText("reponseEtudiant") != null) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ResultatEtudiant.fxml"));
			Stage sta = new Stage();
			BorderPane root = loader.load();
			reponseEtudiantController = loader.getController();
			Scene sc = new Scene(root);
			sta.setScene(sc);
			reponseEtudiantController.init(sta);
		}

		// je reset l'iterator car j'avais juste besoin d'une section
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

			Section s = new Section(sectionsTabPane, sectionsTimeCodePane, elem.getChild("SectionAide").getValue(),
					new String(raw), elem.getChild("SectionTimeLimitCode").getValue(),
					elem.getChild("getTimeStart").getValue(), elem.getChild("getTimeStop").getValue());
			sections.add(s);
			if (reponseEtudiantController != null) {
				reponseEtudiantController.addTab(elem);
			}
		}
		
		if (reponseEtudiantController != null) {
			reponseEtudiantController.run();
		}
		
		return formatvideo;
	}

}

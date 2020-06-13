package application.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class JsonController {

	public static void JSONCreation(String cheminEnregistrement, String title, List<Section> sections,
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

}

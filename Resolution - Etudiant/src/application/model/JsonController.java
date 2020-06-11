package application.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;

import application.control.ApplicationController;
import application.vue.ErreurModel;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class JsonController {
	
	private static final String DEFAULT_NAME_EXTENSION = "Résolution";
	private static final String DEFAULT_EXTENSION = ".res";
	
	public void jSONCreation() {
		//TODO acoder
		/*System.out.println("Lancement");
		JSONObject obj = new JSONObject();
        obj.put("name", "mkyong.com");
        obj.put("age", 100);

        JSONArray list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");

        obj.put("messages", list);
        
        String originalInput = "test input";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        obj.put("Test", encodedString);

        try (FileWriter file = new FileWriter("c:\\project\\test.json")) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(obj);*/
	}
	/**
	 * c'est un reader simpler mais /!\ il retourne le chemin video
	 * 
	 * @param titre
	 * @param consigne
	 * @param solutionBoutton
	 * @param sections
	 * @param TabPaneExo
	 * @return
	 * @throws ParseException
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
			
			
			titre.setText(doc.getRootElement().getChildText("titre"));
			consigne.setText(doc.getRootElement().getChildText("consigne"));
			String formatvideo = doc.getRootElement().getChildText("cheminVideo");
			
			ApplicationController.sensibiliteCase = Boolean.parseBoolean(doc.getRootElement().getChildText("sensibiliteCase"));
			ApplicationController.aideAutorisation=Boolean.parseBoolean(doc.getRootElement().getChildText("aidestatus"));
			ApplicationController.motincomplet = Boolean.parseBoolean(doc.getRootElement().getChildText("motIncomplet"));
			
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

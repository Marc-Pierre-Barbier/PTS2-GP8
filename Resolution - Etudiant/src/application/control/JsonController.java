package application.control;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.vue.Main;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class JsonController {
	
	private static final String DEFAULT_EXTENSION_NAME = "Résolution";
	private static final String DEFAULT_EXTENSION_FILE = ".res";
	
	public void jSONCreation() {
		System.out.println("Lancement");
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

        System.out.print(obj);
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
	public static String JSONReader(Text titre,TextField consigne,Button solutionBoutton, List<Section> sections,TabPane TabPaneExo) throws ParseException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Fichier " + DEFAULT_EXTENSION_NAME, "*" + DEFAULT_EXTENSION_FILE));
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
				ApplicationController.sensibiliteCase = (boolean) jsonObject.get("sensibiliteCase");
				String limiteTemps = (String) jsonObject.get("limiteTemps");
				ApplicationController.motincomplet = (boolean) jsonObject.get("motIncomplet");
				if (!(boolean) jsonObject.get("affichageSolution")) {
					solutionBoutton.setDisable(true);
				}

				if (!(limiteTemps.equals("00:00:00"))) {
					ApplicationController.tempsTotal = LocalTime.parse(limiteTemps);
					// int hours = Integer.parseInt(limiteTemps.charAt(0) + limiteTemps.charAt(1)
					// +"");
					// int minutes = Integer.parseInt(limiteTemps.charAt(3) + limiteTemps.charAt(4)
					// + ""); //le char 0 est les disaines d'eur le char 1 les heure le char 2 le :
					// etc..
					ApplicationController.chronometrer = true;
				} else {
					ApplicationController.tempsTotal = LocalTime.parse("00:00:00");
					ApplicationController.chronometrer = false;
				}

				for (int i = 1; i <= (long) jsonObject.get("sections"); i++) {
					sections.add(new Section(TabPaneExo, (String) jsonObject.get("SectionAide" + i),
							(String) jsonObject.get("SectionText" + i),
							(String) jsonObject.get("SectionTimeCode" + i)));
				}
				return selectedFile.getAbsolutePath().replace(".res", formatvideo);
			} catch (IOException e) {
				return "ABORT";
			}
		}
		return "ABORT";
	}
}

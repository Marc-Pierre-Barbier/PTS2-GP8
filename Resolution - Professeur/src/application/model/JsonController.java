package application.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.json.simple.JSONObject;

import application.view.ErreurModel;

public class JsonController {
		
	@SuppressWarnings("unchecked")
	public static void JSONCreation(String cheminEnregistrement, Object title, List<Section> sections,boolean aidestatus ,boolean sensibiliteCase, boolean modeApprentissage, boolean motIncomplet, boolean solution, String consigne, String cheminVideo, String limiteTemps) {
		
		/*
		 *  JSON 
		 *  JSONObject obj = new JSONObject();
		 *  obj.put("name", "mkyong.com"); mettre un identifiant avec une valeur
		 * */
		 
		JSONObject obj = new JSONObject();
        obj.put("titre", title);
        obj.put("sections", sections.size());
        obj.put("sensibiliteCase", sensibiliteCase);
        obj.put("modeApprentissage", modeApprentissage);
        obj.put("motIncomplet", motIncomplet);
        obj.put("affichageSolution", solution);
        obj.put("aidestatus", aidestatus);
        //obj.put("modeEvaluation", modeEvaluation); inutile de le mettre on a que 2 mode car modeEvaluation=!modeEtudiant
        obj.put("consigne", consigne);
        obj.put("cheminVideo", cheminVideo);
        obj.put("limiteTemps", limiteTemps);

        for(Section  s : sections) {
        	if(s.getTimeCode().equals("ABORT")) {
        		
        		return;
        	}
        	if(aidestatus)obj.put("SectionAide"+s.getidTab(),s.getAide());
        	obj.put("SectionText"+s.getidTab(),s.getText());
        	obj.put("SectionTimeCode"+s.getidTab(),s.getTimeCode());
        }
        
        /*JSONArray list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");

        obj.put("messages", list);
        
        String originalInput = "test input";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        obj.put("Test", encodedString);*/

        try (FileWriter file = new FileWriter(cheminEnregistrement)) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
        	ErreurModel.erreur(Lang.WRITE_ERR, Lang.WRITE_RIGHT_ERR);
        }

        System.out.print(obj);
	}

}

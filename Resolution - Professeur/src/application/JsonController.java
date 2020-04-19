package application;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonController {
		
	@SuppressWarnings("unchecked")
	public static void JSONCreation(String cheminEnregistrement, Object title, Object texte, Object aide, boolean sensibiliteCase, boolean modeApprentissage, boolean motIncomplet, boolean solution, boolean modeEvaluation, String consigne, String cheminVideo, int limiteTempsHeure, int limiteTempsMinutes) {
		
		/*
		 *  JSON 
		 *  JSONObject obj = new JSONObject();
		 *  obj.put("name", "mkyong.com"); mettre un identifiant avec une valeur
		 * */
		 
		JSONObject obj = new JSONObject();
        obj.put("titre", title);
        obj.put("texte", texte);
        obj.put("aide", aide);
        obj.put("sensibiliteCase", sensibiliteCase);
        obj.put("modeApprentissage", modeApprentissage);
        obj.put("motIncomplet", motIncomplet);
        obj.put("affichageSolution", solution);
        obj.put("modeEvaluation", modeEvaluation);
        obj.put("consigne", consigne);
        obj.put("cheminVideo", cheminVideo);
        obj.put("limiteTemps", limiteTempsHeure+":"+limiteTempsMinutes);

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
            e.printStackTrace();
        }

        System.out.print(obj);
	}

}

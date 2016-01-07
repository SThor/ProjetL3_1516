package serveur.element.personnage;

import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Personnage;
/**
 * Un Ork se d�place plus vite
 */
public class Elfe extends Personnage{

	public Elfe(String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
	}

}

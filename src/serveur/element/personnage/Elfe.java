package serveur.element.personnage;

import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Personnage;
/**
 * Un Elfe se deplace plus vite
 */
public class Elfe extends Personnage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Elfe(String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
	}

}

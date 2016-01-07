package serveur.element.personnage;

import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Personnage;

/**
 * Un Ork fait plus de degat (+20)
 */
public class Orks extends Personnage{

	public Orks(String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
	}

}

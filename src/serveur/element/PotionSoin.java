package serveur.element;
import utilitaires.Calculs;

/**
 * Une potion: un element donnant un effet de confusion a celui qui le bois 
 * la confusion fait errer le buveur en le faisant se déplacer rapidement
 */
public class PotionSoin extends Potion {

	private static final long serialVersionUID = 1L;
	

	/**
	 * Constructeur d'une potion avec un nom, le groupe qui l'a envoyee et ses 
	 * caracteristiques (ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom nom de la potion
	 * @param groupe groupe d'etudiants de la potion
	 * @param caracts caracteristiques de la potion
	 */
	public PotionSoin(String groupe) {
		super("Mystere", groupe,Calculs.initCarac());
	}
}

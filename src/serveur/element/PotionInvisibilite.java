package serveur.element;


import utilitaires.Calculs;

/**
 * Une potion: un element donnant un effet d'invisibilite à celui qui
 * le ramasse.(il n'apparait pas dans la vision des autre joueurs)
 */
public class PotionInvisibilite extends Potion {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur d'une potion avec un nom, le groupe qui l'a envoyee et ses 
	 * caracteristiques (ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom nom de la potion
	 * @param groupe groupe d'etudiants de la potion
	 * @param caracts caracteristiques de la potion
	 */
	public PotionInvisibilite(String groupe) {
		super("Mystere",groupe,Calculs.initCarac());
	}
}

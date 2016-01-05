package serveur.element;

import utilitaires.Calculs;

public class PotionCarac extends Potion{
	
	private static final long serialVersionUID = 1L;
	

	/**
	 * Constructeur d'une potion avec un nom, le groupe qui l'a envoyee et ses 
	 * caracteristiques (ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom de la potion
	 * @param groupe groupe d'etudiants de la potion
	 * @param caracts caracteristiques de la potion
	 */
	public PotionCarac(String groupe) {
		super("", groupe,Calculs.CaracRandom());
		String newNom = "V= "+this.getCaract(Caracteristique.VIE) + "// F= "+this.getCaract(Caracteristique.FORCE) + "// I= "+this.getCaract(Caracteristique.INITIATIVE);
		this.setNom(newNom);
	}
	
}

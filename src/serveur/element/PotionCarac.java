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
		int vie = this.getCaract(Caracteristique.VIE);
		int force = this.getCaract(Caracteristique.FORCE);
		int ini = this.getCaract(Caracteristique.INITIATIVE);
		String newNom = "SpeedCola";
		if (vie >= force){
			if (vie >= ini){
				newNom = "Vitavi";
			}else{
				newNom = "Pafarouch";
			}
		}else{
			if (force >= ini){
				newNom = "Juggernog";
			}else{
				newNom = "Pafarouch";
			}
		}
		this.setNom(newNom);
	}
	
}

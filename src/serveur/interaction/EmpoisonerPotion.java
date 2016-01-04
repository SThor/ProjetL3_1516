package serveur.interaction;

import java.util.logging.Level;

import serveur.Arene;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Constantes;

public class EmpoisonerPotion extends Interaction<VuePotion> {

	public EmpoisonerPotion(Arene arene, VuePersonnage empoisonneur, VuePotion potion) {
		super(arene, empoisonneur, potion);
	}

	@Override
	public void interagit() {
		logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye d'empoisonner " + 
				Constantes.nomRaccourciClient(defenseur));
		
		// si le personnage est vivant
		if(attaquant.getElement().estVivant()) {
			
			// activation de l'empoisonnement
			defenseur.getElement().setEmpoisonnee();
			
			logs(Level.INFO, "Potion empoisonnee !");
			
		} else {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
					Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
		}
	}

}

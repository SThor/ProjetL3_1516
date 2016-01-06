package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Passif;
import serveur.element.PotionCarac;
import serveur.element.PotionConfusion;
import serveur.element.PotionInvisibilite;
import serveur.element.PotionSoin;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Constantes;

/**
 * Represente le ramassage d'une potion par un personnage.
 *
 */
public class Ramassage extends Interaction<VuePotion> {

	/**
	 * Cree une interaction de ramassage.
	 * @param arene arene
	 * @param ramasseur personnage ramassant la potion
	 * @param potion potion a ramasser
	 */
	public Ramassage(Arene arene, VuePersonnage ramasseur, VuePotion potion) {
		super(arene, ramasseur, potion);
	}

	@Override
	public void interagit() {
		try {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye de rammasser " + 
					Constantes.nomRaccourciClient(defenseur));
			
			// si le personnage est vivant
			if(attaquant.getElement().estVivant()) {
				if(defenseur.getElement() instanceof PotionCarac){
					// caracteristiques de la potion
					HashMap<Caracteristique, Integer> valeursPotion = defenseur.getElement().getCaracts();
					
					for(Caracteristique c : valeursPotion.keySet()) {
						arene.incrementeCaractElement(attaquant, c, valeursPotion.get(c));
					}
					
					logs(Level.INFO, "Potion bue !");
					
					// test si mort
					if(!attaquant.getElement().estVivant()) {
						arene.setPhrase(attaquant.getRefRMI(), "Je me suis empoisonne, je meurs ");
						logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vient de boire un poison... Mort >_<");
					}
				}else if (defenseur.getElement() instanceof PotionConfusion){
					attaquant.getElement().getPassifs().put(Passif.Confusion, Constantes.CONFUSION_DEFAUT);
					logs(Level.INFO, "Potion de confusion bue !");
				}else if (defenseur.getElement() instanceof PotionSoin){
					attaquant.getElement().getPassifs().put(Passif.Soin, Constantes.SOIN_DEFAUT);
					logs(Level.INFO, "Potion de soins bue !");
				}else if (defenseur.getElement() instanceof PotionInvisibilite) {
					attaquant.getElement().getPassifs().put(Passif.Invisibilite, Constantes.INVISIBILITE_DEFAUT);
					logs(Level.INFO, "Potion d'invisibilite bue !");
				}

				// suppression de la potion
				arene.ejectePotion(defenseur.getRefRMI());
				
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un ramassage : " + e.toString());
		}
	}
}

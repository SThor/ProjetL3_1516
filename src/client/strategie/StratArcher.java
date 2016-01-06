package client.strategie;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Ce personnage attaque a distance des qu'il peut
 * S'il est suffisament pres, il attaque au corps a corps (duel)
 * Il ne ramasse ni va vers les potions qu'il trouve inutile (voir potionUtile)
 */
public class StratArcher extends StrategiePersonnage{

	/**
	 * Cree un personnage, la console associe et sa strategie.
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param nom nom du personnage
	 * @param groupe groupe d'etudiants du personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */

	public StratArcher(String ipArene, int port, String ipConsole, String nom,
			String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {

		super( ipArene,  port,  ipConsole,  nom,
				groupe, caracts,
				nbTours,  position,  logger);
	}

	/** 
	 * Decrit la strategie.
	 * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
	 * de Arene et de ConsolePersonnage. 
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 */

	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		//caracteristiques
		int vie = console.getPersonnage().getCaract(Caracteristique.VIE);
		int force = console.getPersonnage().getCaract(Caracteristique.FORCE);
		int initiative = console.getPersonnage().getCaract(Caracteristique.INITIATIVE);

		// arene
		IArene arene = console.getArene();

		// reference RMI de l'element courant
		int refRMI = 0;

		// position de l'element courant
		Point position = null;

		// flag de potion utile
		boolean potionUtile = true;

		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0);

		} else {
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));
			Element elemPlusProche = arene.elementFromRef(refCible);

			//verifie qu'une potion est utile, en demandant une moyenne des stats a 0 et sans nous tuer
			//et sans nous faire baisser a moins de 10 une de nos caracteristiques (30 pour la vie)
			potionUtile = ((elemPlusProche instanceof Potion) &&
					((elemPlusProche.getCaract(Caracteristique.VIE) > 0) || (elemPlusProche.getCaract(Caracteristique.VIE) + vie) > 30) && 
					((elemPlusProche.getCaract(Caracteristique.FORCE) > 0) || (elemPlusProche.getCaract(Caracteristique.FORCE) + force) > 10) &&
					((elemPlusProche.getCaract(Caracteristique.INITIATIVE) > 0) || (elemPlusProche.getCaract(Caracteristique.INITIATIVE) + initiative) > 10) &&
					((elemPlusProche.getCaract(Caracteristique.VIE) 
							+ elemPlusProche.getCaract(Caracteristique.FORCE)
							+ elemPlusProche.getCaract(Caracteristique.INITIATIVE)) / 3) > 0);


			//si vu par att distante et non au corps a corps
			if((distPlusProche <= Constantes.DISTANCE_MIN_ATT_DISTANTE && distPlusProche > Constantes.DISTANCE_MIN_INTERACTION) &&
					elemPlusProche instanceof Personnage) {//personnage
				//tire
				console.setPhrase("Je tire vers mon ennemi "+ elemPlusProche.getNom());
				arene.lanceAttaqueDistante(refRMI, refCible);

			} else if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
				// j'interagis directement
				if(potionUtile) { // c'est une potion utile
					// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI, refCible);

				} else if(elemPlusProche instanceof Personnage){// personnage
					//duel
					console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
					arene.lanceAttaque(refRMI, refCible);
				}else{ //potion inutile
					//j'erre
					console.setPhrase("J'erre..."+ potionUtile);
					arene.deplace(refRMI, 0);
				}

			} else if(elemPlusProche instanceof Personnage || potionUtile) { 
				// si voisins, mais plus eloignes 
				// je vais vers le plus proche
				console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
				arene.deplace(refRMI, refCible);
			} else {
				//Sinon j'erre (potion inutile)
				console.setPhrase("J'erre, cette potion ne m'ait pas utile...");
				arene.deplace(refRMI, 0);
			}
		}
	}

}

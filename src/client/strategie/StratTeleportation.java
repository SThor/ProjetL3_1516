package client.strategie;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Passif;
import serveur.element.Personnage;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StratTeleportation extends StrategiePersonnage {

	/**
	 * Cree un personnage, la console associe et sa strategie.
	 * 
	 * @param ipArene
	 *            ip de communication avec l'arene
	 * @param port
	 *            port de communication avec l'arene
	 * @param ipConsole
	 *            ip de la console du personnage
	 * @param nom
	 *            nom du personnage
	 * @param groupe
	 *            groupe d'etudiants du personnage
	 * @param nbTours
	 *            nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position
	 *            position initiale du personnage dans l'arene
	 * @param logger
	 *            gestionnaire de log
	 */

	public StratTeleportation(String ipArene, int port, String ipConsole, String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts, int nbTours, Point position, LoggerProjet logger) {

		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);

	}

	/**
	 * Decrit la strategie. Les methodes pour evoluer dans le jeu doivent etre
	 * les methodes RMI de Arene et de ConsolePersonnage.
	 * 
	 * @param voisins
	 *            element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 */
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// arene
		IArene arene = console.getArene();

		// reference RMI de l'element courant
		int refRMI = 0;

		// position de l'element courant
		Point position = null;

		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			//si le cooldown me le permet, je me teleporte aleatoirement
			if(console.getPersonnage().getPassifs().get(Passif.TeleportationCoolDown)==0){
				console.setPhrase("J'erre, mais genre instant quoi ...");
				arene.teleporte(refRMI, 0);
			}else{
				console.setPhrase("J'erre...");
				arene.deplace(refRMI, 0);
			}

		} else {
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.elementFromRef(refCible);

			if (distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si
																			// suffisamment
																			// proches
				// j'interagis directement
				if (elemPlusProche instanceof Potion) { // potion
					// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI, refCible);

				} else { // personnage
					// duel
					console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
					arene.lanceAttaque(refRMI, refCible);
				}

			} else { // si voisins, mais plus eloignes
				// je vais vers le plus proche, en me teleportant si possible
				if(console.getPersonnage().getPassifs().get(Passif.TeleportationCoolDown)==0){
					console.setPhrase("Je me teleporte sur mon voisin " + elemPlusProche.getNom());
					arene.teleporte(refRMI, refCible);
				}else{
					console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
					arene.deplace(refRMI, refCible);
				}
			}
		}
	}

}

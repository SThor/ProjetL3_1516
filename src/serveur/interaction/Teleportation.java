package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Random;

import serveur.element.Passif;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class Teleportation {
	/**
	 * Vue du personnage qui veut se deplacer.
	 */
	private VuePersonnage personnage;

	/**
	 * References RMI et vues des voisins (calcule au prealable).
	 */
	private HashMap<Integer, Point> voisins;

	/**
	 * Cree un deplacement.
	 * 
	 * @param personnage
	 *            personnage voulant se deplacer
	 * @param voisins
	 *            voisins du personnage
	 */
	public Teleportation(VuePersonnage personnage, HashMap<Integer, Point> voisins) {
		this.personnage = personnage;

		if (voisins == null) {
			this.voisins = new HashMap<Integer, Point>();
		} else {
			this.voisins = voisins;
		}
	}

	/**
	 * Deplace ce sujet sur l'element dont la reference est donnee. Si la
	 * reference est la reference de l'element courant, il ne bouge pas ; si la
	 * reference est egale a 0, il se teleporte a un endroit aleatoire ; sinon
	 * il va sur le voisin correspondant (s'il existe dans les voisins).
	 * 
	 * @param refObjectif
	 *            reference de l'element cible
	 */
	public void seTeleporteA(int refObjectif) throws RemoteException {
		Point pvers;

		// on ne bouge que si la reference n'est pas la notre
		if (refObjectif != personnage.getRefRMI()) {

			// la reference est nulle (en fait, nulle ou negative) :
			// le personnage erre
			if (refObjectif <= 0) {
				pvers = Calculs.positionAleatoireArene();

			} else {
				// sinon :
				// la cible devient le point sur lequel se trouve l'element
				// objectif
				pvers = voisins.get(refObjectif);
			}

			// on ne bouge que si l'element existe
			if (pvers != null) {
				seTeleporteA(pvers);
			}
		}
	}

	/**
	 * Deplace ce sujet sur la case donnee, a une variance pres, et a condition
	 * que la case soit dans le champ de vision. Sinon, on prend la case la plus proche.
	 * 
	 * @param objectif
	 *            case cible
	 * @throws RemoteException
	 */
	public void seTeleporteA(Point objectif) throws RemoteException {
		// si la case n'est pas dans le champ de vision
		Point objectifRestreint = objectif;
		if(Calculs.distanceChebyshev(personnage.getPosition(), objectif) >= Constantes.VISION){
			objectifRestreint = Calculs.restreintPositionVision(objectif, personnage.getPosition());
		}
		
		
		// variation autour du point vise
		Random rand = new Random();
		int xvarie, yvarie;
		Point objectifVarie;
		
		// on tente une autre variation tant que la cible n'est pas une case vide
		do  {
			xvarie = (int) (objectifRestreint.getX()) + rand.nextInt(2) - 1;
			yvarie = (int) (objectifRestreint.getY()) + rand.nextInt(2) - 1;
			objectifVarie = new Point(xvarie, yvarie);
		}while (!Calculs.caseVide(objectifVarie, voisins));

		Point cible = Calculs.restreintPositionArene(objectifVarie);

		// on ne peut se teleporter que si le cooldown est a 0
		if (cible != null && personnage.getElement().getPassifs().get(Passif.TeleportationCoolDown) == 0) {
			personnage.setPosition(cible);
			personnage.getElement().getPassifs().put(Passif.TeleportationCoolDown, Constantes.CD_TELEPORTATION);
		}
	}
}

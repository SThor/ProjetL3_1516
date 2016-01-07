package client.strategie;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.personnage.Persons;
import utilitaires.Calculs;
import utilitaires.Constantes;

/* 
 * Cette classe met en place le comportement de type "Fuyard".
 * Celui-ci fuit les combats même si il pouvait les gagner en sélectionnant parmis les directions celle qui l'éloigne le plus de tous les ennemis en vue
 */
public class StratBourrin extends StrategiePersonnage {

	public StratBourrin	(String ipArene, int port, String ipConsole, String nom,
			String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger, Persons type) 
	{
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger, type);
	}

	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException 
	{
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
			console.setPhrase("On se fait chier dans cette aventure.");
			arene.deplace(refRMI, 0); 

		} else {
			int refCible = 0;
			//On fait la liste des personnages visibles
			HashMap<Integer, Point> personnages = new HashMap<Integer, Point>();
			Iterator<Integer> it = voisins.keySet().iterator();
			while(it.hasNext()){
				int reference = (int)it.next();
				Element test = arene.elementFromRef(reference);
				if( test instanceof Personnage ){
					personnages.put((Integer)reference, voisins.get(reference));
				}
			}
			if(!(personnages.isEmpty())){
				//On cherche le personnage le plus proche
				refCible =Calculs.chercheElementProche(position,personnages);
				//Si possible on le frappe
				if(Calculs.distanceChebyshev(position, arene.getPosition(refCible) )<= Constantes.DISTANCE_MIN_INTERACTION ){
					console.setPhrase("Je tabasse " + arene.elementFromRef(refCible).getNom()+" !");
					arene.lanceAttaque(refRMI, refCible);
				}
				//Sinon on le charge
				else{
					console.setPhrase("Je charge " + arene.elementFromRef(refCible).getNom()+" !");
					arene.deplace(refRMI, refCible );
				}
			}
			else{
				//Si pas de personnages visibles on cherche la potion la plus proche
				refCible = Calculs.chercheElementProche(position, voisins);
				if(Calculs.distanceChebyshev(position, arene.getPosition(refCible) )<= Constantes.DISTANCE_MIN_INTERACTION ){
					//si possible on la boit
					console.setPhrase("Je bois " + arene.elementFromRef(refCible).getNom()+" !");
					arene.ramassePotion(refRMI, refCible);
				}
				else{
					//sinon on s'avance vers elle	
					console.setPhrase("Je veux boire " + arene.elementFromRef(refCible).getNom()+" !");
					arene.deplace(refRMI, refCible );
				}
			}
		}
	}
}
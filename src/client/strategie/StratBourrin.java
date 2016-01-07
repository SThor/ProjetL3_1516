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
			int refIlemPlusProche = Calculs.chercheElementProche(position, voisins);
			Element elemPlusProche = arene.elementFromRef(refIlemPlusProche);
			if(elemPlusProche instanceof Personnage){
				if(Calculs.distanceChebyshev(position, arene.getPosition(refIlemPlusProche))< Constantes.DISTANCE_MIN_INTERACTION)
				{
					console.setPhrase("Je me bastonne avec " + elemPlusProche.getNom());
					arene.lanceAttaque(refRMI, refIlemPlusProche);
				}
			}
			else{
				console.setPhrase("Je charge !");
				arene.deplace(refRMI, trouverCible(position, voisins) );
			}
		}

	}

	private Point trouverCible(Point position, HashMap<Integer, Point> voisins) throws RemoteException 
	{
		IArene arene = console.getArene();
		Point[] possibilites = new Point[] { 
				new Point((int)position.getX() -1 , (int)position.getY() - 1),
				new Point((int)position.getX() -1 , (int)position.getY()    ),
				new Point((int)position.getX() -1 , (int)position.getY() + 1),
				new Point((int)position.getX()    , (int)position.getY() - 1),
				new Point((int)position.getX()    , (int)position.getY() + 1),
				new Point((int)position.getX() +1 , (int)position.getY() - 1),
				new Point((int)position.getX() +1 , (int)position.getY()    ),
				new Point((int)position.getX() +1 , (int)position.getY() + 1)
		};

		int[] distancesTotales = new int[8]; // La distance additionnée entre les ennemis et le personnage en choisissant la case i
		for(int i = 0; i < 8; i++){
			if(Calculs.estDansArene(possibilites[i])){
				Iterator<Integer> it = voisins.keySet().iterator();
				while(it.hasNext()){
					int reference = (int)it.next();
					if(arene.elementFromRef(reference) instanceof Personnage){
						distancesTotales[i] += Calculs.distanceChebyshev(possibilites[i], voisins.get((Integer) reference));
					}
				}
			}
		}
		int meilleurePossibilite = 0;
		int meilleureDistance = 0;
		for(int i = 0; i < 8; i++){
			if(meilleureDistance < distancesTotales[i]){
				meilleureDistance = distancesTotales[i];
				meilleurePossibilite = i;
			}
		}

		return possibilites[meilleurePossibilite];
	}
}
package client.strategie;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import utilitaires.Calculs;

/* 
 * Cette classe met en place le comportement de type "Fuyard".
 * Celui-ci fuit les combats m�me si il pouvait les gagner en s�lectionnant parmis les directions celle qui l'�loigne le plus de tous les ennemis en vue
 */
public class StratFuyard extends StrategiePersonnage {

	public StratFuyard(	String ipArene, int port, String ipConsole, String nom,
						String groupe, HashMap<Caracteristique, Integer> caracts,
						int nbTours, Point position, LoggerProjet logger) 
	{
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
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
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0); 

		} else {
			console.setPhrase("Je fuis !");
			arene.deplace(refRMI, trouverPointFuite(position, voisins) );
		}

	}

	private Point trouverPointFuite(Point position, HashMap<Integer, Point> voisins) throws RemoteException 
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

		int[] distancesTotales = new int[8]; // La distance additionn�e entre les ennemis et le personnage en choisissant la case i
		for(int i = 0; i < 8; i++){
			if(Calculs.estDansArene(possibilites[i])){
				Iterator it = voisins.keySet().iterator();
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
		
	/*	
		
		
		double meilleureMoyenneDistance = -1;
		int meilleurPoint = -1;
		int nbEnnemisProches = 0;
		IArene arene = console.getArene();
		for(int j = 0; j< possibilites.length; j++){
			if(Calculs.estDansArene(possibilites[j])){
				int distanceTotale = 0; //La distance additionn�e entre les ennemis et le personnage
				Iterator it = voisins.keySet().iterator();
				while(it.hasNext())
					try {
						int i = (int)it.next();
						if(arene.elementFromRef(i) instanceof Personnage){
							distanceTotale += Calculs.distanceChebyshev(possibilites[j], (Point) voisins.values().toArray()[i]);
							nbEnnemisProches ++;
						}
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}
				}
	//On choisi la direction qui nous �loigne le plus en moyenne
	if(distanceTotale / nbEnnemisProches < meilleureMoyenneDistance){
		meilleureMoyenneDistance = distanceTotale / nbEnnemisProches;
		meilleurPoint = j;
	}
}
}
return possibilites[meilleurPoint];
}
*/
}
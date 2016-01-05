package client.strategie;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import utilitaires.Calculs;

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

	private Point trouverPointFuite(Point position, HashMap<Integer, Point> voisins)
	{
		Point[] possibilites = new Point[] { new Point((int)position.getX() +1 , (int)position.getY() + 1),
				new Point((int)position.getX() +1 , (int)position.getY()    ),
				new Point((int)position.getX() +1 , (int)position.getY() - 1),
				new Point((int)position.getX()    , (int)position.getY() + 1),
				new Point((int)position.getX()    , (int)position.getY() - 1),
				new Point((int)position.getX() -1 , (int)position.getY() + 1),
				new Point((int)position.getX() -1 , (int)position.getY()    ),
				new Point((int)position.getX() -1 , (int)position.getY() - 1)
		};
		double meilleureMoyenneDistance = -1;
		int meilleurPoint = -1;
		for(int j = 0; j< possibilites.length; j++){
			if(Calculs.estDansArene(possibilites[j])){
				int distanceTotale = 0;
				for(int i = 0; i < voisins.size(); i++){
					distanceTotale += Calculs.distanceChebyshev(possibilites[j], voisins.get((Integer) i));
				}
				if(distanceTotale / voisins.size() < meilleureMoyenneDistance){
					meilleureMoyenneDistance = distanceTotale / voisins.size();
					meilleurPoint = j;
				}
			}
		}
		return possibilites[meilleurPoint];
	}
}
	
package client.strategie;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StratPoison extends StrategiePersonnage {
	
	ArrayList potionDejaEmpoisonnee;
	
	public StratPoison(	String ipArene, int port, String ipConsole, String nom,
			String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) 
	{
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
		potionDejaEmpoisonnee = new ArrayList();
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
			int refPotionPlusProche=-1;
			int distPotionPlusProche = 100;
			int refPersoPlusProche=-1;
			int distPersoPlusProche= 100;
			Iterator<Integer> it = voisins.keySet().iterator();
			while(it.hasNext()){
				int reference = (int)it.next();
				if(arene.elementFromRef(reference) instanceof Potion 
					&& distPotionPlusProche > Calculs.distanceChebyshev(position, voisins.get((Integer) reference)) 
					&& !potionDejaEmpoisonnee.contains(reference)){
					refPotionPlusProche = reference;
					distPotionPlusProche = Calculs.distanceChebyshev(position, voisins.get((Integer) reference));
				}else{
					if(distPotionPlusProche > Calculs.distanceChebyshev(position, voisins.get((Integer) reference))){
						refPersoPlusProche = reference;
						distPersoPlusProche = Calculs.distanceChebyshev(position, voisins.get((Integer) reference));
					}
				}
			}
			if (refPotionPlusProche != -1){
				if (distPotionPlusProche <= Constantes.DISTANCE_MIN_INTERACTION){
					console.setPhrase("Je vais empoisonner cette potion!");
					if(arene.empoisonne(refRMI, refPotionPlusProche)){
						potionDejaEmpoisonnee.add(refPotionPlusProche);
					}
				}else{
					console.setPhrase("Je vais vers la potion");
					arene.deplace(refRMI, refPotionPlusProche);
				}
			}else{
				if (distPersoPlusProche <= Constantes.DISTANCE_MIN_ATT_DISTANTE){ 
					String nom = arene.elementFromRef(refPersoPlusProche).getNom();
					console.setPhrase("Je vais annihiler "+nom+" grâce à mon arc.");
					arene.lanceAttaqueDistante(refRMI,refPersoPlusProche);
				}else{
					console.setPhrase("J'erre...");
					arene.deplace(refRMI, 0); 
				}
			}

		}

	}
}
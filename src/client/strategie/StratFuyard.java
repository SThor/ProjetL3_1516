package client.strategie;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Passif;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.element.personnage.Persons;
import utilitaires.Calculs;

/* 
 * Cette classe met en place le comportement de type "Fuyard".
 * Celui-ci fuit les combats même si il pouvait les gagner en sélectionnant parmis les directions celle qui l'éloigne le plus de tous les ennemis en vue
 */
public class StratFuyard extends StrategiePersonnage {

	/**
	 * Cree un fuyard, la console associee et sa strategie.
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param nom nom du personnage
	 * @param groupe groupe d'etudiants du personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 **/
	public StratFuyard(	String ipArene, int port, String ipConsole, String nom,
			String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger, Persons type) 
	{
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger, type);
	}

	/**
	 * Decrit la stratégie adoptée par un fuyard 
	 * Si aucun personnage est en vu, il erre à moins qu'une potion ne soit en vue dans ce cas il s'approche et l'utilise si il peut
	 * Sinon il fuit les personnages en vu. 
	 * Dans le cas ou il a un personnage en vu et  est coince contre un mur, il se teleporte aleatoirement 
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 **/
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
			boolean presMur = estPresDUnMur(possibilites);
			HashMap<Integer,Point> personnagesVoisins = getPersonnagesVoisins(voisins);
			boolean tpDispo = console.getPersonnage().getPassifs().get(Passif.TeleportationCoolDown)==0;

			if(!personnagesVoisins.isEmpty()&& presMur && tpDispo){
				console.setPhrase("Je me téléporte en urgence.");
				arene.teleporte(refRMI, 0);
			}
			else if(personnagesVoisins.isEmpty() ){
				int refcible = Calculs.chercheElementProche(position, voisins);
				Element elemPlusProche = arene.elementFromRef(refcible);
				if(elemPlusProche instanceof Potion) {
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI,refcible);
				}
			}
			else{
				console.setPhrase("Je fuis !");
				arene.deplace(refRMI, trouverPointFuite(position, voisins, possibilites) );
			}
		}

	}

	/**
	 * @param position position initiale du personnage dans l'arene
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @param possibilites les differentes possibilites de deplacement
	 * @return le point qui permet de s'éloigner le plus des différents personnages voisins
	 * @throws RemoteException
	 */
	private Point trouverPointFuite(Point position, HashMap<Integer, Point> voisins, Point[] possibilites ) throws RemoteException 
	{
		IArene arene = console.getArene();


		int[] distancesTotales = new int[8]; // La distance additionnée entre les ennemis et le personnage en choisissant la case i
		for(int i = 0; i < possibilites.length; i++){
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


	/**
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @return une Hashmap qui contient uniquement les personnages
	 * @throws RemoteException
	 */
	private HashMap<Integer, Point> getPersonnagesVoisins(HashMap<Integer,Point>  voisins) throws RemoteException{
		HashMap<Integer, Point> personnages = voisins;
		IArene arene = console.getArene();
		Iterator<Integer> it = personnages.keySet().iterator();
		while(it.hasNext()){
			int reference = (int)it.next();
			if(!(arene.elementFromRef(reference) instanceof Personnage)){
				personnages.remove(reference);
			}
		}
		return personnages;	
	}

	/**
	 * @param possibilites les differentes possibilites de deplacement
	 * @return si le personnage a une limite de l'arene dans une de ses possibilite de deplacement
	 */
	private boolean estPresDUnMur(Point[] possibilites){
		for(int i=0; i<8; i++){
			if(!Calculs.estDansArene(possibilites[i])) return true;
		}
		return false;
	}
}

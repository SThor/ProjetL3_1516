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
import serveur.element.personnage.Persons;
import utilitaires.Calculs;
import utilitaires.Constantes;

/* 
 * Cette classe met en place le comportement de type "Fuyard".
 * Celui-ci fuit les combats meme si il pouvait les gagner en selectionnant parmis les directions celle qui l'eloigne le plus de tous les ennemis en vue
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
	 * @param type 
	 **/
	public StratFuyard(	String ipArene, int port, String ipConsole, String nom,
			String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger, Persons type) 
	{
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger, type);
	}

	/**
	 * Decrit la strategie adoptee par un fuyard 
	 * Si aucun personnage est en vu, il erre a moins qu'une potion ne soit en vue dans ce cas il s'approche et l'utilise si il peut
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
			//On fait la liste des personnages visibles
			HashMap<Integer, Point> personnagesVoisins = new HashMap<Integer, Point>();
			Iterator<Integer> it = voisins.keySet().iterator();
			while(it.hasNext()){
				int reference = (int)it.next();
				Element test = arene.elementFromRef(reference);
				if( test instanceof Personnage ){
					personnagesVoisins.put((Integer)reference, voisins.get(reference));
				}
			}
			if(!(personnagesVoisins.isEmpty())){
				//Si un personnage est en vu on regarde les possibilites de fuite
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
				boolean tpDispo = console.getPersonnage().getPassifs().get(Passif.TeleportationCoolDown)==0;
				
				//Si on est contre un mur et que le CD de la teleportation est disponible, on se teleporte 
				if(presMur && tpDispo){
					console.setPhrase("Je me teleporte en urgence.");
					arene.teleporte(refRMI, 0);
				}
				else{
					//Dans les autres cas, que l'on ne soit pas contre un mur ou que le CD de la teleportation n'est pas disponible
					//On cherche  la case adjacente qui nous eloigne le plus des ennemis en vue
					int[] distancesTotales = new int[8]; // La distance additionnee entre les ennemis et le personnage en choisissant la case i
					for(int i = 0; i < possibilites.length; i++){
						if(Calculs.estDansArene(possibilites[i])){
							Iterator<Integer> it1 = personnagesVoisins.keySet().iterator();
							while(it1.hasNext()){
								int reference = it1.next();
								if(arene.elementFromRef(reference) instanceof Personnage){
									distancesTotales[i] += Calculs.distanceChebyshev(possibilites[i], personnagesVoisins.get((Integer) reference));
								}
							}
						}
					}
					int meilleurePossibilite = 0;
					int meilleureDistance = 0;
					for(int i = 0; i < possibilites.length; i++){
						if(meilleureDistance < distancesTotales[i]){
							meilleureDistance = distancesTotales[i];
							meilleurePossibilite = i;
						}
					}
					console.setPhrase("Je fuis et j'ai pas de rustine.");
					arene.deplace(refRMI, possibilites[meilleurePossibilite]);
				}
			}
			else{
				//Si pas de personnages visibles on cherche la potion la plus proche
				int refCible = Calculs.chercheElementProche(position, voisins);
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

	
	/**
	 * @param possibilites les differentes possibilites de deplacement
	 * @return si le personnage a une limite de l'arene dans une de ses possibilite de deplacement
	 */
	private boolean estPresDUnMur(Point[] possibilites){
		for(int i=0; i<possibilites.length; i++){
			if(!Calculs.estDansArene(possibilites[i])) return true;
		}
		return false;
	}
}

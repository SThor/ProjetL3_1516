package lanceur.lanceurPerso;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Random;

import client.strategie.StratArcher;
import lanceur.ErreurLancement;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.personnage.Persons;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Lance une Console avec un Element sur l'Arene. 
 * A lancer apres le serveur, eventuellement plusieurs fois.
 */
public class LanceArcher {
	
	private static String usage = "USAGE : java " + LancePersonnage.class.getName() + " [ port [ ipArene ] ]";

	public static void main(String[] args) {
		String nom = "Archer";
		String groupe = "G15"; 
		
		// nombre de tours pour ce personnage avant d'etre deconnecte 
		// (30 minutes par defaut)
		// si negatif, illimite
		int nbTours = Constantes.NB_TOURS_PERSONNAGE_DEFAUT;
		
		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				ErreurLancement.aide(usage);
			}
			
			if (args.length > 2) {
				ErreurLancement.TROP_ARGS.erreur(usage);
			}
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				ErreurLancement.PORT_NAN.erreur(usage);
			}
			
			if (args.length > 1) {
				ipArene = args[1];
			}
		}
		
		// creation du logger
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true, "personnage_" + nom + groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement du serveur
		try {
			String ipConsole = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("Lanceur", "Creation du personnage...");
			
			// caracteristiques du personnage
			HashMap<Caracteristique, Integer> caracts = new HashMap<Caracteristique, Integer>();
			// la force n'a pas sa valeur par defaut
			caracts.put(Caracteristique.FORCE, 
					Calculs.valeurCaracAleatoire(Caracteristique.FORCE)); 
			
			Point position = Calculs.positionAleatoireArene();
			
			Persons type;
			switch (new Random().nextInt(2)){
				case 1: type = Persons.ELFE; break;
				case 2: type = Persons.ORKS; break;
				default: type = Persons.HUMAIN; break;
			}
			
			new StratArcher(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger, type);
			logger.info("Lanceur", "Creation du personnage reussie");
			
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}

package lanceur;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;

import client.personnage.PersonnageTest;
import utilitaires.Calculs;
import utilitaires.logger.MyLogger;

/**
 * Lance une Console avec un Element sur l'Arene (apres lancement Arene). A lancer plusieurs fois.
 */
public class LancePersonnage {
	
	private static String USAGE = "USAGE : java " + LancePersonnage.class.getName() + " [ port [ nom_arene ] ]";

	public static void main(String[] args) {
		// init des variables
		String nom = "Truc";
		String groupe = "B"+Calculs.randomNumber(0,99); // REMPLACER CETTE LIGNE PAR VOTRE NUMERO DE GROUPE
		String ipConsole = null;
		
		// init des arguments
		int port = 5099;
		String ipArene = "localhost";
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				Erreur.help(USAGE);
			}
			
			if (args.length > 2) {
				Erreur.too_much_arg.erreur(USAGE);
			}
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				Erreur.port_NaN.erreur(USAGE);
			}
			
			if (args.length == 2) {
				ipArene = args[1];
			}
		}
		
		// création du logger
		MyLogger logger = null;
		try {
			logger = new MyLogger(true, "personnage_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		// lancement du serveur
		try {
			//ipConsole = "localhost";
			ipConsole = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("lanceur", "Creation du personnage...");
			Point position = new Point(Calculs.randomNumber(0,100), Calculs.randomNumber(0,100));
			
			new PersonnageTest(nom, groupe, position, port, ipArene, ipConsole, logger);
			logger.info("lanceur", "Creation du personnage reussie");
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
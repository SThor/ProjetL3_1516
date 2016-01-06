
package lanceur;

import java.io.IOException;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.*;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class LancePotion {
	
	private static String usage = "USAGE : java " + LancePotion.class.getName() + " [ port [ ipArene ] ]";

	public static void main(String[] args) {
		String nom = "Anduril";
		String groupe = "G15"; 
		
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
			logger = new LoggerProjet(true, "potion_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(ipArene, port, "Arene"));
			logger.info("Lanceur", "Lancement de la potion sur le serveur...");
			
			// le choix de la potion a lancer se fait aleatoirement
			double rd = Math.random();
			double i = Constantes.TX_POTION_INVI;
			if (rd < i){
				arene.ajoutePotion(new PotionInvisibilite(groupe), Calculs.positionAleatoireArene());
				logger.info("Lanceur", "Lancement de la potion d'invisibilite reussi");
			}else{
				if (i<=rd && rd<i+Constantes.TX_POTION_CONFU){
					arene.ajoutePotion(new PotionConfusion(groupe), Calculs.positionAleatoireArene());
					logger.info("Lanceur", "Lancement de la potion de confusion reussi");
				}else{
					i+=Constantes.TX_POTION_CONFU;
					if (i<=rd && rd<i+Constantes.TX_POTION_SOIN){
						arene.ajoutePotion(new PotionSoin(groupe), Calculs.positionAleatoireArene());
						logger.info("Lanceur", "Lancement de la potion de soin reussi");
					}else{
						arene.ajoutePotion(new PotionCarac(groupe), Calculs.positionAleatoireArene());
						logger.info("Lanceur", "Lancement de la potion de boost reussi");
					}
				}
			}			
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}

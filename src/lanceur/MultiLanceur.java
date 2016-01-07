package lanceur;

import lanceur.lanceurPerso.LanceArcher;
import lanceur.lanceurPerso.LanceFuyard;
import lanceur.lanceurPerso.LancePoison;
import lanceur.lanceurPerso.LanceTeleporteur;

public class MultiLanceur {
	public static void main(String[] args) {
		LanceArene.main(args);
		LanceIHM.main(args);
		for(int i = 0; i < 10 ; i++){
			LancePotion.main(args);
		}
		for(int i = 0; i < 2 ; i++){
			LancePoison.main(args);
			LanceArcher.main(args);
			//LanceFuyard.main(args);
			LanceTeleporteur.main(args);
		}
	}
}

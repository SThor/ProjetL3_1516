package lanceur;

import lanceur.lanceurPerso.LanceArcher;
import lanceur.lanceurPerso.LanceBourrin;
import lanceur.lanceurPerso.LanceFuyard;
import lanceur.lanceurPerso.LancePoison;
import lanceur.lanceurPerso.LanceTeleporteur;

public class MultiLanceur {
	public static void main(String[] args) {
		LanceArene.main(args);
		LanceIHM.main(args);
		for(int i = 0; i < 5 ; i++){
			LancePotion.main(args);
		}

		LancePoison.main(args);
		LanceArcher.main(args);
		LanceFuyard.main(args);
		LanceTeleporteur.main(args);
		LanceBourrin.main(args);


	}
}

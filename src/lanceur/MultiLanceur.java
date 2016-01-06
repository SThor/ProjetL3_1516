package lanceur;

import lanceur.lanceurPerso.LancePoison;

public class MultiLanceur {
	public static void main(String[] args) {
		LanceArene.main(args);
		LanceIHM.main(args);
		for(int i = 0; i < 10 ; i++){
			LancePotion.main(args);
		}
		for(int i = 0; i < 10 ; i++){
			LancePoison.main(args);
		}
	}
}

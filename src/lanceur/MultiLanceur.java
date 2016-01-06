package lanceur;

import lanceurPerso.LancePoison;

public class MultiLanceur {
	public static void main(String[] args) {
		LanceArene.main(null);
		LanceIHM.main(null);
		for(int i = 0; i < 10 ; i++){
			LancePotion.main(null);
		}
		for(int i = 0; i < 10 ; i++){
			LancePoison.main(null);
		}
	}
}

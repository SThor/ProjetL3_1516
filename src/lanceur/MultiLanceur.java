package lanceur;

import lanceurPerso.LancePoison;

public class MultiLanceur {
	public static void main(String[] args) {
		//LanceArene lanceArene = new LanceArene();
		//LanceIHM lanceIHM = new LanceIHM();
		LancePotion lancepotion;
		LancePoison lancepoison;
		//lanceArene.main(null);
		//lanceIHM.main(null);
		for(int i = 0; i < 10 ; i++){
			lancepotion = new LancePotion();
			lancepotion.main(null);
		}
		for(int i = 0; i < 10 ; i++){
			lancepoison = new LancePoison();
			lancepoison.main(null);
		}
	}
}

package lanceur;

import lanceurPerso.LancePoison;

public class MultiLanceur {
	public static void main(String[] args) {
		//LanceArene lanceArene = new LanceArene();
		//LanceIHM lanceIHM = new LanceIHM();
		//lanceArene.main(null);
		//lanceIHM.main(null);
		for(int i = 0; i < 10 ; i++){
			LancePotion.main(null);
		}
		for(int i = 0; i < 10 ; i++){
			LancePoison.main(null);
		}
	}
}

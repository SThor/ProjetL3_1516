package lanceur;

import lanceur.*;
import lanceur.lanceurPerso.*;

public class MultiLanceur {
	public static void main(String[] args) {
		LanceArene lanceArene = new LanceArene();
		LanceIHM lanceIHM = new LanceIHM();
		LancePotion lancepotion;
		LancePoison lancepoison;
		lanceArene.main(args);
		lanceIHM.main(args);
		for(int i = 0; i < 10 ; i++){
			lancepotion = new LancePotion();
			lancepotion.main(args);
		}
		for(int i = 0; i < 10 ; i++){
			lancepoison = new LancePoison();
			lancepoison.main(args);
		}
	}
}

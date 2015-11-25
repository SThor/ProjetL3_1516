package utilitaires;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import serveur.Arene;
import serveur.element.Caracteristique;

/**
 * Classe regroupant quelques methodes utiles pour l'arene (distance, case vide,
 * elements voisins...).
 */
public class Calculs {

	private static long token;

	/**
	 * Renvoie la distance Chebyshev entre deux points
	 * @param p1 le premier point
	 * @param p2 le deuxieme point
	 * @return un entier representant la distance
	 */
	public static int distanceChebyshev(Point p1, Point p2) {
		return Math.max(Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));
	}

	/**
	 * Verifie si un element parmi les voisins occupe la position
	 * @param p une position   
	 * @param voisins des elements (Point)
	 * @return true si la case est vide et false si la case est occupe
	 */
	public static boolean caseVide(Point p, Hashtable<Integer, Point> voisins){
		boolean trouve = false;
		Point pAux = null;
		Enumeration<Point> enump = voisins.elements();
		while (!trouve && enump.hasMoreElements()) {
			pAux = enump.nextElement();
			trouve = p.equals(pAux); 
		}		
		return !trouve;
	}
	
	/** 
	 * Renvoie le meilleur point a occuper par l'element courant dans la direction de la cible
	 * @param depart le point sur lequel se trouve l'element courant
	 * @param objectif le point sur lequel se trouve la cible
	 * @param voisins le positionement des autres elements dans l'arene
	 * @return le meilleur point libre dans la direction de la cible
	 */
	public static Point meilleurPoint(Point depart, Point objectif, Hashtable<Integer, Point> voisins){
		//liste contenant tous les positions vers lesquelles l'element peut avancer
		ArrayList<Point> listePossibles = new ArrayList<Point>();		
		//pour chaque de 8 cases autour de lui
		for (int i=-1;i<=1;i++){
			for (int j=-1;j<=1;j++){
				if ((i!=0) || (j!=0))  {
 					//on ajoute la position (en valeur absolue pour eviter de sortir du cadre)
					listePossibles.add(new Point(Math.abs(depart.x+i),Math.abs(depart.y+j)));
				}
			}
		}
		//organise les points de la liste du plus pres vers le plus eloigne de la cible
		Collections.sort(listePossibles,new PointComp(objectif));		
		//cherche la case vide la plus proche de la cible
		boolean ok = false;
		int i=0;
		Point res=null;
		while (!ok & i<listePossibles.size()) {
			res = listePossibles.get(i);
			ok = caseVide(res, voisins);
			i++;
		}
		//renvoie cette case
		return res;
	}

	/**
	 * Cherche l'element le plus proche vers lequel se didiger
	 * @param origine position à partir de laquelle on cherche
	 * @param voisins liste des voisins
	 * @return reference de l'élément le plus proche, 0 si il n'y en a pas
	 */
	public static int chercherElementProche(Point origine, Hashtable<Integer, Point> voisins) {
		int distPlusProche = 100;
		int refPlusProche = 0;
		for(Integer refVoisin : voisins.keySet()) {
			Point target = voisins.get(refVoisin);
			if (Calculs.distanceChebyshev(origine, target)<distPlusProche) {
				distPlusProche = Calculs.distanceChebyshev(origine, target);
				refPlusProche = refVoisin;
			}
		}		
		return refPlusProche;
	}
	
	/**
	 * Genere un entier correpondant à une caractéristique
	 * @param c caractéristique pour laquelle on souhaite une valeur
	 * @return valeur générée
	 */
	public static int randomCarac(Caracteristique c) {
		return randomNumber(c.min, c.max);
	}
	
	/**
	 * Genere un entier dans un interval
	 * @param min borne inferieure de l'interval
	 * @param max borne superieure de l'interval
	 * @return valeur générée
	 */
	public static int randomNumber(int min, int max) {
		Random r = new Random(System.currentTimeMillis() + token);
		if (max < 0){
			return r.nextInt(500-min)+min;
		}
		int res = r.nextInt(max-min)+min;
		token = res;
		return res;
	}
	
	/**
	 * Cape une valeur correspondant à une caractéristique
	 * @param c caractéristique pour laquelle on souhaite caper une valeur
	 * @param val valeur à caper
	 * @return valeur capée
	 */
	public static int caperCarac(Caracteristique c, int val) {		
		return caperNumber(c.min, c.max, val);
	}

	/**
	 * Cape une valeur dans un interval
	 * @param min borne inferieure de l'interval
	 * @param max borne superieure de l'interval
	 * @param val valeur à caper
	 * @return valeur capée
	 */
	public static int caperNumber(int min, int max, int val) {
		if (max < 0){
			return Math.max(val, min);
		}		
		return Math.min(Math.max(val, min), max);
	}
	
	public static Point caperPositionArene(Point position){
		int xMin = Arene.XMIN;
		int xMax = Arene.XMAX;
		int yMin = Arene.YMIN;
		int yMax = Arene.YMAX;
		
		return new Point(caperNumber(xMin, xMax, position.x), caperNumber(yMin, yMax, position.y));
	}

	public static Point randomPosition() {
		return new Point(
				Calculs.randomNumber(Arene.XMIN, Arene.XMAX), 
				Calculs.randomNumber(Arene.YMIN, Arene.YMAX));
	}
}
package com.ican.anamorphoses_jsdn;

import android.content.Context;

import com.ican.anamorphoses_jsdn.activity.AnamorphosisDifficulty;
import com.ican.anamorphoses_jsdn.network.Client;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Clément on 12/04/2017.
 */

 
 
public class AnamorphGameManager {

	public static final int VICTORY_ANAMORPH_NB = 4;

		// Liste de toutes les anamorphoses
	static private ArrayList<Anamorphosis> anamorphosisDict;
	static public ArrayList<Anamorphosis> getAnamorphosisDict() {
        return anamorphosisDict;
	}
	static public void setAnamorphosisDict(ArrayList<Anamorphosis> anamorphosisDict) {
		AnamorphGameManager.anamorphosisDict = anamorphosisDict;
	}


		// Surnom du joueur utilisant l'application
	static private String playerNickname;
	static public String getplayerNickname() {
		return playerNickname;
	}
	static public void setplayerNickname(String playerNickname) {
		AnamorphGameManager.playerNickname = playerNickname;
	}


		// Anamorphose en objectif courant
	static private Anamorphosis targetAnamorphosis;
	static public Anamorphosis getTargetAnamorphosis() {
		return targetAnamorphosis;
	}
	static public void setTargetAnamorphosis(Anamorphosis targetAnamorphosis) {
		AnamorphGameManager.targetAnamorphosis = targetAnamorphosis;
	}


		// Anamorphoses qui ont été précédemment validées
	static private ArrayList<Anamorphosis> validatedAnamorphosis;
	static public ArrayList<Anamorphosis> getValidatedAnamorphosis() {
		return validatedAnamorphosis;
	}
	static public void setValidatedAnamorphosis(ArrayList<Anamorphosis> validatedAnamorphosis) {
		AnamorphGameManager.validatedAnamorphosis = validatedAnamorphosis;
	}


		// Score du joueur pour la manche en cours
	static private int currentPlayerScore;
	static public int getCurrentPlayerScore() { return currentPlayerScore;}
	static public void setCurrentPlayerScore (int currentPLayerScore) {
		AnamorphGameManager.currentPlayerScore = currentPLayerScore;
	}


		// Score du joueur pour toute la partie en cours
	static private int gamePlayerScore;
	static public int getGamePlayerScore() { return gamePlayerScore;}
	static public void setGamePlayerScore (int gamePlayerScore) {
		AnamorphGameManager.gamePlayerScore = gamePlayerScore;
	}


		// Score du nickname commun à toute les parties
	static private int totalPlayerScore;
	static public int getTotalPlayerScore() { return totalPlayerScore;}
	static public void setTotalPlayerScore (int totalPlayerScore) {
		AnamorphGameManager.totalPlayerScore = totalPlayerScore;
	}


	// Nom de la room dans lequel le joueur est
	static private String roomTitle;
	static public String getTitleRoom() { return roomTitle;}
	static public void setTitleRoom (String roomTitle) {
		AnamorphGameManager.roomTitle = roomTitle;
	}

	// Initalisation du dictionnaire d'anamorphose
	static public void InitAnamorphosisDict(Context context)
    {
        AnamorphGameManager.anamorphosisDict = new ArrayList<Anamorphosis>();
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_1_s, R.drawable.anamorphosis_1_l, AnamorphosisDifficulty.EASY, "triangle.svm"));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_2_s, R.drawable.anamorphosis_2_l, AnamorphosisDifficulty.EASY, "carre.svm"));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_3_s, R.drawable.anamorphosis_3_l, AnamorphosisDifficulty.EASY, "pentagone.svm"));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_4_s, R.drawable.anamorphosis_4_l, AnamorphosisDifficulty.MEDIUM, "sablier2.svm"));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_5_s, R.drawable.anamorphosis_5_l, AnamorphosisDifficulty.HARD, "tripik.svm"));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_6_s, R.drawable.anamorphosis_6_l, AnamorphosisDifficulty.HARD, "trefle.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_7_s, R.drawable.anamorphosis_7_l, AnamorphosisDifficulty.HARD, "lajesaispas.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_8_s, R.drawable.anamorphosis_8_l, AnamorphosisDifficulty.HARD, "rond.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_9_s, R.drawable.anamorphosis_9_l, AnamorphosisDifficulty.HARD, "3rond.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_10_s, R.drawable.anamorphosis_10_l, AnamorphosisDifficulty.HARD, "nuage.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_11_s, R.drawable.anamorphosis_11_l, AnamorphosisDifficulty.HARD, "nuage2.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_12_s, R.drawable.anamorphosis_12_l, AnamorphosisDifficulty.HARD, "escalier.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_13_s, R.drawable.anamorphosis_13_l, AnamorphosisDifficulty.HARD, "tripentagone.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_14_s, R.drawable.anamorphosis_14_l, AnamorphosisDifficulty.HARD, "choufleur.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_15_s, R.drawable.anamorphosis_15_l, AnamorphosisDifficulty.HARD, "quille.svm"));
		AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_16_s, R.drawable.anamorphosis_16_l, AnamorphosisDifficulty.HARD, "etoile.svm"));
    }

    public static Anamorphosis getRandomMediumAnamorphosis() {
		/*
		return anamorphosisDict
				.stream()
				.filter(a -> a.getDifficulty() == AnamorphosisDifficulty.MEDIUM)
				.collect(Collectors.toCollection());
		*/
		// FUCKING API LEVEL 19 !! This require API level 24

		ArrayList<Anamorphosis> mediums = new ArrayList<>();
		for (Anamorphosis a : anamorphosisDict) {
			if (a.getDifficulty() == AnamorphosisDifficulty.MEDIUM) {
				mediums.add(a);
			}
		}
		return mediums.get(new Random().nextInt(mediums.size()));
	}

	private static Client gameClient;

	public static Client getGameClient() {
		return gameClient;
	}

	public static void setGameClient(Client gameClient) {
		AnamorphGameManager.gameClient = gameClient;
	}


}

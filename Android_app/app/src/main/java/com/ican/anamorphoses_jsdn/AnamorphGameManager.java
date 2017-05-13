package com.ican.anamorphoses_jsdn;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.ican.anamorphoses_jsdn.resource.AnamorphDictionary;

import java.util.ArrayList;
import java.util.List;

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


	// Initalisation du dictionnaire d'anamorphose
	static public void InitAnamorphosisDict(Context context)
    {
        AnamorphGameManager.anamorphosisDict = new ArrayList<Anamorphosis>();
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_1_s, R.drawable.anamorphosis_1_l, AnamorphosisDifficulty.EASY));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_2_s, R.drawable.anamorphosis_2_l, AnamorphosisDifficulty.EASY));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_3_s, R.drawable.anamorphosis_3_l, AnamorphosisDifficulty.MEDIUM));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_4_s, R.drawable.anamorphosis_4_l, AnamorphosisDifficulty.MEDIUM));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_5_s, R.drawable.anamorphosis_5_l, AnamorphosisDifficulty.HARD));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_6_s, R.drawable.anamorphosis_6_l, AnamorphosisDifficulty.HARD));
    }

}

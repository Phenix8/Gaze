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


	// Initalisation du dictionnaire d'anamorphose
	static public void InitAnamorphosisDict(Context context)
    {
        AnamorphGameManager.anamorphosisDict = new ArrayList<Anamorphosis>();
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_1, AnamorphosisDifficulty.EASY));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_2, AnamorphosisDifficulty.EASY));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_3, AnamorphosisDifficulty.MEDIUM));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_4, AnamorphosisDifficulty.MEDIUM));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_5, AnamorphosisDifficulty.HARD));
        AnamorphGameManager.anamorphosisDict.add(new Anamorphosis(R.drawable.anamorphosis_6, AnamorphosisDifficulty.HARD));
    }

}

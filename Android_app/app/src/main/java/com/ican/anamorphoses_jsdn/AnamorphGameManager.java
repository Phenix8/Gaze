package com.ican.anamorphoses_jsdn;

import java.util.List;

/**
 * Created by Clément on 12/04/2017.
 */

 
 
public class AnamorphGameManager {

		// Liste de toutes les anamorphoses
	static private List<Anamorphosis> anamorphosisDict;

	static public List<Anamorphosis> getAnamorphosisDict() {
		return anamorphosisDict;
	}
	static public void setAnamorphosisDict(List<Anamorphosis> anamorphosisDict) {
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
	static private List<Anamorphosis> validatedAnamorphosis;

	static public List<Anamorphosis> getValidatedAnamorphosis() {
		return validatedAnamorphosis;
	}
	static public void setValidatedAnamorphosis(List<Anamorphosis> validatedAnamorphosis) {
		AnamorphGameManager.validatedAnamorphosis = validatedAnamorphosis;
	}

}

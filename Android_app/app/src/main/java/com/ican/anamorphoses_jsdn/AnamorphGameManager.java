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
	private String playerNickname;

	public String getplayerNickname() {
		return playerNickname;
	}

	public void setplayerNickname(String playerNickname) {
		this.playerNickname = playerNickname;
	}

		// Anamorphose en objectif courant
	private Anamorphosis targetAnamorphosis;

	public Anamorphosis getTargetAnamorphosis() {
		return targetAnamorphosis;
	}

	public void setTargetAnamorphosis(Anamorphosis targetAnamorphosis) {
		this.targetAnamorphosis = targetAnamorphosis;
	}

		// Anamorphoses qui ont été précédemment validées
	private List<Anamorphosis> validatedAnamorphosis;

	public List<Anamorphosis> getValidatedAnamorphosis() {
		return validatedAnamorphosis;
	}

	public void setValidatedAnamorphosis(List<Anamorphosis> validatedAnamorphosis) {
		this.validatedAnamorphosis = validatedAnamorphosis;
	}

}

package com.ican.anamorphoses_jsdn;

/**
 * Created by Clément on 12/04/2017.
 */

public class Anamorphosis {

	// Identifiant unique de l'anamorphose
	private int id;

	public void getId(int id)
	{	this.id = id;	}

	public int setId()
	{	return this.id;	}


	// Nom de la ressource image de l'anamorphose
	private String fileName;

	public void getFileName(String fileName)
	{	this.fileName =  fileName;	}

	public String setFileName()
	{	return this.fileName;	}


	// Niveau de difficulté (facile, moyen, difficile)
	private AnamorphosisDifficulty difficulty;

	public void getDifficulty(AnamorphosisDifficulty difficulty)
	{	this.difficulty =  difficulty;	}

	public AnamorphosisDifficulty setDifficulty()
	{	return this.difficulty;	}
	
}

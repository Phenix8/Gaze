package com.ican.anamorphoses_jsdn;

import android.graphics.drawable.Drawable;

/**
 * Created by Clément on 12/04/2017.
 */

public class Anamorphosis {

    private int idCounter = 0;

	// Identifiant unique de l'anamorphose
	private int id;

	public void setId(int id)
	{	this.id = id;	}
	public int getId()
	{	return this.id;	}


	// Nom de la ressource image de l'anamorphose
	private int drawableImage;

	public void setDrawableImage(int drawableImage)
	{	this.drawableImage =  drawableImage;	}
	public int getDrawableImage()
	{	return this.drawableImage;	}


	// Niveau de difficulté (facile, moyen, difficile)
	private AnamorphosisDifficulty difficulty;

	public void setDifficulty(AnamorphosisDifficulty difficulty)
	{	this.difficulty =  difficulty;	}
	public AnamorphosisDifficulty getDifficulty()
	{	return this.difficulty;	}


    // CONSTRUCTEUR
	public Anamorphosis(int drawableImage, AnamorphosisDifficulty difficulty)
	{
		this.id = idCounter++;
        this.drawableImage = drawableImage;
        this.difficulty = difficulty;
	}
}

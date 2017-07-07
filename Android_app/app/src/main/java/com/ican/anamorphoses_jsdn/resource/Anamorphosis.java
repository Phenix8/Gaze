package com.ican.anamorphoses_jsdn.resource;

import com.ican.anamorphoses_jsdn.activity.AnamorphosisDifficulty;

import java.io.Serializable;

/**
 * Created by Clément on 12/04/2017.
 */

public class Anamorphosis implements Serializable {

    private static int idCounter = 0;

    /**
     * Identifiant de l'anamorphose (attribué automatiquement)
     */
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }


    /**
     * Identifiant du petit icon de l'anamorphose.
     */
    private int drawableImage;

    public void setDrawableImage(int drawableImage) {
        this.drawableImage = drawableImage;
    }

    public int getDrawableImage() {
        return this.drawableImage;
    }

    /**
     * Identifiant du grand icon de l'anamorphose.
     */
    private int largeDrawableImage;

    public void setLargeDrawableImage(int largeDrawableImage) {
        this.largeDrawableImage = largeDrawableImage;
    }

    public int getLargeDrawableImage() {
        return this.largeDrawableImage;
    }


    /**
     * Niveau de difficulté (facile, moyen, difficile)
     */
    private AnamorphosisDifficulty difficulty;

    public void setDifficulty(AnamorphosisDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public AnamorphosisDifficulty getDifficulty() {
        return this.difficulty;
    }

    /**
     * Nom du detecteur utilise pour la detection de cette anamorphose.
     * Les detecteurs sont stockes dans assets/detectors.
     */
    private String detectorName;

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(String detectorName) {
        this.detectorName = detectorName;
    }

    /**
     * Contructeur complement renseigne.
     * @param drawableImage
     * @param largeDrawableImage
     * @param difficulty
     * @param detectorName
     */
    public Anamorphosis(int drawableImage, int largeDrawableImage, AnamorphosisDifficulty difficulty, String detectorName) {
        this.id = idCounter++;
        this.drawableImage = drawableImage;
        this.largeDrawableImage = largeDrawableImage;
        this.difficulty = difficulty;
        this.detectorName = detectorName;
    }
}

package com.bof.gaze.model;


import java.io.Serializable;

public class Player implements Serializable {

    private String name;
    private int score;
    private boolean ready;
    private String playerId;
    private int nbFoundAnamorphosis;

    public Player(String name, int score, boolean ready, String playerId) {
        this.name = name;
        this.score = score;
        this.ready = ready;
        this.playerId = playerId;
        this.nbFoundAnamorphosis = 0;
    }

    public Player(String name, int score, boolean ready, String playerId, int nbFoundAnamorphosis) {
        this.name = name;
        this.score = score;
        this.ready = ready;
        this.playerId = playerId;
        this.nbFoundAnamorphosis = nbFoundAnamorphosis;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public boolean isReady() {
        return ready;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getNbFoundAnamorphosis() { return nbFoundAnamorphosis;}

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setNbFoundAnamorphosis(int nbFoundAnamorphosis) { this.nbFoundAnamorphosis = nbFoundAnamorphosis; }

    @Override
    public String toString() {
        return String.format("%s : %s", getName(), isReady() ? "ready" : "not ready");
    }
}

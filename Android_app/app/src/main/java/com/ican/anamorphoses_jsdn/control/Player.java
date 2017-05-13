package com.ican.anamorphoses_jsdn.control;


public class Player {

    private String name;
    private int score;
    private boolean ready;
    private String playerId;

    public Player(String name, int score, boolean ready, String playerId) {
        this.name = name;
        this.score = score;
        this.ready = ready;
        this.playerId = playerId;
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

    @Override
    public String toString() {
        return String.format("%s : %s", getName(), isReady() ? "ready" : "not ready");
    }
}

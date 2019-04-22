package com.bof.gaze.model;

import com.bof.gaze.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
public abstract class AnamorphDictionary {

	private List<Anamorphosis> all;

	private ArrayList<Anamorphosis> easy = new ArrayList<>();
	private ArrayList<Anamorphosis> medium = new ArrayList<>();
	private ArrayList<Anamorphosis> hard = new ArrayList<>();

	private ArrayList<Anamorphosis> alreadyValidatedEasy = new ArrayList<>();
	private ArrayList<Anamorphosis> alreadyValidatedMedium = new ArrayList<>();
	private ArrayList<Anamorphosis> alreadyValidatedHard = new ArrayList<>();

	private Random rng = new Random();

	// Initalisation du dictionnaire d'anamorphose
	protected abstract List<Anamorphosis> loadAnamorphosis();

    private void sortAnamorphosisByDifficulty() {
		for (Anamorphosis a : all) {
			switch (a.getDifficulty()) {
				case EASY:
					easy.add(a);
				break;

				case MEDIUM:
					medium.add(a);
				break;

				case HARD:
					hard.add(a);
				break;
			}
		}
	}

    public AnamorphDictionary() {
		all = loadAnamorphosis();
		sortAnamorphosisByDifficulty();
	}

	public void setAlreadyValidated(Anamorphosis a) {
		switch (a.getDifficulty()) {
			case EASY:
				alreadyValidatedEasy.add(a);
			break;

			case MEDIUM:
				alreadyValidatedMedium.add(a);
			break;

			case HARD:
				alreadyValidatedHard.add(a);
			break;
		}
	}

	public Anamorphosis getRandom(Anamorphosis.Difficulty difficulty, boolean notAlreadyValidated) {
		List<Anamorphosis> usedList;
		List<Anamorphosis> usedAlreadyValidatedList;

		switch (difficulty) {
			case EASY:
				usedList = easy;
				usedAlreadyValidatedList = alreadyValidatedEasy;
			break;

			case MEDIUM:
				usedList = medium;
				usedAlreadyValidatedList = alreadyValidatedMedium;
			break;

			case HARD:
				usedList = hard;
				usedAlreadyValidatedList = alreadyValidatedHard;
			break;

			default:
				usedList = all;
				usedAlreadyValidatedList = new ArrayList<Anamorphosis>();
				usedAlreadyValidatedList.addAll(alreadyValidatedEasy);
				usedAlreadyValidatedList.addAll(alreadyValidatedMedium);
				usedAlreadyValidatedList.addAll(alreadyValidatedHard);
		}

		if (usedAlreadyValidatedList.size() == usedList.size()) {
			if (usedList == all) {
				alreadyValidatedEasy.clear();
				alreadyValidatedMedium.clear();
				alreadyValidatedHard.clear();
			} else {
				usedAlreadyValidatedList.clear();
			}
		}

		ArrayList<Anamorphosis> copy = new ArrayList<>(usedList);

		copy.removeAll(usedAlreadyValidatedList);

		return copy.get(rng.nextInt(copy.size()));
	}

	public Anamorphosis getById(int id) {
        for (Anamorphosis a : all) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }
}

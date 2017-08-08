package com.bof.gaze.model;

import com.bof.gaze.R;

import java.util.ArrayList;
import java.util.Random;
 
public class AnamorphDictionary {

	private ArrayList<Anamorphosis> all = new ArrayList<>();

	private ArrayList<Anamorphosis> easy = new ArrayList<>();
	private ArrayList<Anamorphosis> medium = new ArrayList<>();
	private ArrayList<Anamorphosis> hard = new ArrayList<>();

	private ArrayList<Anamorphosis> alreadyValidatedEasy = new ArrayList<>();
	private ArrayList<Anamorphosis> alreadyValidatedMedium = new ArrayList<>();
	private ArrayList<Anamorphosis> alreadyValidatedHard = new ArrayList<>();

	private Random rng = new Random();

	// Initalisation du dictionnaire d'anamorphose
	private void loadAnamorphosis()
    {
		all.add(new Anamorphosis(R.drawable.anamorphosis_1_s, R.drawable.anamorphosis_1_l, Anamorphosis.Difficulty.EASY, "triangle.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_6_s, R.drawable.anamorphosis_6_l, Anamorphosis.Difficulty.EASY, "trefle.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_3_s, R.drawable.anamorphosis_3_l, Anamorphosis.Difficulty.EASY, "pentagone.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_15_s, R.drawable.anamorphosis_15_l, Anamorphosis.Difficulty.EASY, "quille.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_4_s, R.drawable.anamorphosis_4_l, Anamorphosis.Difficulty.EASY, "sablier2.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_9_s, R.drawable.anamorphosis_9_l, Anamorphosis.Difficulty.MEDIUM, "3rond.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_13_s, R.drawable.anamorphosis_13_l, Anamorphosis.Difficulty.MEDIUM, "tripentagone.svm"));

		all.add(new Anamorphosis(R.drawable.anamorphosis_16_s, R.drawable.anamorphosis_16_l, Anamorphosis.Difficulty.HARD, "etoile.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_2_s, R.drawable.anamorphosis_2_l, Anamorphosis.Difficulty.HARD, "carre.svm"));
		//all.add(new Anamorphosis(R.drawable.anamorphosis_14_s, R.drawable.anamorphosis_14_l, Anamorphosis.Difficulty.HARD, "choufleur.svm"));
		//all.add(new Anamorphosis(R.drawable.anamorphosis_5_s, R.drawable.anamorphosis_5_l, Anamorphosis.Difficulty.HARD, "tripik.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_8_s, R.drawable.anamorphosis_8_l, Anamorphosis.Difficulty.MEDIUM, "rond.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_10_s, R.drawable.anamorphosis_10_l, Anamorphosis.Difficulty.MEDIUM, "nuage.svm"));
        all.add(new Anamorphosis(R.drawable.anamorphosis_18_s, R.drawable.anamorphosis_18_l, Anamorphosis.Difficulty.MEDIUM, "sablier.svm"));
        all.add(new Anamorphosis(R.drawable.anamorphosis_19_s, R.drawable.anamorphosis_19_l, Anamorphosis.Difficulty.MEDIUM, "bouteille.svm"));

		all.add(new Anamorphosis(R.drawable.anamorphosis_7_s, R.drawable.anamorphosis_7_l, Anamorphosis.Difficulty.MEDIUM, "lajesaispas.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_12_s, R.drawable.anamorphosis_12_l, Anamorphosis.Difficulty.HARD, "escalier.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_17_s, R.drawable.anamorphosis_17_l, Anamorphosis.Difficulty.HARD, "L.svm"));




    }

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
		loadAnamorphosis();
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
		ArrayList<Anamorphosis> usedList;
		ArrayList<Anamorphosis> usedAlreadyValidatedList;

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

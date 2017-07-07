package com.ican.anamorphoses_jsdn.model;

import com.ican.anamorphoses_jsdn.R;

import java.util.ArrayList;
import java.util.Random;
 
public class AnamorphDictionary {

	private ArrayList<Anamorphosis> all = new ArrayList<>();

	private ArrayList<Anamorphosis> easy = new ArrayList<>();
	private ArrayList<Anamorphosis> medium = new ArrayList<>();
	private ArrayList<Anamorphosis> hard = new ArrayList<>();

	private ArrayList<Anamorphosis> alreadyValidated = new ArrayList<>();

	private Random rng = new Random();

	// Initalisation du dictionnaire d'anamorphose
	private void loadAnamorphosis()
    {
		all.add(new Anamorphosis(R.drawable.anamorphosis_1_s, R.drawable.anamorphosis_1_l, Anamorphosis.Difficulty.EASY, "triangle.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_2_s, R.drawable.anamorphosis_2_l, Anamorphosis.Difficulty.HARD, "carre.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_3_s, R.drawable.anamorphosis_3_l, Anamorphosis.Difficulty.EASY, "pentagone.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_4_s, R.drawable.anamorphosis_4_l, Anamorphosis.Difficulty.EASY, "sablier2.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_5_s, R.drawable.anamorphosis_5_l, Anamorphosis.Difficulty.HARD, "tripik.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_6_s, R.drawable.anamorphosis_6_l, Anamorphosis.Difficulty.EASY, "trefle.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_7_s, R.drawable.anamorphosis_7_l, Anamorphosis.Difficulty.MEDIUM, "lajesaispas.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_8_s, R.drawable.anamorphosis_8_l, Anamorphosis.Difficulty.MEDIUM, "rond.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_9_s, R.drawable.anamorphosis_9_l, Anamorphosis.Difficulty.MEDIUM, "3rond.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_10_s, R.drawable.anamorphosis_10_l, Anamorphosis.Difficulty.MEDIUM, "nuage.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_11_s, R.drawable.anamorphosis_11_l, Anamorphosis.Difficulty.HARD, "nuage2.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_12_s, R.drawable.anamorphosis_12_l, Anamorphosis.Difficulty.HARD, "escalier.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_13_s, R.drawable.anamorphosis_13_l, Anamorphosis.Difficulty.MEDIUM, "tripentagone.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_14_s, R.drawable.anamorphosis_14_l, Anamorphosis.Difficulty.HARD, "choufleur.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_15_s, R.drawable.anamorphosis_15_l, Anamorphosis.Difficulty.EASY, "quille.svm"));
		all.add(new Anamorphosis(R.drawable.anamorphosis_16_s, R.drawable.anamorphosis_16_l, Anamorphosis.Difficulty.HARD, "etoile.svm"));
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
		alreadyValidated.add(a);
	}

	public void clearAlreadyValidated() {
		alreadyValidated.clear();
	}

	public Anamorphosis getRandom(Anamorphosis.Difficulty difficulty, boolean notAlreadyValidated) {
		//Le nombre de fois ou l'on a choisi une anamorphose deja validee.
		int nbAttempt = 0;

		//La liste d'anamorphose dans laquelle on faire le tirage au sort.
		//Facile, moyen, difficile ou all.
		ArrayList<Anamorphosis> usedList = null;

		//On prend l'une des trois listes en fonction de la difficulte demandee.
		//Si difficulty est null, on choisit une anamorphose sans se soucier de la difficulte
		//On utilise donc la liste all.
		switch (difficulty) {
			case EASY:
				usedList = easy;
			break;

			case MEDIUM:
				usedList = easy;
			break;

			case HARD:
				usedList = hard;
			break;

			default:
				usedList = all;
		}

		//On tire un nombre au hasard entre 0 et le nombre d'anamorphose dans la liste.
		int index = rng.nextInt(usedList.size());

		//Tant que l'on a pas essayé toute les anamorphose de la liste :
		//C'est a dire quand on aura essaye autant de fois qu'il y a d'anamorphose dans a liste.
		while (nbAttempt < usedList.size()) {

			//On recupere l'anamorphose correspondant au nombre que l'on vient de tirer au sort.
			Anamorphosis a = usedList.get(index);

			//Si cette anamorphose n'a pas deja ete validee,
			//Ou si il n'est pas demande que l'anamorphose n'ai pas deja ete valide
			//On retourne cette anamorphose.
			if (!alreadyValidated.contains(a) || !notAlreadyValidated) {
				return a;
			}

			//Sinon, on essait avec l'anamorphose suivante dans la liste.
			index = (index + 1) % usedList.size();

			//On incremente le nombre de tentatives
			nbAttempt++;
		}

		//Si toutes les anamorphoses de la liste on deja ete validee, on retourne null.
		return null;
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

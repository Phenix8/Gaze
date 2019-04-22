package com.bof.gaze.model;

import com.bof.gaze.R;

import java.util.ArrayList;
import java.util.List;

public class FirstBoardDictionary extends AnamorphDictionary {

    @Override
    protected List<Anamorphosis> loadAnamorphosis() {
        ArrayList<Anamorphosis> all = new ArrayList<>();

        // V1 du plateau
        all.add(new Anamorphosis(R.drawable.anamorphosis_1_s, R.drawable.anamorphosis_1_l, Anamorphosis.Difficulty.EASY, "triangle.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_3_s, R.drawable.anamorphosis_3_l, Anamorphosis.Difficulty.EASY, "pentagone.svm", Anamorphosis.HintType.WHITE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_15_s, R.drawable.anamorphosis_15_l, Anamorphosis.Difficulty.EASY, "quille.svm", Anamorphosis.HintType.GREEN));
        //all.add(new Anamorphosis(R.drawable.anamorphosis_4_s, R.drawable.anamorphosis_4_l, Anamorphosis.Difficulty.EASY, "sablier2.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_6_s, R.drawable.anamorphosis_6_l, Anamorphosis.Difficulty.EASY, "trefle.svm", Anamorphosis.HintType.GREEN));


        all.add(new Anamorphosis(R.drawable.anamorphosis_9_s, R.drawable.anamorphosis_9_l, Anamorphosis.Difficulty.MEDIUM, "3rond.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_13_s, R.drawable.anamorphosis_13_l, Anamorphosis.Difficulty.MEDIUM, "tripentagone.svm", Anamorphosis.HintType.GREEN_BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_18_s, R.drawable.anamorphosis_18_l, Anamorphosis.Difficulty.MEDIUM, "sablier.svm", Anamorphosis.HintType.YELLOW_RED));
        all.add(new Anamorphosis(R.drawable.anamorphosis_19_s, R.drawable.anamorphosis_19_l, Anamorphosis.Difficulty.MEDIUM, "bouteille.svm", Anamorphosis.HintType.WHITE));
        //all.add(new Anamorphosis(R.drawable.anamorphosis_8_s, R.drawable.anamorphosis_8_l, Anamorphosis.Difficulty.MEDIUM, "rond.svm", Anamorphosis.HintType.YELLOW));
        //all.add(new Anamorphosis(R.drawable.anamorphosis_10_s, R.drawable.anamorphosis_10_l, Anamorphosis.Difficulty.MEDIUM, "nuage.svm", Anamorphosis.HintType.GREEN));
        //all.add(new Anamorphosis(R.drawable.anamorphosis_7_s, R.drawable.anamorphosis_7_l, Anamorphosis.Difficulty.MEDIUM, "lajesaispas.svm", Anamorphosis.HintType.RED));

        all.add(new Anamorphosis(R.drawable.anamorphosis_16_s, R.drawable.anamorphosis_16_l, Anamorphosis.Difficulty.HARD, "etoile.svm", Anamorphosis.HintType.GREEN_RED));
        all.add(new Anamorphosis(R.drawable.anamorphosis_2_s, R.drawable.anamorphosis_2_l, Anamorphosis.Difficulty.HARD, "carre.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_12_s, R.drawable.anamorphosis_12_l, Anamorphosis.Difficulty.HARD, "escalier.svm", Anamorphosis.HintType.BLUE));
        //all.add(new Anamorphosis(R.drawable.anamorphosis_17_s, R.drawable.anamorphosis_17_l, Anamorphosis.Difficulty.HARD, "L.svm", Anamorphosis.HintType.WHITE));
        //all.add(new Anamorphosis(R.drawable.anamorphosis_14_s, R.drawable.anamorphosis_14_l, Anamorphosis.Difficulty.HARD, "choufleur.svm", Anamorphosis.HintType.GREEN));
        //all.add(new Anamorphosis(R.drawable.anamorphosis_5_s, R.drawable.anamorphosis_5_l, Anamorphosis.Difficulty.HARD, "tripik.svm", Anamorphosis.HintType.WHITE));

        return all;

    }
}

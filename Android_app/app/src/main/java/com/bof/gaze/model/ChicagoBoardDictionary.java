package com.bof.gaze.model;

import com.bof.gaze.R;

import java.util.ArrayList;
import java.util.List;

public class ChicagoBoardDictionary extends AnamorphDictionary {

    // Initalisation du dictionnaire d'anamorphose
    protected List<Anamorphosis> loadAnamorphosis() {
        ArrayList<Anamorphosis> all = new ArrayList<>();

        // V2 du plateau
        all.add(new Anamorphosis(R.drawable.anamorphosis_1_s, R.drawable.anamorphosis_1_l, Anamorphosis.Difficulty.EASY, "1.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_7_s, R.drawable.anamorphosis_7_l, Anamorphosis.Difficulty.EASY, "3.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_24_s, R.drawable.anamorphosis_24_l, Anamorphosis.Difficulty.EASY, "10.svm", Anamorphosis.HintType.WHITE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_6_s, R.drawable.anamorphosis_6_l, Anamorphosis.Difficulty.EASY, "13.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_26_s, R.drawable.anamorphosis_26_l, Anamorphosis.Difficulty.EASY, "15.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_28_s, R.drawable.anamorphosis_28_l, Anamorphosis.Difficulty.EASY, "17.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_13_s, R.drawable.anamorphosis_13_l, Anamorphosis.Difficulty.EASY, "19.svm", Anamorphosis.HintType.BLUE));

        all.add(new Anamorphosis(R.drawable.anamorphosis_20_s, R.drawable.anamorphosis_20_l, Anamorphosis.Difficulty.MEDIUM, "2.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_15_s, R.drawable.anamorphosis_15_l, Anamorphosis.Difficulty.MEDIUM, "4.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_8_s, R.drawable.anamorphosis_8_l, Anamorphosis.Difficulty.MEDIUM, "5.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_22_s, R.drawable.anamorphosis_22_l, Anamorphosis.Difficulty.MEDIUM, "7.svm", Anamorphosis.HintType.YELLOW_RED));
        all.add(new Anamorphosis(R.drawable.anamorphosis_18_s, R.drawable.anamorphosis_18_l, Anamorphosis.Difficulty.MEDIUM, "9.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_25_s, R.drawable.anamorphosis_25_l, Anamorphosis.Difficulty.MEDIUM, "14.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_27_s, R.drawable.anamorphosis_27_l, Anamorphosis.Difficulty.MEDIUM, "16.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_29_s, R.drawable.anamorphosis_29_l, Anamorphosis.Difficulty.MEDIUM, "20.svm", Anamorphosis.HintType.GREEN));

        all.add(new Anamorphosis(R.drawable.anamorphosis_21_s, R.drawable.anamorphosis_21_l, Anamorphosis.Difficulty.HARD, "6.svm", Anamorphosis.HintType.GREEN));
        all.add(new Anamorphosis(R.drawable.anamorphosis_23_s, R.drawable.anamorphosis_23_l, Anamorphosis.Difficulty.HARD, "8.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_12_s, R.drawable.anamorphosis_12_l, Anamorphosis.Difficulty.HARD, "11.svm", Anamorphosis.HintType.YELLOW));
        all.add(new Anamorphosis(R.drawable.anamorphosis_3_s, R.drawable.anamorphosis_3_l, Anamorphosis.Difficulty.HARD, "12.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_14_s, R.drawable.anamorphosis_14_l, Anamorphosis.Difficulty.HARD, "18.svm", Anamorphosis.HintType.BLUE));
        all.add(new Anamorphosis(R.drawable.anamorphosis_2_s, R.drawable.anamorphosis_2_l, Anamorphosis.Difficulty.HARD, "21.svm", Anamorphosis.HintType.YELLOW_RED));

        return all;
    }
}

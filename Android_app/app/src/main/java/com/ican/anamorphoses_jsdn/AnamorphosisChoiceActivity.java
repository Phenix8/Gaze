package com.ican.anamorphoses_jsdn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Dictionary;
import java.util.List;
import java.util.Random;

public class AnamorphosisChoiceActivity extends AppCompatActivity {

    private ImageButton easyButton = null;
    private ImageButton mediumButton = null;
    private ImageButton hardButton = null;
    private ImageButton okButton = null;
    private ImageView selectorImg = null;
    private AnamorphosisDifficulty difficulty = null;
	
	private Dictionary<AnamorphosisDifficulty, Anamorphosis> anamorphosisByDifficulty;

    private char selectedAnamorphose = 'n';
	private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anamorphose_choice);

        selectorImg = (ImageView) findViewById(R.id.anamorphoseSelector);
		
        /////////////////////////
        // IMAGE BUTTON "Easy" //
        /////////////////////////

        easyButton = (ImageButton) findViewById(R.id.easyImgButton);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectorImg.getVisibility() == View.INVISIBLE)
                    selectorImg.setVisibility(View.VISIBLE);
                selectedAnamorphose = 'e';
                selectorImg.setX(easyButton.getLeft() -10);
                selectorImg.setY(easyButton.getRight() - 10);
                selectorImg.refreshDrawableState();
            }
        });

        ///////////////////////////
        // IMAGE BUTTON "Medium" //
        ///////////////////////////

        mediumButton = (ImageButton) findViewById(R.id.mediumImgButton);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectorImg.getVisibility() == View.INVISIBLE)
                    selectorImg.setVisibility(View.VISIBLE);
                selectedAnamorphose = 'm';
                selectorImg.setX(mediumButton.getLeft() - 10);
                selectorImg.setY(mediumButton.getRight() - 10);
                selectorImg.refreshDrawableState();
            }
        });

        /////////////////////////
        // IMAGE BUTTON "Hard" //
        /////////////////////////

        hardButton = (ImageButton) findViewById(R.id.hardImgButton);
        hardButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (selectorImg.getVisibility() == View.INVISIBLE)
                  selectorImg.setVisibility(View.VISIBLE);
              selectedAnamorphose = 'h';

              selectorImg.setX(hardButton.getLeft() - 10);
              selectorImg.setY(hardButton.getRight() - 10);
              selectorImg.refreshDrawableState();
          }
        });

        ///////////////////////
        // IMAGE BUTTON "Ok" //
        ///////////////////////

        okButton = (ImageButton) findViewById(R.id.okImgButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAnamorphose == 'n')
                    return;
				else if (selectedAnamorphose == 'e')
					difficulty = AnamorphosisDifficulty.EASY;
				else if (selectedAnamorphose == 'm')
					difficulty = AnamorphosisDifficulty.MEDIUM;
				else if (selectedAnamorphose == 'h')
					difficulty = AnamorphosisDifficulty.HARD;

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you choose : " + difficulty + " ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if (difficulty != null)
                        AnamorphGameManager.setTargetAnamorphosis(anamorphosisByDifficulty.get(difficulty));
                        Intent nicknameActivity = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(nicknameActivity);
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
            }
        });
			

		//InitializeAnamorphosisImages(AnamorphosisDifficulty.easy, easyButton);
		//InitializeAnamorphosisImages(AnamorphosisDifficulty.medium, mediumButton);
		//InitializeAnamorphosisImages(AnamorphosisDifficulty.hard, hardButton);
    }

	
    @Override
    public void onBackPressed() {
        return;
    }


	// Fonction d'initialisation de l'affichage et de l'objet
	// "Anamorphose" d'après la difficulté et le buttonImage envoyés
	private void InitializeAnamorphosisImages(AnamorphosisDifficulty difficulty, ImageButton button)
	{
		//Anamorphosis currentAnamorph = GenerateAnamorphosisObj(difficulty);
		//anamorphosisByDifficulty.Add(AnamorphosisDifficulty.easy, GenerateAnamorphosisObj(difficulty));
		
		// ASSIGNATION DE L'IMAGE AU BUTTON EN FONCTION DU PATH
		// button.setImageDrawable();
	}


	/*
	// Fonction de génération d'un object "Anamorphose"
	// en fonction de la difficulté en paramètre
	private Anamorphosis GenerateAnamorphosisObj(AnamorphosisDifficulty difficulty)
	{

		List<Anamorphosis> anamorphSubDict = new List<Anamorphosis>();
		int id = -1;
		
		foreach (Anamorphosis Anam in AnamorphGameManager.getAnamorphosisDict())
		{
			if (Anam.Difficulty.Equals(difficulty)
				anamorphSubDict.add(Anam);
		}
		while (id == -1 || anamorphSubDict.validatedAnamorphosis.contains(anamorphSubDict[id]))
			id = Random.nextInt() % anamorphSubDict.size();
		
		return anamorphSubDict[id];


	}
    */
}

package com.ican.anamorphoses_jsdn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ican.anamorphoses_jsdn.network.Client;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AnamorphosisChoiceActivity extends AppCompatActivity {

    private ImageButton easyButton = null;
    private ImageButton mediumButton = null;
    private ImageButton hardButton = null;
    private ImageButton okButton = null;
    private ImageView selectorImg = null;
    private AnamorphosisDifficulty difficulty = null;
	
	private HashMap<AnamorphosisDifficulty, Anamorphosis> anamorphosisByDifficulty = new HashMap<>();
    private char selectedAnamorphose = 'n';
	private Random randomGenerator = new Random();

    private Client gameClient;

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
            selectedAnamorphose = 'e';
            SetSelectorPosition(easyButton);
            }
        });

        ///////////////////////////
        // IMAGE BUTTON "Medium" //
        ///////////////////////////

        mediumButton = (ImageButton) findViewById(R.id.mediumImgButton);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            selectedAnamorphose = 'm';
            SetSelectorPosition(mediumButton);
            }
        });

        /////////////////////////
        // IMAGE BUTTON "Hard" //
        /////////////////////////

        hardButton = (ImageButton) findViewById(R.id.hardImgButton);
        hardButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              selectedAnamorphose = 'h';
              SetSelectorPosition(hardButton);
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

                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you choose : " + difficulty + " ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id)
                    {
                        */
                        if (difficulty != null && anamorphosisByDifficulty != null)
                            AnamorphGameManager.setTargetAnamorphosis(anamorphosisByDifficulty.get(difficulty));
                        Intent nicknameActivity = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(nicknameActivity);
                        /*
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
                */
            }
        });

		InitializeAnamorphosisImages(AnamorphosisDifficulty.EASY, (ImageView) findViewById(R.id.anamorphosis_img_1) );
		InitializeAnamorphosisImages(AnamorphosisDifficulty.MEDIUM, (ImageView) findViewById(R.id.anamorphosis_img_2));
		InitializeAnamorphosisImages(AnamorphosisDifficulty.HARD, (ImageView) findViewById(R.id.anamorphosis_img_3));

        this.gameClient = AnamorphGameManager.getGameClient();
    }

	
    @Override
    public void onBackPressed() {
        return;
    }


    // Fonction de positionnemnt de l'encadré de sélection
    // en fonction de la position de l'ImageButton en paramètres
    private void SetSelectorPosition(ImageButton button)
    {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.selectorLayout);
        int[] location = new int[2];

        if (selectorImg.getVisibility() == View.INVISIBLE)
            selectorImg.setVisibility(View.VISIBLE);
        button.getLocationInWindow(location);
        frameLayout.setPadding(location[0] - 45, location[1] - 110, 0, 0);
    }


	// Fonction d'initialisation de l'affichage et de l'objet
	// "Anamorphose" d'après la difficulté et le buttonImage envoyés
	private void InitializeAnamorphosisImages(AnamorphosisDifficulty difficulty, ImageView button)
	{
        if (AnamorphGameManager.getAnamorphosisDict() == null)
            AnamorphGameManager.InitAnamorphosisDict(getApplicationContext());

        Anamorphosis currentAnamorphosis = GenerateAnamorphosisObj(difficulty);
		anamorphosisByDifficulty.put(difficulty, currentAnamorphosis);
		
		// ASSIGNATION DE L'IMAGE AU BUTTON EN FONCTION DU PATH
		button.setImageDrawable(ResourcesCompat.getDrawable(getResources(),currentAnamorphosis.getLargeDrawableImage(), null));

	}


	// Fonction de génération d'un object "Anamorphose"
	// en fonction de la difficulté en paramètre
	private Anamorphosis GenerateAnamorphosisObj(AnamorphosisDifficulty difficulty) {
		ArrayList<Anamorphosis> anamorphSubDict = new ArrayList<>();
		int id = -1;
		
		for (Anamorphosis Anam : AnamorphGameManager.getAnamorphosisDict())
		{
			if (Anam.getDifficulty().equals(difficulty))
				anamorphSubDict.add(Anam);
		}
		while (id == -1 || (AnamorphGameManager.getValidatedAnamorphosis() != null &&
                AnamorphGameManager.getValidatedAnamorphosis().contains(anamorphSubDict.get(id))))
			id = randomGenerator.nextInt() % anamorphSubDict.size();
		
		return anamorphSubDict.get(id);


	}

}

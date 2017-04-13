package com.ican.anamorphoses_jsdn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AnamorphoseChoiceActivity extends AppCompatActivity {

    private ImageButton easyButton = null;
    private ImageButton mediumButton = null;
    private ImageButton hardButton = null;
    private ImageView selectorImg = null;

    private char selectedAnamorphose = 'n';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anamorphose_choice);

        selectorImg = (ImageView) findViewById(R.id.anamorphoseSelector);

        ///////////////////////////
        // IMAGE BUTTON "Easy" //
        ///////////////////////////

        easyButton = (ImageButton) findViewById(R.id.returnImgButton);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectorImg.getVisibility() == View.INVISIBLE)
                    selectorImg.setVisibility(View.VISIBLE);
                selectedAnamorphose = 'e';
            }
        });

        ///////////////////////////
        // IMAGE BUTTON "Medium" //
        ///////////////////////////

        easyButton = (ImageButton) findViewById(R.id.returnImgButton);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectorImg.getVisibility() == View.INVISIBLE)
                    selectorImg.setVisibility(View.VISIBLE);
                selectedAnamorphose = 'm';
            }
        });

        ///////////////////////////
        // IMAGE BUTTON "Hard" //
        ///////////////////////////

        easyButton = (ImageButton) findViewById(R.id.returnImgButton);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectorImg.getVisibility() == View.INVISIBLE)
                    selectorImg.setVisibility(View.VISIBLE);
                selectedAnamorphose = 'h';
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}

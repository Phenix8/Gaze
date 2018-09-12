package com.bof.gaze.activity.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.bof.gaze.R;

public class TutorialActivity extends AppCompatActivity {

    private ImageButton tutoImg = null;

    private int tutoSlideNb = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tutorial);


        // Evénement de click sur le slide
        tutoImg = (ImageButton)findViewById(R.id.tuto_img_btn);

        tutoImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                if(tutoSlideNb == 8)
                {
                    boolean isFirstLaunch = getIntent().getBooleanExtra("isFirstLaunch", false);
                    Intent intent;
                    if(isFirstLaunch)
                    {
                        intent = new Intent(getApplicationContext(), NicknameActivity.class);
                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    }
                    else
                        intent = new Intent(getApplicationContext(), MenuActivity.class);

                    startActivity(intent);
                }
                else
                {
                    tutoSlideNb ++;
                    UpdateSlide();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(tutoSlideNb <= 1)
            finish();

        tutoSlideNb --;
        UpdateSlide();
    }


    // Mise à jour de la slide courante à afficher
    private void UpdateSlide()
    {
        if (tutoSlideNb == 1) tutoImg.setImageResource(R.drawable.tuto_slide_1);
        else if (tutoSlideNb == 2) tutoImg.setImageResource(R.drawable.tuto_slide_2);
        else if (tutoSlideNb == 3) tutoImg.setImageResource(R.drawable.tuto_slide_3);
        else if (tutoSlideNb == 4) tutoImg.setImageResource(R.drawable.tuto_slide_4);
        else if (tutoSlideNb == 5) tutoImg.setImageResource(R.drawable.tuto_slide_5);
        else if (tutoSlideNb == 6) tutoImg.setImageResource(R.drawable.tuto_slide_6);
        else if (tutoSlideNb == 7) tutoImg.setImageResource(R.drawable.tuto_slide_7);
        else if (tutoSlideNb == 8) tutoImg.setImageResource(R.drawable.tuto_slide_8);

        tutoImg.refreshDrawableState();
    }
}

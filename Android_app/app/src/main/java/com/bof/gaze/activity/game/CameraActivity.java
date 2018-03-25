package com.bof.gaze.activity.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bof.gaze.R;
import com.bof.gaze.activity.common.CommonGameActivity;
import com.bof.gaze.camera.CameraProcessor;
import com.bof.gaze.detection.ObjectDetector;
import com.bof.gaze.model.Anamorphosis;
import com.bof.gaze.model.Player;
import com.bof.gaze.network.client.Client;
import com.bof.gaze.view.AutoFitTextureView;
import com.bof.gaze.view.CameraGlassSurfaceView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends CommonGameActivity
        implements View.OnClickListener, CameraProcessor.CameraProcessorListener, View.OnTouchListener, Client.GameEventListener {

    private ImageView cancelImg, littleAnamorphImg, littleAnamorphBg, largeAnamorphImg, largeAnamophBg, cameraImg;
    private CameraGlassSurfaceView cameraGlassSurfaceView;
    private AutoFitTextureView textureView;

    private Anamorphosis targetAnamorphosis;

    private boolean canCancel = true;

    private CameraProcessor cameraProcessor = new CameraProcessor(this);

    // 5 seconds before a hint is available
    private long hintActivationDelay = 5000;
    private Calendar startActivityDate;

    ArrayList<Player> playerListTest = new ArrayList<Player>();

    // Adapter to fill (Image + text) the player scores list
    private ArrayAdapter<Player> adapter;

    private class CustomAdapter extends ArrayAdapter<Player> {
        CustomAdapter(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            Player player = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.camera_player_score_item, parent, false);

            if (player == null)
                return convertView;

            TextView txtPlayerScore = convertView.findViewById(R.id.camera_J_score_txt);
            ImageView imgPlayerScore = convertView.findViewById(R.id.camera_J_score_img);

            txtPlayerScore.setText(String.valueOf(player.getScore()));

            imgPlayerScore.setImageResource(
                player.getPlayerId().equals(getGameClient().getPlayerId()) ?
                        R.drawable.camera_player_score : R.drawable.camera_other_player_score
            );

            updateFoundAnamorphosisImg(convertView, player);

            return convertView;
        }
    }

    private void updateFoundAnamorphosisImg(View convertView, Player player)
    {
        convertView.findViewById(R.id.camera_player_anam1_img).setVisibility( (player.getNbFoundAnamorphosis() > 0) ? View.VISIBLE : View.INVISIBLE);
        convertView.findViewById(R.id.camera_player_anam2_img).setVisibility( (player.getNbFoundAnamorphosis() > 1) ? View.VISIBLE : View.INVISIBLE);
        convertView.findViewById(R.id.camera_player_anam3_img).setVisibility( (player.getNbFoundAnamorphosis() > 2) ? View.VISIBLE : View.INVISIBLE);
        convertView.findViewById(R.id.camera_player_anam4_img).setVisibility( (player.getNbFoundAnamorphosis() > 3) ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadComponents() {
        cancelImg = (ImageView) findViewById(R.id.camera_act_cancel_img);
        cancelImg.setOnClickListener(this);

        littleAnamorphImg = (ImageView) findViewById(R.id.camera_act_little_anamorph_img);
        littleAnamorphImg.setOnClickListener(this);

        littleAnamorphBg = (ImageView) findViewById(R.id.camera_act_little_anamorph_bg);

        largeAnamorphImg = (ImageView) findViewById(R.id.camera_act_large_anamorph_img);
        largeAnamorphImg.setOnClickListener(this);

        largeAnamophBg = (ImageView) findViewById(R.id.camera_act_large_anamorph_bg);

        cameraImg = (ImageView) findViewById(R.id.camera_act_camera_img);
        cameraImg.setOnClickListener(this);

        cameraGlassSurfaceView = (CameraGlassSurfaceView) findViewById(R.id.camera_act_grid_img);
        cameraGlassSurfaceView.setOnTouchListener(this);

        textureView = (AutoFitTextureView) findViewById(R.id.camera_act_surface);
    }

    private void loadTargetAnamorphosis() {
        targetAnamorphosis = (Anamorphosis) getIntent().getSerializableExtra("anamorphosis");

        if (targetAnamorphosis == null) {
            showToast("Error : no target anamorphosis defined");
            return;
        }

        littleAnamorphImg.setImageResource(targetAnamorphosis.getDrawableImage());
        largeAnamorphImg.setImageResource(targetAnamorphosis.getLargeDrawableImage());
    }

    private void cancelAnamorphosis(){
        if (!canCancel) {
            showToast("Please wait a few more seconds");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you  really want to try another anamorphosis ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void searchTargetAnamorphosis() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.obturateur);
        mp.start();
        cameraProcessor.captureImage();
    }

    private void toggleLargeAnamorphosisImg() {
        View v = (View) largeAnamorphImg.getParent();
        v.setVisibility(v.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
    }

    private void checkForAlreadyCanceledState() {
        if (!getIntent().getBooleanExtra("alreadyCanceled", false) ||
                getIntent().getBooleanExtra("debug", false)) {
            return;
        }

        canCancel = false;
        cancelImg.setImageResource(R.drawable.camera_cancel_disabled);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        canCancel = true;
                        cancelImg.setImageResource(R.drawable.camera_cancel);
                    }
                });
            }
        }, 30000);
    }

    // Check if the delay to display a hint is passed, and display it if it is the case
    private void checkHintAvailability()
    {
        if (startActivityDate.getTimeInMillis() + hintActivationDelay > Calendar.getInstance().getTimeInMillis())
            return;

        // DEBUG

        if(targetAnamorphosis.getHint() == Anamorphosis.HintType.BLUE ||targetAnamorphosis.getHint() == Anamorphosis.HintType.GREEN_BLUE)
        {
            littleAnamorphBg.setImageResource(R.drawable.camera_little_anamorph_b_bg);
            largeAnamophBg.setImageResource(R.drawable.camera_large_anamorph_b_bg);
        }
        else if(targetAnamorphosis.getHint() == Anamorphosis.HintType.RED || targetAnamorphosis.getHint() == Anamorphosis.HintType.GREEN_RED)
        {
            littleAnamorphBg.setImageResource(R.drawable.camera_little_anamorph_r_bg);
            largeAnamophBg.setImageResource(R.drawable.camera_large_anamorph_r_bg);
        }
        else if(targetAnamorphosis.getHint() == Anamorphosis.HintType.GREEN)
        {
            littleAnamorphBg.setImageResource(R.drawable.camera_little_anamorph_g_bg);
            largeAnamophBg.setImageResource(R.drawable.camera_large_anamorph_g_bg);
        }
        else if(targetAnamorphosis.getHint() == Anamorphosis.HintType.YELLOW || targetAnamorphosis.getHint() == Anamorphosis.HintType.YELLOW_RED)
        {
            littleAnamorphBg.setImageResource(R.drawable.camera_little_anamorph_y_bg);
            largeAnamophBg.setImageResource(R.drawable.camera_large_anamorph_y_bg);
        }
        littleAnamorphImg.setImageResource(targetAnamorphosis.getDrawableImage());
    }

    // Play the sound of a found anamorphosis depending on the number of already found anamorphosis (0 to 3)
    private void playFoundSound() {
        int imageId, nbAnamorphosis = getGameClient().getCurrentPlayer().getNbFoundAnamorphosis();

        if (nbAnamorphosis == 3)            imageId = R.raw.anam_valid4;
        else if (nbAnamorphosis == 2)       imageId = R.raw.anam_valid3;
        else if (nbAnamorphosis == 1)       imageId = R.raw.anam_valid2;
        else                                imageId = R.raw.anam_valid1;

        MediaPlayer mp = MediaPlayer.create(this, imageId);
        mp.start();
    }

    @Override
    public void onError(String error) {
        showError(error);
    }

    @Override
    public void onImageAvailable(Image img) {
        Log.d("CameraActivity", "Got an image, analysing...");
        int result =
                ObjectDetector.getInstance()
                        .checkForObjects(
                                img,
                                targetAnamorphosis.getDetectorName(),
                                4
                        );
        if (result == -1) {
            showToast("An error occured");
        } else if (result > 0) {
            cameraGlassSurfaceView.displayFeedbackFound();
            playFoundSound();
            setResult(RESULT_OK);
            finish();
        } else {
            cameraGlassSurfaceView.displayFeedbackNotFound();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (view == cameraGlassSurfaceView) {

                boolean focusIsSupported = cameraProcessor.focusOnPoint(
                        motionEvent.getX() / (float) view.getWidth(),
                        motionEvent.getY() / (float) view.getHeight()
                );

                if (focusIsSupported) {
                    cameraGlassSurfaceView.displayFeedbackFocus(
                            (int) motionEvent.getX(),
                            (int) motionEvent.getY()
                    );
                }
            }
        }

        checkHintAvailability();

        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == cancelImg) {
            cancelAnamorphosis();
        } else if (view == littleAnamorphImg){
            toggleLargeAnamorphosisImg();
        } else if (view == largeAnamorphImg) {
            toggleLargeAnamorphosisImg();
        } else if (view == cameraImg) {
            searchTargetAnamorphosis();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.camera_activity_layout);

        loadComponents();
        loadTargetAnamorphosis();
        checkForAlreadyCanceledState();

        // Player scores list
        ListView playerList = (ListView) findViewById(R.id.playerScoresListview);
        adapter = new CameraActivity.CustomAdapter(this, R.layout.camera_player_score_item);
        setPlayerAdapter(adapter);
        playerList.setAdapter(adapter);

        this.startActivityDate = Calendar.getInstance();
    }

    @Override
    protected void onResume() {
        Log.d("CameraActivity", "onResume() start");
        super.onResume();
        cameraProcessor.start(this, textureView);
        Log.d("CameraActivity", "onResume() end");
    }

    @Override
    protected void onPause() {
        Log.d("CameraActivity", "onPause() start");
        super.onPause();
        cameraProcessor.stop();
        Log.d("CameraActivity", "onPause() end");
    }
}

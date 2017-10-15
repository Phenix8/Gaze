package com.ican.gaze.activity.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ican.gaze.R;
import com.ican.gaze.activity.common.CommonGameActivity;
import com.ican.gaze.model.Player;
import com.ican.gaze.network.Client;
import com.ican.gaze.view.AutoFitTextureView;
import com.ican.gaze.camera.CameraProcessor;
import com.ican.gaze.model.Anamorphosis;
import com.ican.gaze.view.CameraGlassSurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dlibwrapper.DLibWrapper;

public class CameraActivity extends CommonGameActivity
        implements View.OnClickListener, CameraProcessor.CameraProcessorListener, View.OnTouchListener, Client.GameEventListener {

    private ImageView cancelImg, littleAnamorphImg, largeAnamorphImg, cameraImg;
    private CameraGlassSurfaceView cameraGlassSurfaceView;
    private AutoFitTextureView textureView;

    private Anamorphosis targetAnamorphosis;

    private boolean canCancel = true;

    private CameraProcessor cameraProcessor = new CameraProcessor(this);

    // Adapter to fill (Image + text) the player scores list
    private ArrayAdapter<Player> adapter;

    private class CustomAdapter extends ArrayAdapter<Player> {
        CustomAdapter(Context context, int resource, ArrayList<Player> players) {
            super(context, resource, players);
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

            // The current player case
            //if (player.getPlayerId().equals(getGameClient().getPlayerId()))
            if (player.getPlayerId().equals("99"))
            {
                updateFoundAnamorphosisImg(convertView, player);
                imgPlayerScore.setImageResource(R.drawable.camera_player_score);
            }
            else
                imgPlayerScore.setImageResource(R.drawable.camera_other_player_score);

            return convertView;
        }
    }

    private void updateFoundAnamorphosisImg(View convertView, Player player)
    {
        convertView.findViewById(R.id.camera_player_anam1_img).setVisibility( (getGameClient().getNbFoundAnamorphosis() > 0) ? View.VISIBLE : View.INVISIBLE);
        convertView.findViewById(R.id.camera_player_anam2_img).setVisibility( (getGameClient().getNbFoundAnamorphosis() > 1) ? View.VISIBLE : View.INVISIBLE);
        convertView.findViewById(R.id.camera_player_anam3_img).setVisibility( (getGameClient().getNbFoundAnamorphosis() > 2) ? View.VISIBLE : View.INVISIBLE);
        convertView.findViewById(R.id.camera_player_anam4_img).setVisibility( (getGameClient().getNbFoundAnamorphosis() > 3) ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadComponents() {
        cancelImg = (ImageView) findViewById(R.id.camera_act_cancel_img);
        cancelImg.setOnClickListener(this);

        littleAnamorphImg = (ImageView) findViewById(R.id.camera_act_little_anamorph_img);
        littleAnamorphImg.setOnClickListener(this);

        largeAnamorphImg = (ImageView) findViewById(R.id.camera_act_large_anamorph_img);
        largeAnamorphImg.setOnClickListener(this);

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

    @Override
    public void onError(String error) {
        showError(error);
    }

    @Override
    public void onImageAvailable(Image img) {
        Log.d("CameraActivity", "Got an image, analysing...");
        int result =
                DLibWrapper.getInstance()
                        .checkForObjects(
                                img,
                                targetAnamorphosis.getDetectorName(),
                                4
                        );

        if (result == -1) {
            showToast("An error occured");
        } else if (result > 0) {
            cameraGlassSurfaceView.displayFeedbackFound();
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
                if (cameraProcessor.focusOnPoint(
                        motionEvent.getX() / (float) view.getWidth(),
                        motionEvent.getY() / (float) view.getHeight()
                )) {
                    cameraGlassSurfaceView.displayFeedbackFocus(
                            (int) motionEvent.getX(),
                            (int) motionEvent.getY()
                    );
                }
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {

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
        setContentView(R.layout.camera_activity_layout);

        loadComponents();
        loadTargetAnamorphosis();
        checkForAlreadyCanceledState();

        // TEST
        ArrayList<Player> playerListTest = new ArrayList<Player>();
        playerListTest.add(new Player("Test 1", 2, true, "99"));
        playerListTest.add(new Player("Test 2", 0, true, "100"));
        playerListTest.add(new Player("Test 3", 12, true, "101"));
        playerListTest.add(new Player("Ron", 4, true, "102"));

        //
        ListView playerList = (ListView) findViewById(R.id.playerScoresListview);
        adapter = new CameraActivity.CustomAdapter(this, R.layout.camera_player_score_item, playerListTest);
        adapter.addAll(getGameClient().getPlayerList());
        adapter.notifyDataSetChanged();
        playerList.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraProcessor.start(this, textureView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraProcessor.stop();
    }
}

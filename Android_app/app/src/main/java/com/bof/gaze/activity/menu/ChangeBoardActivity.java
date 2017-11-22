package com.bof.gaze.activity.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bof.gaze.R;
import com.bof.gaze.detection.ObjectDetector;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by PCMAC on 22/11/2017.
 */

public class ChangeBoardActivity extends Activity implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_board_activity_layout);

        String[] boardNames;

        try {
            boardNames = getAssets().list("detectors");
        } catch (IOException e) {
            e.printStackTrace();
            finish();
            return;
        }

        adapter = new ArrayAdapter<>(this, R.layout.change_board_activity_list_item, boardNames);

        final ListView list = (ListView) findViewById(R.id.change_board_activity_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ObjectDetector.getInstance().loadDetectors(
            getAssets(),
            String.format(
                Locale.ENGLISH,
                "detectors/%s",
                adapter.getItem(position)
            )
        );
        finish();
    }
}

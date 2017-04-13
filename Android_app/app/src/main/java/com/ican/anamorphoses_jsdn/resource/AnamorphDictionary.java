package com.ican.anamorphoses_jsdn.resource;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by root on 12/04/2017.
 */

public class AnamorphDictionary {

    private HashMap<Integer, Anamorph> items = new HashMap<>();

    public static Bitmap loadImageFromAsset(String fileName, Activity act)
        throws IOException {
        InputStream is = act.getAssets().open(fileName);
        return BitmapFactory.decodeStream(is);
    }

    public static AnamorphDictionary FromAssetFile(String fileName, Activity act)
        throws IOException, JSONException {
        InputStream is = act.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        JSONObject json = new JSONObject(new String(buffer, "UTF-8"));
        JSONArray anamorphs = json.getJSONArray("anamorphoses");

        AnamorphDictionary result = new AnamorphDictionary();

        for (int i=0; i<anamorphs.length(); i++) {
            JSONObject anamorph = anamorphs.getJSONObject(i);
            result.add(
                    new Anamorph(
                            i,
                            anamorph.getString("detector"),
                            loadImageFromAsset(anamorph.getString("image"), act))
            );
        }

        return result;
    }

    private AnamorphDictionary() {

    }

    public Anamorph getById(int id) {
        return items.get(id);
    }

    private void add(Anamorph a) {
        items.put(a.getId(), a);
    }
}

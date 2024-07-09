package com.example.first_assignment;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecordManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public RecordManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("game_records", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void saveRecords(List<Record> records) {
        Gson gson = new Gson();
        String json = gson.toJson(records);
        editor.putString("records", json);
        editor.apply();
    }

    public ArrayList<Record> loadRecords() {
        String json = sharedPreferences.getString("records", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Record>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }
}


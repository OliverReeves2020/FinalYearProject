package com.example.finalyearproject.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prompt_evaluator.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_PROMPTS =
            "CREATE TABLE prompts (" +
                    "prompt TEXT PRIMARY KEY," +
                    "positive_count INTEGER DEFAULT 0," +
                    "negative_count INTEGER DEFAULT 0," +
                    "highest_streak INTEGER DEFAULT 0," +
                    "presented_count INTEGER DEFAULT 0" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROMPTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to do here for now
    }
    public void setPromptHighestStreak(String prompt, int highestStreak) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("highest_streak", highestStreak);
        db.update("prompts", values, "prompt" + "=?", new String[]{prompt});
        db.close();
    }
    @SuppressLint("Range")
    public int getPromptHighestStreak(String prompt) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns to query
        String[] columns = {"highest_streak"};

        // Define the selection criteria
        String selection = "prompt = ?";

        // Define the selection arguments
        String[] selectionArgs = {prompt};

        // Query the database
        Cursor cursor = db.query("prompts", columns, selection, selectionArgs, null, null, null);

        int highestStreak = 0;

        // If the cursor is not null and has at least one row, retrieve the highest streak
        if (cursor != null && cursor.moveToFirst()) {
            highestStreak = cursor.getInt(cursor.getColumnIndex("highest_streak"));
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return highestStreak;
    }


}



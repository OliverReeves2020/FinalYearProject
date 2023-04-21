/**
 * The PromptEvaluator class is used to evaluate user prompts and calculate their NPS score based on
 * positive and negative responses.
 */
package com.example.finalyearproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class PromptEvaluator {

    private SQLiteDatabase db;

    public PromptEvaluator(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void evaluatePrompt(String prompt, boolean isPositive) {
        Cursor cursor = db.rawQuery("SELECT * FROM prompts WHERE prompt=?", new String[]{prompt});

        if (cursor.moveToFirst()) {
            int positiveCountIndex = cursor.getColumnIndex("positive_count");
            int negativeCountIndex = cursor.getColumnIndex("negative_count");
            int presentedCountIndex = cursor.getColumnIndex("presented_count");

            int positiveCount = cursor.getInt(positiveCountIndex >= 0 ? positiveCountIndex : 0);
            int negativeCount = cursor.getInt(negativeCountIndex >= 0 ? negativeCountIndex : 0);
            int presentedCount = cursor.getInt(presentedCountIndex >= 0 ? presentedCountIndex : 0);

            if (isPositive) {
                positiveCount++;
                negativeCount = 0;
            } else {
                negativeCount++;
            }

            presentedCount++;

            ContentValues values = new ContentValues();
            values.put("prompt", prompt);
            values.put("positive_count", positiveCount);
            values.put("negative_count", negativeCount);
            values.put("presented_count", presentedCount);

            int rowsAffected = db.update("prompts", values, "prompt=?", new String[]{prompt});

            if (rowsAffected == 0) {
                db.insert("prompts", null, values);
            }
        } else {
            int positiveCount = 0;
            int negativeCount = 0;
            int presentedCount = 1;

            if (isPositive) {
                positiveCount = 1;
            } else {
                negativeCount = 1;
            }

            ContentValues values = new ContentValues();
            values.put("prompt", prompt);
            values.put("positive_count", positiveCount);
            values.put("negative_count", negativeCount);
            values.put("presented_count", presentedCount);

            db.insert("prompts", null, values);
        }

        cursor.close();
    }

    public Map<String, Integer> evaluateAllPrompts(Context context) {
        Map<String, Integer> promptScores = new HashMap<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT prompt, positive_count, negative_count, highest_streak FROM prompts", null);
        if (cursor.moveToFirst()) {
            do {
                String prompt = cursor.getString(cursor.getColumnIndexOrThrow("prompt"));
                int positiveCount = cursor.getInt(cursor.getColumnIndexOrThrow("positive_count"));
                int negativeCount = cursor.getInt(cursor.getColumnIndexOrThrow("negative_count"));
                int highestStreak = cursor.getInt(cursor.getColumnIndexOrThrow("highest_streak"));
                int score = calculateNpsScore(positiveCount, negativeCount,highestStreak);
                promptScores.put(prompt, score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return promptScores;
    }
    public static int calculateNpsScore(int positiveCount, int negativeCount, int highestStreak) {
        int totalResponses = positiveCount + negativeCount;
        double positivePercentage = (double) positiveCount / totalResponses;
        double negativePercentage = (double) negativeCount / totalResponses;
        int npsScore = (int) Math.round(100 * (positivePercentage - negativePercentage));
        npsScore += 10 * (highestStreak - 1); // Add 10 points for each consecutive positive response
        return npsScore;
    }




    public void close() {
        db.close();
    }
}



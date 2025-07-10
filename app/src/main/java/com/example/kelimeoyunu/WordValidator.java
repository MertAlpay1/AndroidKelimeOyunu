package com.example.kelimeoyunu;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class WordValidator {
    private static Set<String> validWords = new HashSet<>();

    public static void loadWords(Context context) {
        try {
            InputStream is = context.getAssets().open("words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Log.d("Kelime", "başlatırldı: " );

            String line;
            while ((line = reader.readLine()) != null) {
                validWords.add(line.trim().toLowerCase());
            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Kelime", "başlatılamadı: " );
        }
    }

    public static boolean isValidWord(String word) {
        String normalized = Normalizer.normalize(word.toLowerCase(Locale.ROOT), Normalizer.Form.NFC);
        Log.d("Kelime", "kelime :" +normalized );
        //word.toLowerCase()
        return validWords.contains(normalized);

    }
}
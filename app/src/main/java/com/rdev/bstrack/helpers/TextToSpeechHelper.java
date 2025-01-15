package com.rdev.bstrack.helpers;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechHelper {
    private TextToSpeech tts;

    public void initializeTTS(Context context) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set language to English or any other language
                int result = tts.setLanguage(Locale.ENGLISH);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported!");
                } else {
                    Log.i("TTS", "Initialization successful!");
                }
            } else {
                Log.e("TTS", "Initialization failed!");
            }
        });
    }

    public void speak(String text,Boolean flag) {
        if (tts != null && flag) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void stopTTS() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}

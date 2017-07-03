package jhm.ufam.br.epulum.Classes;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Mateus on 18-Jun-17.
 */

public class SpeechHandler {

    public TextToSpeech tts;

    public SpeechHandler(Context con) {
        tts= new TextToSpeech(con, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(new Locale("pt","BR"));
                tts.setSpeechRate(0.6f);
            }
        });

    }

    public void Speak(String spk){
        tts.speak(spk, TextToSpeech.QUEUE_FLUSH, null);
    }
}

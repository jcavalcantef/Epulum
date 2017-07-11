package jhm.ufam.br.epulum.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import java.util.Locale;

/**
 * Created by Mateus on 08/07/2017.
 */

public class SpeechWrapper {

    private static TextToSpeech mTts = null;
    private static boolean isSpeaking = false;
    private static BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED) && mTts != null)
            {
                isSpeaking = false;
                Log.v("IsSpeaking","stopped speaking "+isSpeaking());
            }
        }
    };

    public SpeechWrapper(Context con) {
        Init(con);

    }

    public static void Speak(String sMessage) {
        if (mTts == null || sMessage == null) return;
        sMessage = sMessage.trim();
        isSpeaking = true;

        mTts.speak(sMessage, TextToSpeech.QUEUE_ADD, null);
    }

    public static void Init(Context context) {
        mTts = new TextToSpeech(context, new OnInitListener() {
            @Override
            public void onInit(int status) {
                mTts.setLanguage(new Locale("pt","br"));
                mTts.setSpeechRate(0.6f);
            }
        });
        IntentFilter filter = new IntentFilter(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        context.registerReceiver(receiver, filter);
    }

    public static void Shutdown() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    public static boolean isSpeaking() {
        return isSpeaking;
    }

    public void Stop(){
        mTts.stop();
    }
}

package jhm.ufam.br.epulum.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Mateus on 08/07/2017.
 */

public class SpeechWrapper implements TextToSpeech.OnUtteranceCompletedListener {

    private TextToSpeech mTts = null;
    private boolean isSpeaking = false;
    private BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED) && mTts != null)
            {

                Log.v("IsSpeaking","stopped speaking "+isSpeaking());
            }
        }
    };

    public SpeechWrapper(Context con) {
        Init(con);

    }

    public void Speak(String sMessage) {
        if (mTts == null || sMessage == null) return;
        sMessage = sMessage.trim();
        isSpeaking = true;
        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        //myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
        mTts.speak(sMessage, TextToSpeech.QUEUE_ADD, myHashAlarm);
    }

    public void Init(Context context) {
        mTts = new TextToSpeech(context, new OnInitListener() {
            @Override
            public void onInit(int status) {
                mTts.setLanguage(new Locale("pt","br"));
                mTts.setSpeechRate(0.6f);

            }
        });
        mTts.setOnUtteranceCompletedListener(this);
        IntentFilter filter = new IntentFilter(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        context.registerReceiver(receiver, filter);
    }

    public void Shutdown() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void Stop(){
        mTts.stop();
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        isSpeaking = false;
        Log.v("Fazer","utterance completed");
    }
}

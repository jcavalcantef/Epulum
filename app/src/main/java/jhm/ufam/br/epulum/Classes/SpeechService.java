package jhm.ufam.br.epulum.Classes;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.RecognitionService;
import android.support.annotation.Nullable;
import android.util.Log;

import jhm.ufam.br.epulum.R;

/**
 * Created by Mateus on 11-Jul-17.
 */

public class SpeechService extends RecognitionService {

    @Override
    protected void onStartListening(Intent recognizerIntent, Callback listener) {

    }

    @Override
    protected void onCancel(Callback listener) {

    }

    @Override
    protected void onStopListening(Callback listener) {

    }
}
package jhm.ufam.br.epulum.Classes;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jhm.ufam.br.epulum.Activities.ActivityReadingReceita;
import jhm.ufam.br.epulum.R;

/**
 * Created by Mateus on 11/07/2017.
 */

public class ThreadFazerReceita implements Runnable {

    private enum estados{
        INICIO,
        INGREDIENTES,
        PASSOS
    }
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Receita receita;
    private Context context;
    private String result;
    public boolean newResult;
    private estados eAgora;
    private SpeechWrapper sw;
    private ActivityReadingReceita arr;



    public ThreadFazerReceita(Receita receita, Context context, SpeechWrapper swr, ActivityReadingReceita arrr) {
        this.receita = receita;
        this.context = context;
        this.sw = swr;
        this.arr=arrr;
        eAgora=estados.INICIO;

    }

    @Override
    public void run() {
        sw.Speak("Vamos começar.");
        while(sw.isSpeaking());
        sw.Speak("Você quer separar os ingredientes?");
        while(sw.isSpeaking());
        Log.v("Fazer","parou de falar");
        while(true){
            switch (eAgora){
                case INICIO:
                    //while(sw.isSpeaking());
                    arr.promptSpeechInput();
                    while(!newResult);
                    Log.v("result", "recebeu resultado");
                    try {
                        if (result.matches("sim")) {
                            eAgora = estados.INGREDIENTES;
                            Log.v("result","passou para ingredientes");
                        }
                        else if(result.matches("não"));
                        else if (result == null) sw.Speak("Desculpe não entendi.");
                        Log.v("result", result);
                    }catch (IndexOutOfBoundsException e){

                    }


            }

        }
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

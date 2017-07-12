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

    private enum estados {
        INICIO,
        I_P,
        P_,
        INGREDIENTES,
        PASSOS
    }

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final String SIM = "sim";
    private final String NAO = "não";
    private final String PARA = "para";
    private final String VOLTA = "volta";
    private final String REPETE = "repete";
    private final String ESPERA = "espera";

    private Receita receita;
    private Context context;
    private String result;
    public boolean newResult;
    private estados eAgora;
    private SpeechWrapper sw;
    private ActivityReadingReceita arr;
    private int ingr;
    private int pass;


    public ThreadFazerReceita(Receita receita, Context context, SpeechWrapper swr, ActivityReadingReceita arrr) {
        this.receita = receita;
        this.context = context;
        this.sw = swr;
        this.arr = arrr;
        eAgora = estados.INICIO;
        ingr = 0;
        pass = 0;

    }

    @Override
    public void run() {
        //sw.Speak("Vamos começar.");
        //waitSpeaking();

        while (true) {
            Log.v("result","while loop");
            switch (eAgora) {
                case INICIO:

                    //sw.Speak("Você quer separar os ingredientes?");
                    //waitSpeaking();
                    Log.v("Fazer", "parou de falar");
                    getSpeech();
                    eAgora = estados.I_P;
                    Log.v("result", "recebeu resultado");

                    break;
                case I_P:
                    while (!newResult) ;
                    try {
                        if (hasWord(SIM)) {
                            eAgora = estados.INGREDIENTES;
                            Log.v("result", "passou para ingredientes");
                        } else if (hasWord(NAO)) {
                            eAgora = estados.P_;
                            Log.v("result", "passou para passos");
                        } else sw.Speak("Desculpe não entendi.");
                        Log.v("result", result);
                    } catch (IndexOutOfBoundsException e) {

                    }
                    break;
                case P_:
                    sw.Speak("Então, você quer os passos?");
                    getSpeech();
                    while (!newResult) ;
                    try {
                        if (hasWord(SIM)) {
                            eAgora = estados.PASSOS;
                            Log.v("result", "passou para passos");
                        } else if (hasWord(NAO)) {
                            eAgora = estados.I_P;
                            Log.v("result", "passou para I_P");
                        } else sw.Speak("Desculpe não entendi.");
                        Log.v("result", result);
                    } catch (IndexOutOfBoundsException e) {

                    }
                    break;
                case INGREDIENTES:
                    Log.v("result","ingredientes");
                    if (ingr < receita.getIngredientes().size()) {
                        sw.Speak("Separe " + receita.getIngredientes().get(ingr));
                        waitSpeaking();
                        sleep(2000);
                        sw.Speak("Próximo ingrediente?");
                        waitSpeaking();
                        getSpeech();
                        doneSpeaking();
                        if (hasWord(SIM)) {
                            ingr++;
                            Log.v("result", "proximo ingrediente");
                        } else if (hasWord(ESPERA)) {
                            sleep(3000);
                            Log.v("result", "esta esperando");
                        }


                    } else {
                        sw.Speak("Agora passamos para os passos.");
                        waitSpeaking();
                    }
                    break;

                default:



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

    public boolean hasWord(String word) {
        return result.contains(word);
    }

    public void waitSpeaking() {
        while (sw.isSpeaking()) ;
    }

    public void getSpeech() {
        arr.promptSpeechInput();
    }

    public void doneSpeaking() {
        while (!newResult) ;
    }

    public void sleep(int amount) {
        try {
            Thread.sleep(amount);
        } catch (Exception e) {
        }
    }
}

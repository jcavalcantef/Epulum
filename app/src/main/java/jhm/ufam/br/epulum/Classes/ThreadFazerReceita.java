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
        PASSOS,
        PAROU
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
    public boolean para;
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
        para=false;

    }

    @Override
    public void run() {
        while (!para) {
            Log.v("result","while loop");
            switch (eAgora) {
                case INICIO:

                    Speak("Você quer separar os ingredientes?");

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
                        } else {

                        }
                        Log.v("result", result);
                    } catch (IndexOutOfBoundsException e) {

                    }
                    break;
                case P_:
                    Speak("você quer fazer os passos?");
                    getSpeech();
                    while (!newResult) ;
                    try {
                        if (hasWord(SIM)) {
                            eAgora = estados.PASSOS;
                            Log.v("result", "passou para passos");
                        } else if (hasWord(NAO)) {
                            eAgora = estados.I_P;
                            Log.v("result", "passou para I_P");
                        } else {

                        }
                        Log.v("result", result);
                    } catch (IndexOutOfBoundsException e) {

                    }
                    break;
                case INGREDIENTES:
                    Log.v("result","ingredientes");
                    if (ingr < receita.getIngredientes().size()) {
                        Speak("Separe " + receita.getIngredientes().get(ingr));

                        sleep(2000);
                        Speak("Próximo ingrediente?");

                        getSpeech();

                        if (hasWord(SIM)) {
                            ingr++;
                            Log.v("result", "proximo ingrediente");
                        } else if (hasWord(ESPERA)) {
                            sleep(3000);
                            Log.v("result", "esta esperando");
                        }


                    } else {
                        Speak("Agora vamos para os passos.");

                        eAgora=estados.PASSOS;
                    }
                    Log.v("result", "passei por ingrediente");
                    break;

                case PASSOS:
                    Log.v("result","passos");
                    if(pass< receita.getPassos().size()){
                        Speak(receita.getPassos().get(pass));

                        sleep(1000);
                        Speak("Próximo passo?");

                        getSpeech();

                        if (hasWord(SIM)) {
                            pass++;
                            Log.v("result", "proximo passo");
                        } else if (hasWord(ESPERA)) {
                            sleep(3000);
                            Log.v("result", "esta esperando");
                        }
                    }
                    else {
                        Speak("Este é o fim da receita.");
                        eAgora=estados.PAROU;
                    }
                    break;
                case PAROU:
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

    public void Speak(String pal) {
        if(!para) {
            sw.Speak(pal);
            while (sw.isSpeaking() && !para) ;
        }
    }

    public void getSpeech() {
        if(!para) {
            arr.promptSpeechInput();
            while (!newResult && !para) Log.v("result", "not done speaking");
        }
    }

    public void sleep(int amount) {
        try {
            Thread.sleep(amount);
        } catch (Exception e) {
        }
    }
}

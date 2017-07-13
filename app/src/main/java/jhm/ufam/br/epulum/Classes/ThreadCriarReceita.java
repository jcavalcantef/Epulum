package jhm.ufam.br.epulum.Classes;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jhm.ufam.br.epulum.Activities.ActivityCriarReceita;
import jhm.ufam.br.epulum.Activities.ActivityReadingReceita;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVIngredienteAdapter;
import jhm.ufam.br.epulum.RVAdapter.RVPassosAdapter;

/**
 * Created by Mateus on 11/07/2017.
 */

public class ThreadCriarReceita implements Runnable {

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
    private final String ADICIONAR = "adicionar";
            private final String RECEITA ="receita";
    private final String INGREDIENTE = "ingrediente";
    private final String PASSO = "passo";
    private final String prompt_pi= "Diga Ingrediente ou Passo";
    private final String prompt_ingrediente = "Fale o ingrediente";
    private final String prompt_passo = "Fale o passo";

    private Receita receita;
    private Context context;
    private String result;
    public boolean newResult;
    public boolean para;
    private estados eAgora;
    private SpeechWrapper sw;
    private ActivityCriarReceita arr;
    private RVIngredienteAdapter rv_ingr;
    private RVPassosAdapter rv_pass;
    private int ingr;
    private int pass;
    private boolean primeiro;


    public ThreadCriarReceita(Receita receita,Context context, SpeechWrapper swr, ActivityCriarReceita arrr, RVIngredienteAdapter ingr, RVPassosAdapter pss) {
        this.receita = receita;
        this.context = context;
        this.sw = swr;
        this.arr = arrr;
        eAgora = estados.INICIO;
        this.ingr = 0;
        this.pass = 0;
        this.rv_ingr=ingr;
        this.rv_pass=pss;
        primeiro=true;
        para=false;

    }

    @Override
    public void run() {

        while (!para) {
            switch (eAgora){
                case INICIO:
                    Speak("adicionar ingrediente ou passo?");
                    getSpeech(prompt_pi);
                    if(!para)
                    if(hasWord(INGREDIENTE)) eAgora=estados.INGREDIENTES;
                    else if(hasWord(PASSO)) eAgora=estados.PASSOS;
                    break;
                case INGREDIENTES:
                    getSpeech(prompt_ingrediente);
                    if(!para) {
                        if (hasWord(ADICIONAR) && hasWord(PASSO)) {
                            eAgora = estados.PASSOS;
                        }if(hasWord(PARA) && !hasWord(" "+PARA) && !hasWord(PARA+" ")){
                            para=true;

                        } else {
                            receita.addIngrediente(result);
                            arr.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rv_ingr.notifyDataSetChanged();
                                }
                            });
                            Speak("próximo");
                        }
                    }
                    break;
                case PASSOS:
                    getSpeech(prompt_passo);
                    if(!para) {
                        if (hasWord(ADICIONAR) && hasWord(INGREDIENTE)) {
                            eAgora = estados.INGREDIENTES;
                        } if(hasWord(PARA) && !hasWord(" "+PARA) && !hasWord(PARA+" ")){
                            para=true;
                        } else{
                            receita.addPasso(result);
                            arr.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rv_pass.notifyDataSetChanged();
                                }
                            });
                            Speak("Próximo");
                        }
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

    public boolean hasWord(String word) {
        if(word!=null)
        return result.contains(word);
        else return false;
    }

    public void Speak(String pal) {
        if(!para) {
            sw.Speak(pal);
            while (sw.isSpeaking() && !para) ;
        }
    }

    public void getSpeech(String prompt) {
        result=null;
        if(!para) {
            arr.promptSpeechInput(prompt);
            while (!newResult && !para) Log.i("result", "not done speaking");
        }
    }

    public void sleep(int amount) {
        try {
            Thread.sleep(amount);
        } catch (Exception e) {
        }
    }
}

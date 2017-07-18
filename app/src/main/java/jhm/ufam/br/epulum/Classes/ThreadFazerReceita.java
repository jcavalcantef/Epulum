package jhm.ufam.br.epulum.Classes;


import android.content.Context;
import android.util.Log;

import jhm.ufam.br.epulum.Activities.ActivityFazerReceita;

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
    private final String ADICIONAR = "adicionar";
    private final String RECEITA ="receita";
    private final String INGREDIENTE = "ingrediente";
    private final String INGREDIENTES = "ingredients";
    private final String PASSO = "passo";
    private final String PREPARAR = "preparar";

    private Receita receita;
    private Context context;
    private String result;
    private boolean newResult;
    private boolean para;
    private boolean askedResult;
    private estados eAgora;
    private SpeechWrapper sw;
    private ActivityFazerReceita arr;
    private int ingr;
    private int pass;


    public ThreadFazerReceita(Receita receita, Context context, SpeechWrapper swr, ActivityFazerReceita arrr) {
        this.receita = receita;
        this.context = context;
        this.sw = swr;
        this.arr = arrr;
        eAgora = estados.INICIO;
        ingr = 0;
        pass = 0;
        para=false;
        askedResult = false;
    }

    @Override
    public void run() {
        while (!para) {
            ///Log.v("result","while loop");
            if(isNewResult()){
                novoResultado();
            }else if(!askedResult){
                semResultado();
            }
            sleep(30);
        }
    }

    private void novoResultado(){
        newResult = false;
        askedResult = false;
        if(hasWord(PARA)){
            eAgora=estados.PAROU;
            para=true;
        }
        else if(hasWord(INGREDIENTE))
        switch(eAgora){
            case INICIO:
                if (hasWord(INGREDIENTE) || hasWord(INGREDIENTES)) {
                    eAgora = estados.INGREDIENTES;
                    Log.v("result", "passou para ingredientes");
                } else if (hasWord(PREPARAR)) {
                    eAgora = estados.P_;
                    Log.v("result", "passou para passos");
                }
                Log.v("result", result);
                break;
            case P_:
                if (hasWord(SIM)) {
                    eAgora = estados.PASSOS;
                    Log.v("result", "passou para passos");
                } else if (hasWord(NAO)) {
                    eAgora = estados.I_P;
                    Log.v("result", "passou para I_P");
                }
                break;
            case INGREDIENTES:
                if (hasWord(SIM)) {
                    ingr++;
                    Log.v("result", "proximo ingrediente");
                } else if (hasWord(ESPERA)) {
                    sleep(3000);
                    Log.v("result", "esta esperando");
                }
                break;
            case PASSOS:
                if (hasWord(SIM)) {
                    pass++;
                    Log.v("result", "proximo passo");
                } else if (hasWord(ESPERA)) {
                    sleep(3000);
                    Log.v("result", "esta esperando");
                }
                break;
            case PAROU:
                break;
        }
    }
    private void semResultado(){
        switch (eAgora) {
            case INICIO:
                Speak("Você quer separar os ingredientes ou preparar a receita?");
                Log.v("Fazer", "parou de falar");
                getSpeech();
                Log.v("result", "recebeu resultado");
                break;
            case P_:
                Speak("você quer fazer os passos?");
                getSpeech();
                break;
            case INGREDIENTES:
                Log.v("result","ingredientes");
                if (ingr < receita.getIngredientes().size()) {
                    Speak("Separe " + receita.getIngredientes().get(ingr));
                    sleep(2000);
                    Speak("Próximo ingrediente?");
                    getSpeech();
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
        if (result != null)
            return result.contains(word);
        else return false;
    }

    public void Speak(String pal) {
        if(!para) {
            sw.Speak(pal);
            while (sw.isSpeaking() && !para) ;
        }
    }

    public void getSpeech() {
        result=null;
        if(!para) {
            arr.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arr.promptSpeechInput();
                }
            });
            askedResult = true;
        }
    }

    public void sleep(int amount) {
        try {
            Thread.sleep(amount);
        } catch (Exception e) {
        }
    }

    public boolean isNewResult() {
        return newResult;
    }

    public void setNewResult(boolean newResult) {
        this.newResult = newResult;
    }

    public boolean isPara() {
        return para;
    }

    public void setPara(boolean para) {
        this.para = para;
    }

    public boolean isAskedResult() {
        return askedResult;
    }

    public void setAskedResult(boolean askedResult) {
        this.askedResult = askedResult;
    }
}

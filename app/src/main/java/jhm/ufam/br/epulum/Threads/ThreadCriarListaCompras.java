package jhm.ufam.br.epulum.Threads;


import android.content.Context;
import android.util.Log;

import jhm.ufam.br.epulum.Activities.ActivityCriarListaCompras;
import jhm.ufam.br.epulum.Classes.ListaCompras;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.RVAdapter.RVIngredienteAdapter;

/**
 * Created by Mateus on 11/07/2017.
 */

public class ThreadCriarListaCompras implements Runnable {

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
    private final String prompt_item_novo="Fale o item novo";

    private ListaCompras receita;
    private Context context;
    private String result;
    private boolean newResult;
    private boolean para;
    private boolean askedResult;
    private estados eAgora;
    private SpeechWrapper sw;
    private ActivityCriarListaCompras arr;
    private RVIngredienteAdapter rv_ingr;
    private int ingr;
    private int pass;


    public ThreadCriarListaCompras(ListaCompras receita,Context context, SpeechWrapper swr, ActivityCriarListaCompras arrr, RVIngredienteAdapter ingr) {
        this.receita = receita;
        this.context = context;
        this.sw = swr;
        this.arr = arrr;
        eAgora = estados.INICIO;
        this.ingr = 0;
        this.pass = 0;
        this.rv_ingr=ingr;
        para=false;

    }

    @Override
    public void run() {

        while (!para) {
            switch (eAgora){
                case INICIO:
                    Speak("adicionar item novo");
                    eAgora=estados.INGREDIENTES;
                    break;
                case INGREDIENTES:
                    if(!askedResult) getSpeech();
                    if(!para) {
                        if(hasWord(PARA) && !hasWord(" "+PARA) && !hasWord(PARA+" ")){
                            para=true;

                        } else if(result!=null){
                            receita.addItem(result);
                            result=null;
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
            }
        }
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

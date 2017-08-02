package jhm.ufam.br.epulum.Threads;


import android.content.Context;
import android.util.Log;

import jhm.ufam.br.epulum.Activities.ActivityFazerReceita;
import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;

/**
 * Created by Mateus on 11/07/2017.
 */

public class ThreadFazerReceita implements Runnable {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final String key_SIM = "sim";
    private final String key_NAO = "não";
    private final String key_PARA = "para";
    private final String key_VOLTA = "volta";
    private final String REPETE = "repete";
    private final String key_ESPERA = "espera";
    private final String ADICIONAR = "adicionar";
    private final String key_RECEITA = "receita";
    private final String key_INGREDIENTE = "ingrediente";
    private final String key_INGREDIENTES = "ingredients";
    private final String key_PASSO = "passo";
    private final String key_PASSOS = "passos";
    private final String key_PREPARAR = "preparar";
    private final String key_PROXIMO = "próximo";
    private final String key_PROXIMA = "próxima";
    private final String key_IRPARA = "ir para";
    private final String key_RECIFE = "recife";
    private final String key_VOLTAR = "voltar";
    private final String key_RETORNAR = "retornar";
    private final String key_DENOVO = "de novo";
    private final String key_REPETE = "repete";
    private final String key_REPETIR = "repetir";
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
        para = false;
        askedResult = false;
    }

    @Override
    public void run() {
        while (!para) {
            ///Log.v("result","while loop");
            if (isNewResult()) {
                novoResultado();
            } else if (!askedResult) {
                semResultado();
            }
            sleep(100);
        }
        askedResult=false;
    }

    private void novoResultado() {
        newResult = false;
        if (temPara()) {
            eAgora = estados.PAROU;
            Log.v("result", "parou");
            para = true;
        } else //if(hasWord(key_INGREDIENTE))
            switch (eAgora) {
                case INICIO:
                    if (irParaIngredientes()) {
                        eAgora = estados.INGREDIENTES;
                        Log.v("result", "passou para ingredientes");
                        askedResult = false;
                    } else if (irParaPassos()) {
                        eAgora = estados.PASSOS;
                        Log.v("result", "passou para passos");
                        askedResult = false;
                    }
                    Log.v("result", result);
                    break;
            /*case P_:
                if (hasWord(key_SIM)) {
                    eAgora = estados.PASSOS;
                    Log.v("result", "passou para passos");
                } else if (hasWord(key_NAO)) {
                    eAgora = estados.I_P;
                    Log.v("result", "passou para I_P");
                }
                break;*/
                case INGREDIENTES:
                    if (proximo()) {
                        ingr++;
                        Log.v("result", "proximo ingrediente");
                        askedResult = false;
                    } else if(voltar()){
                        if(ingr>0)
                        ingr--;
                        askedResult=false;
                    }else if(repetir()){
                        askedResult=false;
                    }else if (hasWord(key_ESPERA)) {
                        Log.v("result", "esta esperando");
                    }
                    break;
                case PASSOS:
                    if (proximo()) {
                        pass++;
                        Log.v("result", "proximo passo");
                        askedResult = false;
                    } else if(voltar()){
                        if(pass>0)
                        pass--;
                        askedResult=false;
                    }else if(repetir()){
                        askedResult=false;
                    }else if (hasWord(key_ESPERA)) {
                        Log.v("result", "esta esperando");
                    }
                    break;
                case PAROU:
                    break;
            }
    }

    private boolean temPara() {
        return hasWord(key_PARA) && result.length()==4;
    }

    private boolean irParaIngredientes() {
        return hasWord(key_INGREDIENTE) || hasWord(key_INGREDIENTES);
    }

    private boolean irParaPassos() {
        return hasWord(key_PREPARAR) || hasWord(key_PASSO) || hasWord(key_PASSOS) || hasWord(key_RECEITA) || hasWord(key_RECIFE);
    }

    private boolean proximo() {
        return hasWord(key_SIM) || hasWord(key_PROXIMO) || hasWord(key_PROXIMA);
    }

    private boolean voltar(){
        return hasWord(key_VOLTA) || hasWord(key_VOLTAR) || hasWord(key_RETORNAR);
    }

    private boolean repetir(){
        return hasWord(key_DENOVO) || hasWord(key_REPETE) || hasWord(key_REPETIR);
    }

    private void semResultado() {
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
                Log.v("result", "ingredientes");
                if (ingr < receita.getIngredientes().size()) {
                    Speak("Separe " + receita.getIngredientes().get(ingr));
                    sleep(2000);
                    if (ingr < receita.getIngredientes().size() - 1) {
                        Speak("Próximo ingrediente?");
                        getSpeech();
                    } else {
                        Speak("Agora vamos para os passos.");
                        eAgora = estados.PASSOS;
                    }
                } else {
                    Speak("Agora vamos para os passos.");
                    eAgora = estados.PASSOS;
                }
                Log.v("result", "passei por ingrediente");
                break;
            case PASSOS:
                Log.v("result", "passos");
                if (pass < receita.getPassos().size()) {
                    Speak(receita.getPassos().get(pass));
                    sleep(1000);
                    if (pass < receita.getPassos().size() - 1) {
                        Speak("Próximo passo?");
                        getSpeech();
                    } else {
                        Speak("Este é o fim da receita.");
                        eAgora = estados.PAROU;
                    }
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
        if (!para) {
            sw.Speak(pal);
            while (sw.isSpeaking() && !para) ;
        }
    }

    public void getSpeech() {
        result = null;
        if (!para) {
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

    private enum estados {
        INICIO,
        I_P,
        P_,
        INGREDIENTES,
        PASSOS,
        PAROU
    }
}

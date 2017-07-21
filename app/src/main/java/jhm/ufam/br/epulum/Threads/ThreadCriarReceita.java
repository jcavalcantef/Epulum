package jhm.ufam.br.epulum.Threads;


import android.content.Context;

import jhm.ufam.br.epulum.Activities.ActivityCriarReceita;
import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.RVAdapter.RVIngredienteAdapter;
import jhm.ufam.br.epulum.RVAdapter.RVPassosAdapter;

/**
 * Created by Mateus on 11/07/2017.
 */

public class ThreadCriarReceita implements Runnable {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final String SIM = "sim";
    private final String NAO = "não";
    private final String PARA = "para";
    private final String VOLTA = "volta";
    private final String REPETE = "repete";
    private final String ESPERA = "espera";
    private final String SALVA = "salva";
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
    private boolean newResult;
    private boolean para;
    private estados eAgora;
    private SpeechWrapper sw;
    private ActivityCriarReceita arr;
    private RVIngredienteAdapter rv_ingr;
    private RVPassosAdapter rv_pass;
    private int ingr;
    private int pass;
    private boolean askedResult;
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
        para=false;
        askedResult = false;
    }

    @Override
    public void run() {
        while (!para) {
            if (newResult) {
                novoResultado();
            } else if (!askedResult) {
                semResultado();
            }
            sleep(30);
        }
    }

    public void novoResultado() {
        newResult = false;
        askedResult = false;
        if (hasWord(PARA) && !hasWord(" " + PARA) && !hasWord(PARA + " ")) {
            para = true;

        } else if(hasWord(SALVA)){
            arr.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arr.salva.performClick();
                }
            });


        } else {
            switch (eAgora) {
                case INICIO:
                    if (!para) {
                        if (hasWord(INGREDIENTE)) eAgora = estados.INGREDIENTES;
                        else if (hasWord(PASSO)) eAgora = estados.PASSOS;
                    }
                    break;
                case INGREDIENTES:
                    if (!para) {
                        if (hasWord(ADICIONAR) && hasWord(PASSO)) {
                            eAgora = estados.PASSOS;
                        } else {
                            receita.addIngrediente(result);
                            arr.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rv_ingr.notifyItemInserted(receita.getIngredientes().size() - 1);
                                }
                            });
                            Speak("próximo");
                        }
                    }
                    break;
                case PASSOS:
                    if (!para) {
                        if (hasWord(ADICIONAR) && hasWord(INGREDIENTE)) {
                            eAgora = estados.INGREDIENTES;
                        } else {
                            receita.addPasso(result);
                            arr.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rv_pass.notifyItemInserted(receita.getPassos().size() - 1);
                                }
                            });
                            Speak("Próximo");
                        }
                    }
                    break;
            }
        }
    }

    public void semResultado() {
        switch (eAgora) {
            case INICIO:
                Speak("adicionar ingrediente ou passo?");
                getSpeech();

                break;
            case INGREDIENTES:
                getSpeech();

                break;
            case PASSOS:
                getSpeech();
                break;
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

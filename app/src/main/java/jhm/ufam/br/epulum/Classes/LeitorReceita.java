package jhm.ufam.br.epulum.Classes;

/**
 * Created by Mateus on 21/06/2017.
 */

public class LeitorReceita implements Runnable {
    private Receita receita;
    private SpeechWrapper sh;
    private int posIngr;
    private int posPasso;
    private boolean canRead;
    public boolean startedReading;

    public LeitorReceita(Receita receita, SpeechWrapper shr) {
        this.receita = receita;
        sh = shr;

    }

    public LeitorReceita(SpeechWrapper shr) {
        sh = shr;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public boolean CanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
        if (!canRead) {
            sh.Stop();
        }
    }

    public void LerReceita() {
        if (!startedReading) {
            sh.Speak("Para fazer " + receita.getNome() + ".");
            sh.Speak("Você precisa de");
            posIngr = 0;
            posPasso = 0;
            startedReading = true;
        }
        while (posIngr < receita.getIngredientes().size() && canRead) {


            if (!sh.isSpeaking()) {
                sh.Speak(receita.getIngredientes().get(posIngr).toString());
                posIngr++;
            } else {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }

            }

        }
        while (posPasso < receita.getPassos().size() && canRead) {
            if (!sh.isSpeaking()) {
                sh.Speak(receita.getPassos().get(posPasso));
                posPasso++;
            } else {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }

            }

        }
    }

    @Override
    public void run() {
        LerReceita();
    }


}

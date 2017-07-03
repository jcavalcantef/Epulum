package jhm.ufam.br.epulum.Classes;

/**
 * Created by Mateus on 21/06/2017.
 */

public class LeitorReceita {
    private Receita receita;
    private SpeechHandler sh;

    public LeitorReceita(Receita receita, SpeechHandler shr) {
        this.receita = receita;
        sh=shr;
    }

    public LeitorReceita(SpeechHandler shr) {
        sh=shr;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public void LerReceita(){
        sh.Speak("Para fazer "+receita.getName()+".");
        while(sh.tts.isSpeaking());
        sh.Speak("Você precisa de");
        while(sh.tts.isSpeaking());
        int i=0;
        while(i<receita.getIngredientes().size()){
            if(!sh.tts.isSpeaking()){
            sh.Speak(receita.getIngredientes().get(i).toString());
            i++;
            }

        }
        i=0;
        while(i<receita.getPassos().size()){
            if(!sh.tts.isSpeaking()) {
                sh.Speak("Passo " + (i + 1) + receita.getPassos().get(i));
                i++;
            }
        }
    }
}

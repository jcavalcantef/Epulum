package jhm.ufam.br.epulum.Classes;

/**
 * Created by Mateus on 11-Jul-17.
 */

public interface SpeechActivator
{
    /**
     * listen for speech activation, when heard, call a {@link SpeechActivationListener}
     * and stop listening
     */
    public void detectActivation();

    /**
     * stop waiting for activation.
     */
    public void stop();
}
import java.util.Timer;
import java.util.TimerTask;

/**
 * Se encarga de ir decrementando los minutos y segundos.
 */
public class ContadorTemporizado extends TimerTask {

    private int minutos;
    private int segundos;
    private Timer timer;
    private Temporizador parent;

    public ContadorTemporizado(int minutos, int segundos, Timer timer, Temporizador parent) {
        this.minutos = minutos;
        this.segundos = segundos;
        this.timer = timer;
        this.parent = parent;
    }

    /**
     * Decrementara los minutos y segundos.
     * Una vez llegue a 0, detiene el timer y notifica a su 'parent'.
     */
    @Override
    public void run() {
        System.out.printf("Tiempo restante: %02d:%02d\n", minutos, segundos);

        if (minutos == 0 && segundos == 0) {
            System.out.println("¡Tiempo finalizado!");
            timer.cancel(); // Detiene el temporizador
            parent.signalFinished();
            return;
        }

        if (segundos == 0) {
            minutos--;
            segundos = 59;
        } else {
            segundos--;
        }
    }

    /**
     * Calcula el total de segundos restantes.
     * @return total de segundos
     */
    public int segundos() {
        return minutos * 60 + segundos;
    }
}
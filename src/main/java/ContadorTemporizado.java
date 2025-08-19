import java.util.Timer;
import java.util.TimerTask;

/**
 * Se encarga de ir decrementando los minutos y segundos.
 */
public class ContadorTemporizado extends TimerTask {

    private int minutos;
    private int segundos;
    private Timer timer;

    public ContadorTemporizado(int minutos, int segundos, Timer timer) {
        this.minutos = minutos;
        this.segundos = segundos;
        this.timer = timer;
    }

    /**
     * Decrementara los minutos y segundos por cada vez que se ejecute un pulso de {@code scheduleAtFixedRate}.
     * Una vez llegue a 0, detiene el timer.
     */
    @Override
    public void run() {
        if (minutos == 0 && segundos == 0) {
            System.out.println("¡Tiempo finalizado!");
            timer.cancel(); // Detiene el temporizador
            return;
        }

        System.out.printf("%02d:%02d\n", minutos, segundos);

        if (segundos == 0) {
            minutos--;
            segundos = 59;
        } else {
            segundos--;
        }
    }
}

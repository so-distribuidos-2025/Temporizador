import java.util.Timer;
import java.util.TimerTask;

/**
 * Se encarga de marcar el tiempo y delay de {@code ContadorTemporizado}.
 */
public class Temporizador {

    private int minutos;
    private int segundos;
    private Timer timer;

    public Temporizador(int minutos, int segundos) {
        this.minutos = minutos;
        this.segundos = segundos;
        this.timer = new Timer();
    }

    /**
     * Crea una instancia de ContadorTemporizado, y mediante el método {@code scheduleAtFiexRate},
     * ejecuta la tarea cada un segundo. El contador temporizado se encarga de ir decrementado el tiempo
     * total
     */
    public void iniciar() {
        ContadorTemporizado tarea = new ContadorTemporizado(minutos, segundos, timer);
        timer.scheduleAtFixedRate(tarea, 0, 1000); // Ejecuta cada 1 segundo
    }
}

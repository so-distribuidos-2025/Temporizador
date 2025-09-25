import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.io.PrintWriter;

/**
 * Temporizador controlable que:
 * - Envía señales al servidor (0 = listo/terminado, 1 = empieza a contar)
 * - Puede ser detenido mediante una señal en la BlockingQueue
 */
public class Temporizador {

    private int minutos;
    private int segundos;
    private final Timer timer;
    private volatile boolean isFinished = false;
    private final PrintWriter pw;

    public Temporizador(int totalSegundos, PrintWriter pw) {
        this.minutos = totalSegundos / 60;
        this.segundos = totalSegundos % 60;
        this.timer = new Timer(true); // daemon thread
        this.pw = pw;
    }

    /**
     * Inicia el temporizador y escucha la cola de señales externas.
     *
     * @param signalQueue cola compartida (0 = detener)
     */
    public void iniciar(BlockingQueue<Integer> signalQueue) {
        // Aviso inicial: listo para recibir tiempo
        System.out.println("Preparado");
        pw.println(0);

        // Hilo listener para detener el temporizador
        Thread stopListener = crearInnerStopListener(signalQueue);
        stopListener.start();

        // Aviso al servidor que empieza a contar
        pw.println(1);

        // Programar la tarea del temporizador
        TimerTask tarea = crearInnerTimerTask();
        timer.scheduleAtFixedRate(tarea, 1000, 1000);
    }

    /**
     * Crea un TimerTask que decrementa los segundos y notifica al finalizar.
     */
    private TimerTask crearInnerTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                mostrarTiempoRestante();

                if (minutos == 0 && segundos == 0) {
                    System.out.println("¡Tiempo finalizado!");
                    timer.cancel();
                    signalFinished();
                } else {
                    decrementarSegundos();
                }
            }
        };
    }

    /**
     * Crea un hilo que escucha la cola para detener el temporizador.
     */
    private Thread crearInnerStopListener(BlockingQueue<Integer> signalQueue) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int signal = signalQueue.take(); // bloquea hasta recibir señal
                    if (signal == 0) {
                        parar();
                        System.out.println("Temporizador detenido por señal externa.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void mostrarTiempoRestante() {
        System.out.printf("Tiempo restante: %02d:%02d\n", minutos, segundos);
    }

    private void decrementarSegundos() {
        if (segundos == 0) {
            minutos--;
            segundos = 59;
        } else {
            segundos--;
        }
    }

    private synchronized void signalFinished() {
        isFinished = true;
        this.notifyAll();
    }

    public synchronized void await() throws InterruptedException {
        while (!isFinished) {
            this.wait();
        }
    }

    public synchronized void parar() {
        timer.cancel();
        signalFinished();
    }
}

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;

/**
 * Clase principal del cliente temporizador y objeto que gestiona el conteo.
 */
public class Temporizador {

    private int minutos;
    private int segundos;
    private Timer timer;
    private ContadorTemporizado tarea;

    // volatile asegura que los cambios en esta variable son visibles para todos los hilos.
    private volatile boolean isFinished = false;

    public Temporizador(int segundos) {
        this.minutos = segundos / 60;
        this.segundos = segundos % 60;
        this.timer = new Timer(true); // Usar un 'daemon thread' es una buena práctica
    }

    /**
     * Inicia el contador en un hilo de fondo.
     */
    public void iniciar() {
        tarea = new ContadorTemporizado(minutos, segundos, timer, this);
        timer.scheduleAtFixedRate(tarea, 1000, 1000); // Empieza después de 1s, se repite cada 1s
    }

    /**
     * Método llamado por ContadorTemporizado cuando el tiempo llega a cero.
     * Despierta a cualquier hilo que esté esperando en el método await().
     */
    public synchronized void signalFinished() {
        this.isFinished = true;
        this.notifyAll(); // Despierta a los hilos en espera (el HiloTemporizado)
    }

    /**
     * Pone el hilo actual en espera hasta que signalFinished() sea llamado.
     */
    public synchronized void await() throws InterruptedException {
        while (!isFinished) {
            this.wait(); // Libera el lock y se pone a dormir
        }
    }

    /**
     * BUG FIX: Añadimos una comprobación de nulo. Este método fallaría si se llama
     * antes de que iniciar() haya sido invocado.
     */
    public int totalSegundos() {
        if (tarea != null) {
            return tarea.segundos();
        }
        return this.minutos * 60 + this.segundos;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java Temporizador <id>");
            return;
        }
        String id = args[0];
        PrintWriter pw;

        try {
            InetAddress ipServidor = InetAddress.getByName("localhost");
            Socket cliente = new Socket(ipServidor, 20000);
            System.out.println("Conectado al servidor: " + cliente);

            pw = new PrintWriter(cliente.getOutputStream(), true);
            pw.println("temporizador");
            pw.println(id);

            HiloTemporizado sensor = new HiloTemporizado(cliente, pw);
            sensor.start();

        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de I/O al conectar: " + e.getMessage());
        }
    }
}
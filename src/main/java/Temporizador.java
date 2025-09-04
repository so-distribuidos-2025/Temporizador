import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Se encarga de marcar el tiempo y delay de {@code ContadorTemporizado}.
 */
public class Temporizador {

    private int minutos;
    private int segundos;
    private Timer timer;
    private int id;
    private ContadorTemporizado tarea;


    public Temporizador(int minutos, int segundos) {
        this.minutos = minutos;
        this.segundos = segundos;
        this.timer = new Timer();
    }

    public Temporizador(int segundos) {
        this.segundos = segundos;
        minutos = 0;
    }

    /**
     * Crea una instancia de ContadorTemporizado, y mediante el método {@code scheduleAtFiexRate},
     * ejecuta la tarea cada un segundo. El contador temporizado se encarga de ir decrementado el tiempo
     * total
     */
    public void iniciar() {
        tarea = new ContadorTemporizado(minutos, segundos, timer);
        timer.scheduleAtFixedRate(tarea, 0, 1000); // Ejecuta cada 1 segundo
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int totalSegundos(){
            return tarea.segundos();
    }

    public static void main(String[] args) {
        InetAddress ipServidor = null;
        PrintWriter pw;
        String id = args[0];

        try {
            ipServidor = InetAddress.getByName("localhost");
            Socket cliente = new Socket(ipServidor, 20000);
            System.out.println(cliente);
            pw = new PrintWriter(cliente.getOutputStream(), true); //El segundo parametro activa el autoflush para escribir en el buffer
            pw.println("temporizador");
            pw.println(id);
            HiloTemporizado sensor = new HiloTemporizado(cliente, pw);
            sensor.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}

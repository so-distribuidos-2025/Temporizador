import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * HiloTemporizado representa un hilo que se conecta a un servidor, recibe valores en segundos,
 * ejecuta temporizadores y envía resultados de vuelta.
 */
public class HiloTemporizado extends Thread {

    private final Socket cnxServidor;
    private final PrintWriter pw;
    private final BufferedReader br;
    private Temporizador temporizador;
    private final BlockingQueue<Integer> signalQueue;

    public HiloTemporizado(Socket s, PrintWriter pw) {
        this.cnxServidor = s;
        this.pw = pw;
        this.br = crearLector(s);
        this.signalQueue = new ArrayBlockingQueue<>(1);
    }

    private BufferedReader crearLector(Socket s) {
        try {
            return new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            System.err.println("No se pudo crear el lector del socket: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void run() {
        if (br == null) return;

        try {
            while (true) {
                pw.println(1);
                ejecutarCicloTemporizador();
            }
        } catch (InterruptedException e) {
            System.err.println("El hilo del temporizador fue interrumpido.");
            Thread.currentThread().interrupt();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void ejecutarCicloTemporizador() throws InterruptedException {
        int segundos = leerSegundos();
        if (segundos <= 0) {
            //System.err.println("Se recibió un valor no válido: " + segundos + ". Ignorando.");
            return;
        }
        procesarTemporizador(segundos);
    }

    private int leerSegundos() {
        try {
            String line = br.readLine();
            return Integer.parseInt(line);
        } catch (IOException e) {
            throw new RuntimeException("Error de comunicación. El hilo terminará. " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("El servidor envió un valor no numérico.", e);
        }
    }

    private void procesarTemporizador(int segundos) throws InterruptedException {
        pw.println(0);

        System.out.println("Iniciando temporizador de " + segundos + " segundos...");

        temporizador = new Temporizador(segundos, pw);
        temporizador.iniciar(br, signalQueue);


        // Espera a que el temporizador termine o sea detenido
        temporizador.await();

        // Aviso al servidor que el temporizador finalizó
        System.out.println("Temporizacion terminada");
        temporizador.detenerParada();

        Thread.sleep(1000);
    }

    private void cerrarConexion() {
        try {
            if (cnxServidor != null && !cnxServidor.isClosed()) {
                cnxServidor.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}

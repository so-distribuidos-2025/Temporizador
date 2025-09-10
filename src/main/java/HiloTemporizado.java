import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Clase HiloTemporizado
 * ----------------------
 * Este hilo simula un temporizador que recibe un valor en segundos desde el servidor,
 * lo ejecuta mediante la clase {@link Temporizador}, y envía/visualiza los segundos restantes.
 *
 * Funcionalidades principales:
 *  - Recibe un número de segundos desde un socket.
 *  - Inicia un temporizador local con ese valor.
 *  - Mientras esté activo (`on = true`), actualiza y muestra el tiempo restante.
 *  - Cuando el tiempo llega a cero, se desactiva el temporizador y envía el resultado
 *    al servidor a través del PrintWriter.
 */
public class HiloTemporizado extends Thread {

    /** Indica si el temporizador está encendido o apagado */
    private boolean on;

    /** Temporizador asociado a este hilo */
    private Temporizador temporizador;

    /** Socket de conexión con el servidor */
    private Socket cnxServidor;

    /** Flujo de salida para enviar datos al servidor */
    private PrintWriter pw;

    /** Flujo de entrada para recibir datos desde el servidor */
    private BufferedReader br;

    /**
     * Constructor del hilo de temporización.
     *
     * @param s  Socket de conexión con el servidor
     * @param pw PrintWriter para enviar datos al servidor
     */
    public HiloTemporizado(Socket s, PrintWriter pw) {
        this.on = true;
        this.cnxServidor = s;
        this.pw = pw;
        try {
            // Inicializa el BufferedReader para leer datos entrantes
            this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Enciende el temporizador */
    public void encender() {
        on = true;
    }

    /** Apaga el temporizador */
    public void apagar() {
        on = false;
    }

    /**
     * Método principal del hilo.
     * 1. Espera recibir desde el socket el valor en segundos.
     * 2. Inicializa un objeto {@link Temporizador} con ese valor.
     * 3. Mientras el temporizador esté encendido, imprime el tiempo restante.
     * 4. Cuando el contador llega a cero, se detiene y envía el valor final.
     */
    @Override
    public void run() {
        while (true) {
            try {
                // Recibe desde el servidor el tiempo en segundos
                int segundos = Integer.parseInt(br.readLine());

                // Crea e inicia el temporizador
                Temporizador temporizador = new Temporizador(segundos);
                temporizador.iniciar();

                int totalSegundos = temporizador.totalSegundos();

                // Mientras el temporizador esté encendido
                while (on) {
                    totalSegundos = temporizador.totalSegundos();

                    // Se puede enviar al servidor o solo mostrar por consola
                    // pw.println(totalSegundos);
                    System.out.println(totalSegundos);

                    // Si el temporizador llega a cero, se apaga y se notifica
                    if (totalSegundos == 0) {
                        on = false;
                        pw.println(totalSegundos);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

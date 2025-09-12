import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HiloTemporizado extends Thread {

    private Socket cnxServidor;
    private PrintWriter pw;
    private BufferedReader br;

    public HiloTemporizado(Socket s, PrintWriter pw) {
        this.cnxServidor = s;
        this.pw = pw;
        try {
            this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            System.err.println("No se pudo crear el lector del socket: " + e.getMessage());
        }
    }

    /**
     * Método principal del hilo.
     * 1. Espera recibir desde el socket el valor en segundos.
     * 2. Crea e inicia un objeto Temporizador.
     * 3. Espera pasivamente a que el Temporizador termine.
     * 4. Cuando termina, envía el resultado final al servidor.
     */
    @Override
    public void run() {
        // Si br es nulo, el constructor falló, así que salimos.
        if (br == null) {
            return;
        }

        try {
            while (true) {
                System.out.println("\nEsperando un nuevo valor de tiempo del servidor...");
                String line = br.readLine();

                if (line == null) {
                    System.out.println("El servidor ha cerrado la conexión.");
                    break; // Salir del bucle
                }

                int segundos = Integer.parseInt(line);
                System.out.println("Iniciando temporizador de " + segundos + " segundos...");

                Temporizador temporizador = new Temporizador(segundos);

                temporizador.iniciar();

                temporizador.await();

                //Cuando await() retorna, significa que el tiempo ha finalizado.
                pw.println(0);
            }
        } catch (IOException e) {
            System.err.println("Error de comunicación. El hilo terminará. " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("El hilo del temporizador fue interrumpido.");
            Thread.currentThread().interrupt(); // Buena práctica para restaurar el estado de interrupción
        } catch (NumberFormatException e) {
            System.err.println("El servidor envió un valor no numérico.");
        } finally {
            try {
                if (cnxServidor != null && !cnxServidor.isClosed()) {
                    cnxServidor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
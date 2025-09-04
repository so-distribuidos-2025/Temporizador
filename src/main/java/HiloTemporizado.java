
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Math;
import java.net.Socket;

public class HiloTemporizado extends Thread {
    private boolean on;
    private Temporizador temporizador;
    private Socket cnxServidor;
    PrintWriter pw;
    BufferedReader br;


    public HiloTemporizado(Socket s, PrintWriter pw) {
        this.on = true;
        this.cnxServidor = s;
        this.pw = pw;
        try {
            this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void encender() {
        on = true;
    }

    public void apagar() {
        on = false;
    }

    public void run() {

        while (true) {
            try {
                int segundos = Integer.parseInt(br.readLine());
                Temporizador temporizador = new Temporizador(segundos);
                temporizador.iniciar();
                int totalSegundos = temporizador.totalSegundos();
                while (on) {
                    totalSegundos = temporizador.totalSegundos();
//                    pw.println(totalSegundos);
                    System.out.println(totalSegundos);
//                    br.reset();
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
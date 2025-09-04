
import java.io.PrintWriter;
import java.lang.Math;
import java.net.Socket;

public class HiloTemporizado extends Thread {
    private boolean on;
    private Temporizador temporizador;
    private Socket cnxServidor;
    PrintWriter pw;

    public HiloTemporizado(Socket s, PrintWriter pw) {
        this.on = true;
        this.cnxServidor = s;
        this.pw = pw;
    }

    public void encender() {
        on = true;
    }

    public void apagar() {
        on = false;
    }

    public void run() {
        Temporizador temporizador = new Temporizador(1, 30);
        temporizador.iniciar();
        int totalSegundos = temporizador.totalSegundos();
        while (on){
            totalSegundos = temporizador.totalSegundos();
            pw.println(totalSegundos);
            System.out.println(totalSegundos);
        }
    }
}
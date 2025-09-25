import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class HiloLectorParada extends Thread{
    private final BufferedReader br;
    private final BlockingQueue<Integer> signalQueue;

    public HiloLectorParada(BufferedReader br, BlockingQueue<Integer> signalQueue) {
        this.br = br;
        this.signalQueue = signalQueue;
    }

    @Override
    public void run() {
        int lectura = 1;
        while (lectura != 0) {
            try {
                lectura = Integer.parseInt(br.readLine());
                if (lectura == 0){
                    signalQueue.add(lectura);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

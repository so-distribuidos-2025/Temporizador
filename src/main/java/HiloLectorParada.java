import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class HiloLectorParada extends Thread{
    private final BufferedReader br;
    private final BlockingQueue<Integer> signalQueue;
    private boolean on;

    public void terminar(){
        this.on = false;
    }

    public HiloLectorParada(BufferedReader br, BlockingQueue<Integer> signalQueue) {
        this.br = br;
        this.signalQueue = signalQueue;
    }

    @Override
    public void run() {
        int lectura = 1;
        while (!Thread.currentThread().isInterrupted() && lectura != 0) {
            try {
                String line = br.readLine();
                if (line == null) break; // end of stream
                lectura = Integer.parseInt(line);
                if (lectura == 0){
                    signalQueue.add(lectura);
                }
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    break; // exit gracefully if interrupted during blocking read
                }
                throw new RuntimeException(e);
            }
        }
    }

}

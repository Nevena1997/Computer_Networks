package i03_blocking_queue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

final class SearchFileRunnable implements Runnable {

    private BlockingQueue<Path> queue;
    private String keyword;


    SearchFileRunnable(BlockingQueue<Path> queue, String keyword) {
        this.queue = queue;
        this.keyword = keyword;
    }


    @Override
    public void run() {
        try {
            boolean done = false;
            while (!done) {
                Path p = this.queue.take(); // blocks if queue is empty
                if (p == FileTreeWalkerRunnable.END_OF_WORK) {
                    done = true;
                    // We need to put it back so the other threads can also know
                    // that they need to stop
                    this.queue.put(p);
                } else {
                    this.search(p);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Search a file at given path for a keyword
    private void search(Path f) {
        try (Scanner sc = new Scanner(f)) {
            for (int ln = 1; sc.hasNextLine(); ln++) {
                String line = sc.nextLine();
                if (line.contains(this.keyword))
                    System.out.printf("%s:%d\n", f.getFileName(), ln);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

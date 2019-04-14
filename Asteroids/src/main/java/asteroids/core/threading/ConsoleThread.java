package asteroids.core.threading;

import asteroids.core.graphics.Renderer;

import java.util.Scanner;

public class ConsoleThread implements Runnable {

    private Scanner scanner;
    private Renderer renderer;

    public ConsoleThread(Renderer renderer) {
        this.renderer = renderer;
        this.scanner = new Scanner(System.in);
        Thread t = new Thread(this, "ConsoleThread");
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            String in = scanner.nextLine();
            in = in.trim();

            if (in.equals("quit")) {
                renderer.quit();
                return;
            }
        }
    }
}

package cn.hamster3.application.launcher.thread;

import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;

public class StreamRedirectThread extends Thread {
    private final Process process;
    private volatile boolean stop;

    private final CompletableFuture<Void> launchFuture;

    public StreamRedirectThread(Process process) {
        this(process, null);
    }

    public StreamRedirectThread(Process process, CompletableFuture<Void> launchFuture) {
        this.process = process;
        this.launchFuture = launchFuture;
        stop = false;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[1024 * 1024];
        while (process.isAlive() && !stop) {
            try {
                int readSize = process.getInputStream().read(bytes);
                if (readSize > 0) {
                    printText(new String(bytes, 0, readSize));
                    continue;
                }
                if (process.getErrorStream().available() > 0) {
                    readSize = process.getErrorStream().read(bytes);
                    if (readSize > 0) {
                        printText(new String(bytes, 0, readSize));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private void printText(String output) {
        if (launchFuture != null && output.contains("Backend library: LWJGL version")) {
            launchFuture.complete(null);
        }
        System.out.print(output);
    }

    private void shutdown() {
        if (process == null) {
            return;
        }
        int exitValue = process.exitValue();
        printText("\n程序已结束，退出代码: " + exitValue + "\n");
        Platform.exit();
    }
}

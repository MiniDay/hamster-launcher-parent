package cn.hamster3.application.launcher.thread;

import javafx.application.Platform;

public class StreamRedirectThread extends Thread {
    private final Process process;

    private volatile boolean stop;

    public StreamRedirectThread(Process process) {
        this.process = process;
        stop = false;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[1024 * 1024];
        while (!stop) {
            try {
                int readSize = process.getInputStream().read(bytes);
                if (readSize <= 0) {
                    exit();
                    break;
                }
                String output = new String(bytes, 0, readSize);
                if (process.getErrorStream().available() > 0) {
                    readSize = process.getErrorStream().read(bytes);
                    if (readSize > 0) {
                        output = output + new String(bytes, 0, readSize);
                    }
                }
                printText(output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private void printText(String output) {
        System.out.print(output);
    }

    private void exit() {
        if (process == null) {
            return;
        }
        int exitValue = process.exitValue();
        printText("\n程序已结束，退出代码: " + exitValue + "\n");
        Platform.exit();
    }
}

package cn.hamster3.application.launcher.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadUtils {
    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void exec(Runnable runnable) {
        executorService.execute(runnable);
    }

}

package com.yocto.wetodo.repository;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
    private static final ExecutorService executorForRepository = Executors.newSingleThreadExecutor();

    private Utils() {
    }

    public static Executor getExecutorForRepository() {
        return executorForRepository;
    }
}

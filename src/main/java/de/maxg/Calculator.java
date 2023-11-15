package de.maxg;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Calculator {

    @Getter@Setter
    private BigDecimal wantedExecutions;
    @Getter@Setter
    private volatile AtomicReference<BigDecimal> executionCounter = new AtomicReference<>();
    @Getter@Setter
    private BigDecimal augend;
    @Getter@Setter
    private volatile AtomicReference<BigDecimal> result = new AtomicReference<>();
    @Getter@Setter
    private int maxThreads;
    @Getter

    private volatile boolean isExecutionLimitReached;
    private List<Thread> threadList = new ArrayList<>();
    public Calculator() {
        executionCounter.set(BigDecimal.ZERO);
        result.set(BigDecimal.ZERO);
    }

    public BigDecimal calculate() {

        for (int i = 0; i < maxThreads; i++) {
            threadList.add(new Thread(new CalculatorThread(this)));
        }

        threadList.forEach(thread -> thread.start());

        threadList.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return result.get();
    }

    public boolean incrementExecutionCounter() {
        BigDecimal updatedValue = executionCounter.updateAndGet(bigDecimal -> bigDecimal.add(BigDecimal.ONE));
        if (updatedValue.compareTo(wantedExecutions) > 0) {
            isExecutionLimitReached = true;
            return false;
        }
        return true;
    }

    public void addToResult(BigDecimal augend) {
        result.updateAndGet(bigDecimal -> bigDecimal.add(augend));
    }
}

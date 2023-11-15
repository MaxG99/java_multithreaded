package de.maxg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CalculatorTest {

    public Calculator calculator;

    @BeforeEach
    void setup() {
        calculator = Mockito.spy(new Calculator());
    }

    @Test
    void initResultWithZero() {
        Assertions.assertTrue(calculator.getResult().get().equals(BigDecimal.ZERO));
    }

    @Test
    void assertWantedCalculationsSet() {
        BigDecimal wantedCalculations = BigDecimal.valueOf(10);
        calculator.setWantedExecutions(wantedCalculations);
        Assertions.assertEquals(wantedCalculations, calculator.getWantedExecutions());
    }

    @Test
    void calculateWithOneThread() {
        BigDecimal wantedCalculations = BigDecimal.valueOf(10);
        calculator.setWantedExecutions(wantedCalculations);
        calculator.setAugend(BigDecimal.ONE);
        calculator.setMaxThreads(1);
        BigDecimal expectedResult = calculator.getAugend().multiply(wantedCalculations);

        calculator.calculate();

        verify(calculator, times(wantedCalculations.intValue())).addToResult(any());
        Assertions.assertEquals(expectedResult, calculator.getResult().get());
    }

    @Test
    void calculateWithTwoThreads() {
        BigDecimal wantedCalculations = BigDecimal.valueOf(10);
        calculator.setWantedExecutions(wantedCalculations);
        calculator.setAugend(BigDecimal.ONE);
        calculator.setMaxThreads(2);
        BigDecimal expectedResult = calculator.getAugend().multiply(wantedCalculations);

        calculator.calculate();

        verify(calculator, times(wantedCalculations.intValue())).addToResult(any());
        Assertions.assertEquals(expectedResult, calculator.getResult().get());
    }

    @Test
    void calculateWithMultipleThreads() {
        BigDecimal wantedCalculations = BigDecimal.valueOf(1000);
        calculator.setWantedExecutions(wantedCalculations);
        calculator.setAugend(BigDecimal.ONE);
        calculator.setMaxThreads(7);
        BigDecimal expectedResult = calculator.getAugend().multiply(wantedCalculations);

        calculator.calculate();

        verify(calculator, times(wantedCalculations.intValue())).addToResult(any());
        int invocations = Mockito.mockingDetails(calculator)
                .getInvocations()
                .stream()
                .filter(invocation -> invocation.getMethod().getName().equals("addToResult"))
                .collect(Collectors.toList())
                .size();
        System.out.println(String.format("%d of %d calculations performed", invocations, wantedCalculations.intValue()));
        Assertions.assertEquals(expectedResult, calculator.getResult().get());
    }

    @Test
    @Disabled
    void multithreadingExtremeTest() {
        int wantedExecutionsUpperBound = 100000001;
        int wantedExecutionsLowerBound = 100000000;
        int ThreadsUpperBound = 4;
        int ThreadsLowerBound = 3;
        Random random = new Random();
        for (int iteration = 0; iteration < 10; iteration++) {
            BigDecimal wantedExecutions = BigDecimal.valueOf(random.nextInt(wantedExecutionsUpperBound - wantedExecutionsLowerBound) + wantedExecutionsLowerBound);
            BigDecimal augend = BigDecimal.valueOf(random.nextInt(10 - 1) + 1);
            int maxThreads = random.nextInt(ThreadsUpperBound - ThreadsLowerBound) + ThreadsLowerBound;
            calculator =new Calculator();
            calculator.setWantedExecutions(wantedExecutions);
            calculator.setAugend(augend);
            calculator.setMaxThreads(maxThreads);
            BigDecimal expectedResult = augend.multiply(wantedExecutions);

            long start = System.currentTimeMillis();
            calculator.calculate();
            long stop = System.currentTimeMillis();
            double duration = (stop - start) / 1000d;

            boolean resultCorrect = expectedResult.equals(calculator.getResult());

            String matchesText = resultCorrect ? "matches" : "does not match";
            System.out.println(String.format("Iteration %d finished in %f seconds, %d of %d calculations were run in %d threads. The result %d %s the expected result %d",
                    iteration, duration, wantedExecutions.intValue(), wantedExecutions.intValue(), maxThreads, calculator.getResult().get().intValue(), matchesText, expectedResult.intValue()));
            Assertions.assertEquals(expectedResult, calculator.getResult());
        }
    }

    @Test
    void incrementExecutionCounter() {
        calculator.setWantedExecutions(BigDecimal.ONE);
        calculator.incrementExecutionCounter();
        Assertions.assertEquals(BigDecimal.ONE, calculator.getExecutionCounter().get());
    }

    @Test
    void addToResult() {

        BigDecimal augend = BigDecimal.valueOf(52.42);

        Assertions.assertTrue(calculator.getResult().get().equals(BigDecimal.ZERO));
        calculator.addToResult(augend);
        Assertions.assertEquals(augend, calculator.getResult().get());
    }
}
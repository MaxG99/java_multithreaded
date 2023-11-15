package de.maxg;

public class CalculatorThread implements Runnable{

    Calculator calculator;
    public CalculatorThread(Calculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void run() {
        while (!calculator.isExecutionLimitReached()) {

            if (calculator.incrementExecutionCounter()) {
                calculator.addToResult(calculator.getAugend());
            }
        }
    }
}
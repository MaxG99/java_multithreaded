package de.maxg;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
//        simple calculator that adds the configured value the specified number of times
//        execution of the calculation is split to multiple threads
//        asd
        Calculator calculator = new Calculator();
        calculator.setMaxThreads(4);
        calculator.setWantedExecutions(BigDecimal.valueOf(1000));
        calculator.setAugend(BigDecimal.ONE);

        BigDecimal result = calculator.calculate();

        System.out.println(String.format("Result of calculation was %d", result.intValue()));
    }
}
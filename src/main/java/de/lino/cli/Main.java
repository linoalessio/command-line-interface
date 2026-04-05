package de.lino.cli;

import de.lino.cli.setup.Setup;

public class Main {

    public static void main(String[] args) {

        final Setup setup = new Setup();

        setup.introduce(() -> {

            System.out.println("Setup has been introduced successfully.");

            setup
                    .nextStep(stepId -> {
                        System.out.println(String.format("Current step: %d", stepId));
                    }).nextStep(stepId -> {
                        System.out.println(String.format("Current step: %d", stepId));
                    }).nextStep(stepId -> {
                        System.out.println(String.format("Current step: %d", stepId));
                    }).nextStep(stepId -> {
                        System.out.println(String.format("Current step: %d", stepId));
                    }).nextStep(stepId -> {
                        System.out.println(String.format("Current step: %d", stepId));
                    })
                    .finish(stepId -> {
                        System.out.println(String.format("Finished step: %d", stepId));
                    });

        });

    }

}

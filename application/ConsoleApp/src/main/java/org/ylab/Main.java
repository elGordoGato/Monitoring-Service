package org.ylab;

public class Main {
    public static void main(String[] args) {
        ManualConfig.setInMemoryRepo();
        ConsoleController controller = new ConsoleController(System.in,
                ManualConfig.getUserService(),
                ManualConfig.getReadingServiceByUser(),
                ManualConfig.getReadingServiceByAdmin(),
                ManualConfig.getMeterService());
        controller.start();
    }
}
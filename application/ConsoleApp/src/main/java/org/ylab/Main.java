package org.ylab;

public class Main {
    public static void main(String[] args) {
        ManualConfig.setJdbcRepo();
        ConsoleController controller = new ConsoleController(System.in,
                ManualConfig.getUserService(),
                ManualConfig.getReadingServiceByUser(),
                ManualConfig.getReadingServiceByAdmin(),
                ManualConfig.getMeterService());
        controller.start();
        ConnectionManager.closeConnection();
    }
}
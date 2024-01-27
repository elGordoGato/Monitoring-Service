package org.ylab.in;

import org.ylab.dto.UserDto;
import org.ylab.entity.MeterReading;
import org.ylab.entity.MeterType;
import org.ylab.entity.User;
import org.ylab.service.*;
import org.ylab.utils.mapper.UserMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

public class ConsoleController {
    private final BufferedReader br;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final UserService userService = new UserServiceImpl();
    private final MeterReadingService readingService = new MeterReadingServiceImpl();
    private final MeterTypeService typeService = new MeterTypeServiceImpl();
    private final List<String> log = new ArrayList<>();

    public ConsoleController(InputStream inputStream) {
        br = new BufferedReader(new InputStreamReader(inputStream));
        start();
    }

    private void start() {
        boolean isRunning = true;
        log.add(now() + " - App is started");
        while (isRunning) {
            try {

                System.out.println(
                        """
                                1 - Register new user
                                2 - Login existing user
                                3 - Exit""");
                String input = br.readLine();
                int command = Integer.parseInt(input);
                User currentUser = null;
                switch (command) {
                    case 1 -> {
                        currentUser = registerUser();
                        log.add(now() + "Registered new user: " + currentUser);
                    }
                    case 2 -> {
                        currentUser = loginUser();
                        log.add(now() + "User logged in: " + currentUser);
                    }
                    case 3 -> {
                        log.add("Received request to stop app");
                        isRunning = false;
                    }
                    default -> System.out.println("This command is not supported");
                }
                if (currentUser != null) {
                    isRunning = handleAuthorizedUser(currentUser);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Wrong input");
            } catch (RuntimeException e) {
                log.add(now() + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
        log.add(now() + " - App is stopped");
    }

    private boolean handleAuthorizedUser(User currentUser) throws IOException {
        while (true) {
            try {
                System.out.println(
                        """
                                1 - Получить актуальные показания счетчиков
                                2 - Подать показания
                                3 - Просмотреть показания за конкретный месяц
                                4 - Просмотреть историю подачи показаний
                                5 - Выйти из учетной записи
                                6 - Выйти из программы""");
                int command = Integer.parseInt(br.readLine());
                switch (command) {
                    case 1 -> System.out.println(getActualReadings(currentUser));
                    case 2 -> System.out.println(sendReading(currentUser));
                    case 3 -> System.out.println(getReadingsForMonth(currentUser));
                    case 4 -> System.out.println(getHistory(currentUser));
                    case 5 -> {
                        log.add("Received request to log out");
                        return true;
                    }
                    case 6 -> {
                        log.add("Received request to stop app");
                        return false;
                    }
                    default -> System.out.println("This command is not supported");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input");
            } catch (RuntimeException e) {
                log.add(now() + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
    }

    private String getHistory(User currentUser) {
        log.add(now() + " - Received request to get all history of readings");
        return convertToString(readingService.getAllByUser(currentUser));
    }

    private String getReadingsForMonth(User currentUser) throws IOException {
        System.out.println("Please enter the year:");
        int year = Integer.parseInt(br.readLine());
        System.out.println("Please enter the month number:");
        int month = Integer.parseInt(br.readLine());
        LocalDate date = LocalDate.of(year, month, 1);
        log.add(String.format("%s - Received request to get readings for %s.%s", now(), month, year));
        return convertToString(readingService.getForMonth(currentUser, date));
    }

    private String sendReading(User currentUser) throws IOException {
        List<MeterType> availableTypes = typeService.getAll();
        System.out.println("Какой тип счетчика хотите подать?");
        availableTypes.forEach(t -> System.out.printf("%s - %s%n", t.getId(), t.getName()));
        int typeId = Integer.parseInt(br.readLine());
        MeterType type = typeService.getById(typeId);
        System.out.println("Введите показания для счетчика: " + type.getName());
        long reading = Long.parseLong(br.readLine());
        log.add(String.format("%s - Received request to send readings: %s - %s",
                now(), type.getName(), reading));
        readingService.create(currentUser, type, reading);
        return "Readings successfully sent";
    }

    private String getActualReadings(User currentUser) {
        log.add(now() + " - Received request to get actual readings");
        List<MeterReading> meterReadings = readingService.getActual(currentUser);
        return convertToString(meterReadings);
    }

    private User loginUser() throws IOException {
        System.out.println("Please enter your email:");
        String email = br.readLine();
        System.out.println("Please enter your password:");
        String password = br.readLine();
        log.add(String.format("%s - Received request to login user with email: %s and password: %s",
                now(), email, password));
        return userService.authenticate(email, password);
    }

    private User registerUser() throws IOException {
        UserDto userToCreate = new UserDto();
        System.out.println("Please enter your email:");
        userToCreate.setEmail(br.readLine());
        System.out.println("Please enter your password:");
        userToCreate.setPassword(br.readLine());
        System.out.println("Please enter your first name:");
        userToCreate.setFirstName(br.readLine());
        System.out.println("Please enter your last name:");
        userToCreate.setLastName(br.readLine());
        log.add(now() + " - Received request to register new user: " + userToCreate);
        return userService.create(
                UserMapper.dtoToEntity(userToCreate));
    }

    private String convertToString(List<MeterReading> readings) {
        StringBuilder sb = new StringBuilder();
        readings.forEach(r ->
                sb.append(String.format("%s - %s: %s\n",
                        FORMATTER.format(r.getCollectedDate().atZone(ZoneId.systemDefault())),
                        r.getMeter().getName(),
                        r.getReading())));
        return sb.toString();
    }
}

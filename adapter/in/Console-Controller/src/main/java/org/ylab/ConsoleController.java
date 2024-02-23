package org.ylab;


import org.ylab.dto.UserDto;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.UserEntity;
import org.ylab.enums.Role;
import org.ylab.mapper.UserMapper;
import org.ylab.mapper.UserMapperImpl;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;
import org.ylab.user.UserService;

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

/**
 * A class that handles console input and output for a monitoring service.
 * The class allows users to register, login, and perform various operations related to their accounts.
 * The class also maintains a log of the actions performed by the users and the system.
 *
 * @author Leonid
 * @version 0.1
 */
public class ConsoleController {


    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final BufferedReader br;
    /**
     * @see UserService
     */
    private final UserService userService;
    private final ReadingService readingServiceForUser;
    private final ReadingService readingServiceForAdmin;
    /**
     * @see MeterService
     */
    private final MeterService typeService;
    /**
     * Аудит действий пользователя (авторизация, завершение работы, подача показаний, получение истории подачи показаний и тд)
     */
    private final List<String> log = new ArrayList<>();
    private final UserMapper userMapper;
    /**
     * @see ReadingService
     */
    private ReadingService readingService;

    public ConsoleController(InputStream inputStream,
                             UserService userService,
                             ReadingService readingServiceForUser,
                             ReadingService readingServiceForAdmin,
                             MeterService typeService) {
        br = new BufferedReader(new InputStreamReader(inputStream));
        this.userService = userService;
        this.readingServiceForUser = readingServiceForUser;
        this.readingServiceForAdmin = readingServiceForAdmin;
        this.typeService = typeService;
        this.userMapper = new UserMapperImpl();
    }

    /**
     * main method to start the app
     */
    public void start() {
        String input;
        boolean isRunning = true;
        log.add(now() + " - App is started");
        while (isRunning) {
            try {

                System.out.println(
                        """
                                1 - Register new user
                                2 - Login existing user
                                3 - Exit""");
                input = br.readLine();
                int command = Integer.parseInt(input);
                UserEntity currentUser = null;
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
                System.out.println("Wrong input: " + e.getMessage());
            } catch (RuntimeException e) {
                log.add(now() + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
        log.add(now() + " - App is stopped");
    }


    /**
     * @param currentUser user that has been authorized
     * @return boolean value indicating to stop the app or continue working
     * @throws IOException exceptions produced by failed or interrupted I/O operations.
     */
    private boolean handleAuthorizedUser(UserEntity currentUser) throws IOException {
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);
        setToAdmin(isAdmin);
        while (true) {
            try {
                System.out.println(
                        "1 - Получить актуальные показания счетчиков\n" +
                                "2 - " + (isAdmin ? "Добавить новый тип счетчика" : "Подать показания") + "\n" +
                                "3 - Просмотреть показания за конкретный месяц\n" +
                                "4 - Просмотреть историю подачи показаний\n" +
                                "5 - Выйти из учетной записи\n" +
                                "6 - Выйти из программы");
                int command = Integer.parseInt(br.readLine());
                switch (command) {
                    case 1 -> System.out.println(getActualReadings(currentUser));
                    case 2 -> System.out.println(isAdmin ? createMeter() : sendReading(currentUser));
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
                System.out.println("Wrong input: " + e.getMessage());
            } catch (RuntimeException e) {
                log.add(now() + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * @param currentUser user that has been authorized
     * @return String of actual (last for each meter) readings submitted by currentUser
     */
    private String getActualReadings(UserEntity currentUser) {
        log.add(now() + " - Received request to get actual readings");
        List<Reading> readings = readingService.getActual(currentUser);
        return convertToString(readings);
    }

    /**
     * @param currentUser user that has been authorized
     * @return String that the reading was successfully sent
     * @throws IOException exception produced by failed or interrupted I/O operations.
     */
    private String sendReading(UserEntity currentUser) throws IOException {
        List<Meter> availableTypes = typeService.getAll();
        System.out.println("Какой тип счетчика хотите подать?");
        availableTypes.forEach(t -> System.out.printf("%s - %s%n", t.getId(), t.getType()));
        short typeId = Short.parseShort(br.readLine());
        Meter type = typeService.getById(typeId);
        System.out.println("Введите показания для счетчика: " + type.getType());
        long reading = Long.parseLong(br.readLine());
        log.add(String.format("%s - Received request to send readings: %s - %s",
                now(), type.getType(), reading));
        readingService.create(currentUser, type, reading);
        return "Readings successfully sent";
    }

    private String createMeter() throws IOException {
        var meterToCreate = new Meter();
        System.out.println("Please enter type of new meter:");
        meterToCreate.setType(br.readLine());
        log.add(now() + " - Received request to create new type of meter: " + meterToCreate.getType());
        return "Meter successfully created: " + typeService.create(meterToCreate);
    }

    /**
     * @param currentUser user that has been authorized
     * @return String of all readings submitted by currentUser for selected month
     * @throws IOException exception produced by failed or interrupted I/O operations.
     */
    private String getReadingsForMonth(UserEntity currentUser) throws IOException {
        System.out.println("Please enter the year:");
        int year = Integer.parseInt(br.readLine());
        System.out.println("Please enter the month number:");
        int month = Integer.parseInt(br.readLine());
        LocalDate date = LocalDate.of(year, month, 1);
        log.add(String.format("%s - Received request to get readings for %s.%s", now(), month, year));
        return convertToString(readingService.getForMonth(currentUser, date));
    }

    /**
     * @param currentUser user that has been authorized
     * @return String of all submitted readings by currentUser
     */
    private String getHistory(UserEntity currentUser) {
        log.add(now() + " - Received request to get all history of readings");
        return convertToString(readingService.getAllByUser(currentUser));
    }

    /**
     * @return User entity that has been registered
     * @throws IOException exception produced by failed or interrupted I/O operations.
     */
    private UserEntity registerUser() throws IOException {
        var userToCreate = new UserDto();
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
                userMapper.toUser(userToCreate));
    }

    /**
     * @return User entity that has been authorized
     * @throws IOException exception produced by failed or interrupted I/O operations.
     */
    private UserEntity loginUser() throws IOException {
        System.out.println("Please enter your email:");
        String email = br.readLine();
        System.out.println("Please enter your password:");
        String password = br.readLine();
        log.add(String.format("%s - Received request to login user with email: %s and password: %s",
                now(), email, password));
        return userService.authenticate(email, password);
    }

    /**
     * @param readings List of readings to convert in nice string
     * @return nice string of the readings from list
     */
    private String convertToString(List<Reading> readings) {
        StringBuilder sb = new StringBuilder();
        readings.forEach(r ->
                sb.append(String.format("%s - %s: %s\n",
                        FORMATTER.format(r.getCollectedDate().atZone(ZoneId.systemDefault())),
                        r.getMeter().getType(),
                        r.getReading())));
        return sb.toString();
    }

    /**
     * @param isAdmin boolean that indicates if authorized user is admin
     *                Setting readingService to corresponding implementation
     */
    private void setToAdmin(boolean isAdmin) {
        if (isAdmin) readingService = readingServiceForAdmin;
        else readingService = readingServiceForUser;
    }
}

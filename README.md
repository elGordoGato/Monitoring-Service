# Monitoring-Service

Веб-сервис для подачи показаний счетчиков отопления, горячей и холодной воды

---

### Описание

- Показания можно подавать один раз в месяц.
- Ранее поданные показания редактировать запрещено.
- Последние поданные показания считаются актуальными.
- Пользователь может видеть только свои показания, администратор может видеть показания всех пользователей.
- Реализация соответствует описанным ниже требованиям и ограничениям.

---

### Требования

- предусмотреть расширение перечня подаваемых показаний
- данные хранятся в памяти приложения
- приложение должно быть консольным (никаих спрингов, взаимодействий с БД и тд, только java-core и collections)
- регистрация пользователя
- авторизация пользователя
- реализовать эндпоинт для получения актуальных показаний счетчиков
- реализовать эндпоинт подачи показаний
- реализовать эндпоинт просмотра показаний за конкретный месяц
- реализовать эндпоинт просмотра истории подачи показаний
- реализовать контроль прав пользователя
- Аудит действий пользователя (авторизация, завершение работы, подача показаний, получение истории подачи показаний и
  тд)

### Нефункциональные требования

Unit-тестирование

Доп. материалы: https://drive.google.com/drive/folders/1rZaq58yAAcBKZqBeNSfpc6kb-GkuqHs0?usp=sharing

---

## Стек:

> Java 17, Maven, Lombok, JUnit, Mockito, AssertJ

---

## Запуск приложения:

1) Склонировать репозиторий
   > `git clone https://github.com/elGordoGato/TicketParser.git`

2) Перейти в корневую директорию проекта
   > `cd TicketParser/`

3) Находясь в корневой папке собрать проект
   > `mvn clean package`
4) Запустить сгенерированный jar-файл
   > `java -jar target/TicketParser-1.0-SNAPSHOT.jar resources/tickets.json`

## Ответы:

Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика:

- SU: 06:00
- S7: 06:30
- TK: 05:50
- BA: 08:05

Разница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив:

- 460.00 руб.
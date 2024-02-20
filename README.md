# Monitoring-Service

Веб-сервис для подачи показаний счетчиков отопления, горячей и холодной воды

---

### Описание

- Показания можно подавать один раз в месяц.
- Ранее поданные показания редактировать запрещено.
- Последние поданные показания считаются актуальными.
- Пользователь может видеть только свои показания, администратор может видеть показания всех пользователей.

---

### Pull Requests

1) [Pull request "Вводная"](https://github.com/elGordoGato/Monitoring-Service/pull/1)

2) [Pull request "JDBC. Миграции БД"](https://github.com/elGordoGato/Monitoring-Service/pull/2)

3) [Pull request "Сервлеты. АОП"](https://github.com/elGordoGato/Monitoring-Service/pull/3)

4) [Pull request "Знакомство с Spring Framework"](https://github.com/elGordoGato/Monitoring-Service/pull/4)

---

## Стек:

> Java 17, Spring Framework, Maven, Postgresql, HttpServlet, AspectJ, MapStruct, Jackson, Lombok, JUnit, Mockito,
> AssertJ, Testcontainers

---

### Спецификация API

[Документация OpenAPI (Swagger)](https://petstore.swagger.io/?url=https://gist.githubusercontent.com/elGordoGato/4cfb79941db3b8f81913fda7797c63a1/raw/4517fe7e63c3bcfe13b11bfbbb3f979420c36290/monitoring-service-open-api.yml)

Файл со спецификацией можно найти по пути: `swagger/monitoring-service-open-api.yml`

---

## Запуск приложения:

1) Находясь в корневой директории собрать проект:
   > `mvn clean package`
2) Запустить docker compose:
   > `docker compose up`

# Реєстр акцій
REST сервіс дозволяє створювати, видаляти, оновлювати, отримувати (з фільтрацією) записи реєстру.

### Стек
Java 8, Maven, Spring Boot, Spring Data JPA, Spring Web, REST, Hibernate, PostgreSQL, Liquibase, Lombok, H2 Database (тести), JUnit, Vue.js, Bootstrap (JS, CSS, HTML).

При старті, якщо БД порожня, наповнюється даними з файлу stock.json.
Stock - клас сутності, містить унікальний ID, унікальний ЄДРПОУ, кількість, загальну номінальну вартість (розраховується за допомогою @Formula), номінальну вартість, дату випуску, статус, коментар.
Пошук по базі за ЄДРПОУ та статусом, результат віддається сторінкою (обирається) з обраною кількість записів. Запис видаляється зміною статусу на "DELETED". Редагування ЄДРПОУ неможливе.

Історія змін у БД записується в окрему таблицю. Лише при оновленні існуючього запису.

Тести БД, сервісу, контролеру. Всього 33.

### Запити
Всі записи 
```sh
curl --location --request GET 'http://localhost:8080/stock'
```
Пошук та результат за параметрами
```sh
curl --location --request GET 'http://localhost:8080/stock?code=123&status=ACTIVE&pageNum=1&pageSize=25'
```
Запис з ID
```sh
curl --location --request GET 'http://localhost:8080/stock/1'
```
Cтворити запис
```sh
curl --location --request POST 'http://localhost:8080/stock' \
--header 'Content-Type: application/json' \
--data-raw '{
        "code": "12340000",
        "amount": 100,
        "faceValue": 5,
        "date": "2020-01-01",
        "comment": "one"
    }'
```
Оновити запис (code не змінюється)
```sh
curl --location --request PUT 'http://localhost:8080/stock/1' \
--header 'Content-Type: application/json' \
--data-raw '{
        "id": 1,
        "code": "00000808",
        "amount": 10,
        "faceValue": 75,
        "date": "2017-07-07",
        "totalFaceValue": 750,
        "status": "ACTIVE",
        "comment": "two"
    }'
```
Видалити запис
```sh
curl --location --request DELETE 'http://localhost:8080/stock/1'
```

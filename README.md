# Сервис аренды вещей
Проект представляет собой бэкэнд приложения для шеринга(аренды вещей). Сервис предоставляет пользователям возможность размещать вещи на платформе, которые они готовы дать в аренду, бронировать вещи на нужную дату, а если нужной вещи на платформе не опубликовано, - то создать на неё запрос. Проект реализован полностью самостоятельно с целью попрактиковать написание многомодульного проекта с контейнеризацией, использование ORM-фреймворка Hibernate и написания тестов с высоким порогом покрытия кода.

## Основные возможности:
Основные сущности - пользователи и вещи. Пользователь может создавать/редактировать карточку своей вещи на платформе, другие пользователи могут её забронировать на определённое время, вещь будет убрана с платформы на время аренды. </br>
- Поиск - поиск вещи потенциальным арендатором. Осуществляется по названию и описанию опубликованных вещей. </br>
- Запрос на бронирование - пользователь может отправить запрос на бронирование интересующей его вещи, а владелец отклонить или подтвердить бронирование. </br>
- Получение данных о конкретном бронировании - арендодатель или арендатор могут просматривать информацию по конкретному бронированию.  </br>
- Получение списка бронирований - как арендодатель, так и арендатор, могут просматривать список всех своих бронирований с возможностью фильтрации. </br>
- Отзывы - пользователи могут оставлять отзывы на вещь, которую брали в аренду.

## Стек используемых технологий, основные зависимости:
- Java 11 Amazon Correto
- Spring Boot (Cтартеры: web, validation, test)
- Hibernate
- Lombok
- PostgreSQL
- Maven
- MapStruct
- Docker

Дополнительно: </br>
REST API, для некоторых запросов реализована постраничная выгрузка - пагинация.
Стандартная трёхуровневая архитектура. </br> 
Пакеты разбиты по отдельным фичам. </br>
В проекте два модуля. Getaway - шлюз для HTTP-запросов, в котором проходит валидация данных. Server - модуль с бизнес-логикой. Используются docker-контейнеры с соответствующими названиями и ещё один с облегченной версией PostgreSQL. </br>
На проекте есть тесты с покрытием кода, для этого подключена библиотека Jacoco с установленными значениями покрытия. Также проект тестируется postman-коллекцией. </br>

## Сборка и запуск:
Поскольку на проекте используется docker, в каждом модуле есть dockerfile и docker-compose в корне для развертывания приложения, достаточно скомпилировать проект и поднять через compose удобным способом, например, через консоль или графический интерфейс плагина в IDE.

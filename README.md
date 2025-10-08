# java-filmorate
Template repository for Filmorate project.

![Схема](Схема.png)

### Примеры запросов в базу данных


#### Пользователи

##### Получить всех пользователей

    SELECT *
    FROM users;

##### Получить пользователя по id

    SELECT *
    FROM users
    WHERE user_id = 1;


#### Фильмы

##### Получить все фильмы

    SELECT *
    FROM films;

##### Получить фильм по id

    SELECT *
    FROM films
    WHERE film_id = 1;
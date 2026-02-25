# Todo App

Простое веб-приложение для управления списком задач, построенное на Spring Boot 4 с современным Liquid Glass UI.

## Технологии

- **Java 25**
- **Spring Boot 4.0.3**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Validation
  - Thymeleaf
- **HSQLDB** (in-memory база данных)
- **Lombok**
- **Maven**

## Требования

- JDK 25+
- Maven 3.9+

## Установка и запуск

```bash
cd todoapp

./mvnw clean package

./mvnw spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

## Функциональность

### Основные возможности

| Функция | Описание |
|---------|----------|
| Добавление задач | Создание задач с валидацией (не пустое, макс. 255 символов) |
| Редактирование | Изменение названия задачи |
| Удаление | Удаление отдельной задачи |
| Отметка выполнения | Переключение статуса completed/active |
| Поиск | Фильтрация задач по названию (без учёта регистра) |
| Фильтры | Просмотр всех/активных/выполненных задач |
| Массовые операции | Удаление всех задач, очистка выполненных |

### API Endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| `GET` | `/` | Главная страница со списком задач |
| `GET` | `/?search=текст` | Поиск задач |
| `GET` | `/?filter=active` | Только активные задачи |
| `GET` | `/?filter=completed` | Только выполненные |
| `POST` | `/add` | Добавить задачу |
| `POST` | `/toggle/{id}` | Переключить статус |
| `POST` | `/edit/{id}` | Редактировать задачу |
| `POST` | `/delete/{id}` | Удалить задачу |
| `POST` | `/removeAll` | Удалить все задачи |
| `POST` | `/clearCompleted` | Удалить выполненные |
| `POST` | `/search` | Перенаправление на GET /?search=... |

## Структура проекта

```
src/
├── main/
│   ├── java/com/rmk/todoapp/
│   │   ├── TodoApplication.java
│   │   ├── controllers/
│   │   │   └── TodoController.java
│   │   ├── model/
│   │   │   ├── TodoItem.java
│   │   │   └── TaskFilter.java
│   │   ├── repositories/
│   │   │   └── TodoItemRepository.java
│   │   └── service/
│   │       └── TodoService.java
│   └── resources/
│       ├── application.properties
│       ├── static/
│       │   └── style.css
│       └── templates/
│           └── index.html
└── test/
    └── java/com/rmk/todoapp/
        ├── model/
        │   ├── TodoItemTest.java
        │   └── TaskFilterTest.java
        ├── repository/
        │   └── TodoItemRepositoryTest.java
        ├── controller/
        │   └── TodoControllerTest.java
        └── service/
            └── TodoServiceTest.java
```

## Модель данных

### TodoItem

| Поле | Тип | Валидация | Описание |
|------|-----|-----------|----------|
| `id` | Long | - | Автоинкрементный ID |
| `title` | String | @NotBlank, @Size(max=255) | Название задачи |
| `completed` | boolean | - | Статус выполнения |
| `createdAt` | LocalDateTime | - | Дата создания |

### TaskFilter (Enum)

| Значение | Описание |
|----------|----------|
| `ALL` | Все задачи |
| `ACTIVE` | Только невыполненные |
| `COMPLETED` | Только выполненные |

## Технические особенности

### Архитектура
- **Service Layer** — бизнес-логика вынесена в `TodoService`
- **Controller** — только обработка HTTP запросов и маршрутизация
- **Repository** — доступ к данным через Spring Data JPA

### Валидация
- `@NotBlank` — название не может быть пустым
- `@Size(max=255)` — ограничение длины названия
- HTML `maxlength="255"` — клиентская валидация

### Оптимизации
- `countByCompleted()` вместо `findByCompleted().size()` — COUNT вместо SELECT
- `@Transactional` на массовых операциях — атомарность

### Код-стайл
- Enum `TaskFilter` вместо magic strings
- CSS вынесен в `static/style.css` — кэширование браузером

## Тестирование

```bash
./mvnw test
```

### Структура тестов

| Класс | Тип | Тестов | Покрытие |
|-------|-----|--------|----------|
| `TodoItemTest` | Unit | 6 | Модель, валидация |
| `TaskFilterTest` | Unit | 6 | Enum, парсинг |
| `TodoItemRepositoryTest` | Integration | 9 | CRUD, COUNT |
| `TodoControllerTest` | WebMvc | 20 | Эндпоинты, валидация |
| `TodoServiceTest` | Unit | 16 | Бизнес-логика |
| `TodoappApplicationTests` | Integration | 1 | Контекст Spring |

**Всего: 58 тестов**

## UI

Интерфейс в стиле **Liquid Glass** (Apple/Telegram):
- Полупрозрачные карточки с `backdrop-filter: blur(40px)`
- Градиентные блики
- Минималистичный дизайн
- Адаптивная вёрстка

## Лицензия

MIT

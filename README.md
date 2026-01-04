# Totalizator System

Веб-приложение для системы тотализатора, позволяющее пользователям делать ставки на спортивные соревнования.

## Технологии

- **Java 8+**
- **Maven** - управление зависимостями и сборка проекта
- **Servlet/JSP** - веб-технологии
- **MySQL** - база данных
- **JDBC** - доступ к базе данных
- **Log4J2** - логирование
- **JSTL** - JavaServer Pages Standard Tag Library для JSP

## Архитектура

Приложение следует принципам:
- **Layered Architecture** (многослойная архитектура)
- **MVC Pattern** (Model-View-Controller)
- **DAO Pattern** - для работы с данными
- **Factory Method** - для создания DAO и Service
- **Singleton** - для конфигурации и пула соединений
- **SOLID** - принципы объектно-ориентированного программирования
- **DRY** - Don't Repeat Yourself
- **KISS** - Keep It Simple, Stupid

## Структура проекта

```
src/
├── main/
│   ├── java/com/totalizator/
│   │   ├── controller/          # Контроллеры (MVC)
│   │   │   ├── admin/           # Административные контроллеры
│   │   │   ├── HomeController.java    # Главная страница
│   │   │   ├── LoginController.java   # Авторизация
│   │   │   ├── RegisterController.java # Регистрация
│   │   │   ├── BetController.java      # Управление ставками
│   │   │   ├── LocaleController.java   # Смена языка
│   │   │   └── LogoutController.java    # Выход
│   │   ├── dao/                 # Data Access Object слой
│   │   │   ├── impl/            # Реализации DAO
│   │   │   ├── Dao.java         # Базовый интерфейс DAO
│   │   │   ├── UserDao.java     # DAO для пользователей
│   │   │   ├── CompetitionDao.java # DAO для соревнований
│   │   │   └── BetDao.java      # DAO для ставок
│   │   ├── filter/              # Фильтры
│   │   │   ├── AuthenticationFilter.java # Проверка аутентификации
│   │   │   ├── EncodingFilter.java       # Кодировка UTF-8
│   │   │   └── XSSFilter.java            # Защита от XSS
│   │   ├── model/               # Модели данных (Entity)
│   │   │   ├── User.java        # Пользователь
│   │   │   ├── Competition.java   # Соревнование
│   │   │   ├── Bet.java         # Ставка
│   │   │   ├── BetType.java     # Тип ставки
│   │   │   └── Role.java        # Роль пользователя
│   │   ├── service/             # Бизнес-логика
│   │   │   ├── impl/            # Реализации сервисов
│   │   │   ├── factory/         # Фабрики
│   │   │   │   ├── ServiceFactory.java # Фабрика сервисов
│   │   │   │   └── DaoFactory.java      # Фабрика DAO
│   │   │   ├── UserService.java        # Сервис пользователей
│   │   │   ├── CompetitionService.java # Сервис соревнований
│   │   │   └── BetService.java         # Сервис ставок
│   │   └── util/                # Утилиты
│   │       ├── ConnectionPool.java     # Пул соединений с БД
│   │       └── ValidationUtils.java    # Утилиты валидации
│   ├── resources/
│   │   ├── database/
│   │   │   └── init.sql         # SQL скрипт инициализации БД
│   │   ├── log4j2.xml           # Конфигурация логирования
│   │   ├── messages_en.properties # Английская локализация
│   │   ├── messages_be.properties # Белорусская локализация
│   │   ├── messages_de.properties # Немецкая локализация
│   │   └── db.properties        # Настройки подключения к БД
│   └── webapp/
│       ├── index.jsp            # Главная страница
│       ├── pages/               # JSP страницы
│       │   ├── login.jsp
│       │   ├── register.jsp
│       │   ├── bets.jsp
│       │   ├── admin/           # Страницы администратора
│       │   └── bookmaker/       # Страницы букмекера
│       └── WEB-INF/
│           └── web.xml         # Конфигурация веб-приложения
└── test/
    └── java/                    # JUnit тесты
```

## Описание компонентов

### Model (Модели данных)

**User** - представляет пользователя системы:
- id, username, email, password
- firstName, lastName
- role (Role) - роль пользователя
- balance (BigDecimal) - баланс счета
- active (boolean) - активен ли пользователь
- createdAt, updatedAt - временные метки

**Competition** - представляет спортивное соревнование:
- id, title, description
- sportType - тип спорта
- startDate, endDate - даты начала и окончания
- status (enum: SCHEDULED, IN_PROGRESS, FINISHED, CANCELLED)
- result - результат соревнования
- team1, team2 - команды
- score1, score2 - счет

**Bet** - представляет ставку пользователя:
- id, amount - сумма ставки
- predictedValue - предсказанное значение
- status (enum: PENDING, WON, LOST, CANCELLED)
- winAmount - выигрышная сумма
- user, competition, betType - связи с другими сущностями

**Role** - роли пользователей:
- ADMIN - администратор
- BOOKMAKER - букмекер
- CLIENT - клиент

### DAO Layer (Слой доступа к данным)

Реализует паттерн Data Access Object для абстракции работы с базой данных.

**Dao<T, K>** - базовый интерфейс с методами CRUD:
- findById(K id) - поиск по ID
- findAll() - получение всех записей
- save(T entity) - сохранение новой записи
- update(T entity) - обновление записи
- deleteById(K id) - удаление по ID

**UserDao** - расширенный интерфейс для пользователей:
- findByUsername(String username)
- findByEmail(String email)

**BookmakerDAO** - расширяет интерфейс букмекера
- updateOddsCompetition(int competitionId, BigDecimal winMultiplier, BigDecimal drawMultiplier, BigDecimal lossMultiplier,
  BigDecimal exactScoreMultiplier, BigDecimal totalOverMultiplier,
  BigDecimal totalUnderMultiplier)

**CompetitionDao** - интерфейс для соревнований:
- findByStatus(String status)

**BetDao** - интерфейс для ставок:
- findByUserId(Integer userId)
- findByCompetitionId(Integer competitionId)

### Service Layer (Слой бизнес-логики)

Содержит бизнес-логику приложения.

**UserService**:
- register(User user) - регистрация нового пользователя
- authenticate(String username, String password) - аутентификация
- updateUser(User user) - обновление данных пользователя
- deleteUser(Integer id) - удаление пользователя
- Пароли хешируются с помощью SHA-256

**CompetitionService**:
- createCompetition(Competition competition) - создание соревнования
- updateCompetition(Competition competition) - обновление соревнования
- generateRandomResult(Integer competitionId) - генерация случайного результата
- findByStatus(String status) - поиск по статусу

**BetService**:
- placeBet(Bet bet) - размещение ставки
- cancelBet(Integer betId) - отмена ставки
- processBetsForCompetition(Integer competitionId) - обработка ставок после завершения соревнования
- Вычисление выигрышей на основе результатов соревнований

### Controller Layer (Слой контроллеров)

Обрабатывает HTTP запросы и формирует ответы.

**BaseController** - базовый класс с общей функциональностью:
- ensureDefaultLocale() - установка локали по умолчанию
- getUserFromSession() - получение пользователя из сессии
- isAuthenticated() - проверка аутентификации
- hasRole() - проверка роли
- requireAuthentication() - требование аутентификации с редиректом
- requireRole() - требование роли с ошибкой 403

**HomeController** (`/`) - главная страница:
- Отображает список всех соревнований
- Доступна всем пользователям (включая незарегистрированных)

**LoginController** (`/login`) - авторизация:
- GET - отображение формы входа
- POST - обработка данных входа

**RegisterController** (`/register`) - регистрация:
- GET - отображение формы регистрации
- POST - обработка данных регистрации

**BetController** (`/bets/*`) - управление ставками:
- GET `/bets/` - список ставок пользователя
- GET `/bets/create/{competitionId}` - форма создания ставки
- POST `/bets/create` - создание ставки
- POST `/bets/cancel/{betId}` - отмена ставки

**AdminController** (`/admin/*`) - административная панель:
- Управление пользователями (создание, редактирование, удаление)
- Управление соревнованиями (создание, редактирование, удаление)
- Генерация результатов соревнований
- Требует роль ADMIN

**BookmakerController** (`/bookmaker/*`) - панель букмекера:
- Просмотр соревнований
- Установка коэффициентов (множителей) для ставок
- Требует роль BOOKMAKER

**LocaleController** (`/locale?lang={lang}`) - смена языка:
- Поддерживаемые языки: en, be, de
- Сохраняет выбор в сессии
- Перенаправляет на предыдущую страницу

### Filter Layer (Слой фильтров)

**AuthenticationFilter** - проверка аутентификации:
- Разрешает доступ к публичным страницам (/, /login, /register, /locale)
- Проверяет аутентификацию для защищенных страниц
- Проверяет роли для страниц /admin и /bookmaker
- Устанавливает локаль по умолчанию

**EncodingFilter** - установка кодировки UTF-8:
- Устанавливает request и response encoding в UTF-8

**XSSFilter** - защита от XSS атак:
- Очищает параметры запроса от потенциально опасных символов
- Использует XSSRequestWrapper для модификации параметров

### Factory Pattern (Паттерн Фабрика)

**ServiceFactory** - фабрика сервисов (Singleton):
- getUserService() - получение UserService
- getCompetitionService() - получение CompetitionService
- getBetService() - получение BetService
- Обеспечивает единый экземпляр каждого сервиса

**DaoFactory** - фабрика DAO (Singleton):
- getUserDao() - создание UserDao
- getCompetitionDao() - создание CompetitionDao
- getBetDao() - создание BetDao
- getBetTypeDao() - создание BetTypeDao
- Использует общий ConnectionPool

### Utility Classes (Утилитные классы)

**ValidationUtils** - утилиты валидации:
- isValidId(Integer id) - проверка валидности ID
- isNullOrEmpty(String str) - проверка на null или пустоту
- areAllNotEmpty(String... strings) - проверка нескольких строк
- ensureDefaultLocale(HttpServletRequest request) - проверка языкового отображения
- getUserFromSession(HttpServletRequest request) - получения объекта пользователя из сессии
- isAuthenticated(HttpServletRequest request) - проверка получения пользователя
- hasRole(HttpServletRequest request, String roleName) - проверка получения роли пользователя
- requireAuthentication(HttpServletRequest request, HttpServletResponse response) - проверка аутентификации
- requireRole(HttpServletRequest request, HttpServletResponse response, String roleName) - проверка роли пользователя
- redirectToLogin(HttpServletRequest request, HttpServletResponse response) - перенаправление на страницу аутентификации

**ConnectionPool** - пул соединений с БД (Singleton):
- Управляет пулом соединений с MySQL
- Инициализируется из db.properties
- Использует паттерн Singleton для единого экземпляра

### Таблицы:

1. **roles** - роли пользователей
   - id, name (ADMIN, BOOKMAKER, CLIENT), description

2. **users** - пользователи системы
   - id, username, email, password (SHA-256 hash)
   - first_name, last_name
   - role_id (FK), balance, is_active
   - created_at, updated_at

3. **competitions** - спортивные соревнования
   - id, title, description, sport_type
   - start_date, end_date
   - status (SCHEDULED, IN_PROGRESS, FINISHED, CANCELLED)
   - result, team1, team2, score1, score2
   - created_at, updated_at

4. **bet_types** - типы ставок
   - id, name (WIN, DRAW, LOSS, EXACT_SCORE, TOTAL_OVER, TOTAL_UNDER)
   - description, multiplier

5. **bets** - ставки пользователей
   - id, user_id (FK), competition_id (FK), bet_type_id (FK)
   - amount, predicted_value
   - status (PENDING, WON, LOST, CANCELLED)
   - win_amount, created_at, updated_at

6. **competition_bet_types** - коэффициенты букмекера
   - id, competition_id (FK), bet_type_id (FK)
   - multiplier - коэффициент для конкретного соревнования и типа ставки
   - created_at, updated_at

Для инициализации БД выполните скрипт: `src/main/resources/database/init.sql`

## Функциональность

### Для всех пользователей:
- Регистрация (sign up)
- Авторизация (sign in)
- Выход (sign out)
- Просмотр соревнований на главной странице
- Смена языка интерфейса (EN, BE, DE)

### Для клиентов (CLIENT):
- Просмотр доступных соревнований
- Размещение ставок на соревнования со статусом SCHEDULED
- Просмотр своих ставок
- Отмена ставок (до начала соревнования)

### Для администратора (ADMIN):
- Управление пользователями (создание, редактирование, удаление)
- Создание и редактирование соревнований
- Генерация результатов соревнований (случайным образом)
- Обработка ставок после завершения соревнований
- Просмотр всех ставок

### Для букмекера (BOOKMAKER):
- Просмотр всех соревнований
- Установка коэффициентов (множителей) для типов ставок на конкретные соревнования
- Коэффициенты переопределяют стандартные множители из bet_types

## Безопасность

- **SQL Injection** - защита через PreparedStatement во всех DAO
- **XSS** - фильтр XSSFilter для очистки входных данных
- **Аутентификация** - фильтр AuthenticationFilter проверяет вход пользователей
- **Авторизация** - проверка ролей для доступа к административным функциям
- **Валидация** - на сервере через ValidationUtils и в сервисах
- **Пароли** - хеширование SHA-256 (не хранятся в открытом виде)

## Интернационализация

Поддерживаемые языки:
- **EN** (English) - язык по умолчанию
- **BE** (Беларуская)
- **DE** (Deutsch)

Локализация реализована через:
- JSTL fmt:message теги в JSP
- Файлы messages_*.properties в resources
- LocaleController для смены языка
- Сохранение выбора в сессии

 `http://localhost:8080/my_web_project/`

## Учетные данные по умолчанию

После инициализации БД доступен администратор:
- Username: `admin`
- Password: `admin123`

## Тестирование

Запуск тестов:
```bash
mvn test
```

## Логирование

Логи сохраняются в:
- `logs/totalizator.log` - основной лог файл
- `logs/totalizator-rolling.log` - ротируемый лог файл

Уровни логирования настраиваются в `src/main/resources/log4j2.xml`

## Зависимости (Maven)

Основные зависимости:
- `jakarta.servlet-api` - Servlet API
- `jakarta.servlet.jsp.jstl-api` - JSTL API
- `mysql-connector-j` - MySQL драйвер
- `log4j-core`, `log4j-api` - логирование
- `commons-lang3` - утилиты для работы со строками
- `jackson-databind` - для JSON (REST API)

## Авторы

Totalizator Team

## Лицензия

Этот проект создан в учебных целях.

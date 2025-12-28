-- Database: test12
-- Character Set: utf8mb4 for full UTF-8 support including Cyrillic

CREATE DATABASE IF NOT EXISTS test12 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE test12;

-- Table: roles
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role_id INT NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: competitions
CREATE TABLE IF NOT EXISTS competitions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    sport_type VARCHAR(100) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    result VARCHAR(50),
    team1 VARCHAR(255) NOT NULL,
    team2 VARCHAR(255) NOT NULL,
    score1 INT DEFAULT NULL,
    score2 INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: bet_types
CREATE TABLE IF NOT EXISTS bet_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    multiplier DECIMAL(5, 2) NOT NULL DEFAULT 1.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: bets
CREATE TABLE IF NOT EXISTS bets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    competition_id INT NOT NULL,
    bet_type_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    predicted_value VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    win_amount DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (competition_id) REFERENCES competitions(id) ON DELETE RESTRICT,
    FOREIGN KEY (bet_type_id) REFERENCES bet_types(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert initial data
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Администратор системы'),
('BOOKMAKER', 'Букмекер'),
('CLIENT', 'Клиент');

INSERT INTO bet_types (name, description, multiplier) VALUES
('WIN', 'Победа команды', 2.00),
('DRAW', 'Ничья', 3.00),
('LOSS', 'Поражение команды', 2.00),
('EXACT_SCORE', 'Точный счет', 5.00),
('TOTAL_OVER', 'Тотал больше', 1.80),
('TOTAL_UNDER', 'Тотал меньше', 1.80);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, role_id, balance) VALUES
('admin', 'admin@totalizator.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pLU', 'Администратор', 'Системы', 1, 10000.00);

-- Insert sample competitions
INSERT INTO competitions (title, description, sport_type, start_date, status, team1, team2) VALUES
('Футбол: Динамо Минск - БАТЭ', 'Чемпионат Беларуси по футболу', 'ФУТБОЛ', DATE_ADD(NOW(), INTERVAL 1 DAY), 'SCHEDULED', 'Динамо Минск', 'БАТЭ'),
('Хоккей: Динамо-Минск - Неман', 'Чемпионат Беларуси по хоккею', 'ХОККЕЙ', DATE_ADD(NOW(), INTERVAL 2 DAY), 'SCHEDULED', 'Динамо-Минск', 'Неман'),
('Баскетбол: Цмоки-Минск - Гродно-93', 'Чемпионат Беларуси по баскетболу', 'БАСКЕТБОЛ', DATE_ADD(NOW(), INTERVAL 3 DAY), 'SCHEDULED', 'Цмоки-Минск', 'Гродно-93');


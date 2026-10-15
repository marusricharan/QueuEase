-- QueueEase Database Schema (MySQL 8.0)
-- Creates database structures for users, businesses, services, queues, bookings, status histories, and reviews.

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS businesses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255),
    phone VARCHAR(50),
    opening_hours VARCHAR(255) DEFAULT '09:00 - 18:00',
    open_time TIME DEFAULT '09:00:00',
    close_time TIME DEFAULT '18:00:00',
    avg_service_duration_minutes INT DEFAULT 25,
    daily_capacity INT DEFAULT 50,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    queue_open BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id BIGINT,
    CONSTRAINT fk_businesses_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    duration_minutes INT NOT NULL DEFAULT 30,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_services_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS queue_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT NOT NULL,
    customer_id BIGINT,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    service_id BIGINT NOT NULL,
    ticket_number VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'WAITING',
    counter_number INT,
    joined_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    called_time DATETIME,
    served_time DATETIME,
    completed_time DATETIME,
    estimated_wait_minutes INT DEFAULT 0,
    notes VARCHAR(500),
    CONSTRAINT fk_queue_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_queue_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_queue_service FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_service FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS status_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    queue_entry_id BIGINT,
    booking_id BIGINT,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(255),
    notes VARCHAR(500),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

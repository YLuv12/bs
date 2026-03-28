CREATE DATABASE IF NOT EXISTS bs_tourism CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bs_tourism;

CREATE TABLE IF NOT EXISTS user_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    login_type VARCHAR(20) DEFAULT 'local'
);

CREATE TABLE IF NOT EXISTS scenic_spot (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_zh VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    description_zh TEXT,
    description_en TEXT,
    images VARCHAR(500),
    total_score INT DEFAULT 0,
    score_count INT DEFAULT 0,
    avg_score DECIMAL(2, 1) DEFAULT 0,
    visit_count INT DEFAULT 0,
    collect_count INT DEFAULT 0,
    region VARCHAR(50),
    type VARCHAR(50),
    tags VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS food (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_zh VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    description_zh TEXT,
    description_en TEXT,
    avg_price DECIMAL(10, 2) DEFAULT 0,
    images VARCHAR(500),
    total_score INT DEFAULT 0,
    score_count INT DEFAULT 0,
    avg_score DECIMAL(2, 1) DEFAULT 0,
    visit_count INT DEFAULT 0,
    collect_count INT DEFAULT 0,
    region VARCHAR(50),
    category VARCHAR(50),
    tags VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS collect (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    content TEXT NOT NULL,
    score INT,
    like_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS footprint (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS play_plan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS play_plan_detail (
    id INT PRIMARY KEY AUTO_INCREMENT,
    plan_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    order_num INT DEFAULT 0,
    remark VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS score (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    score INT NOT NULL
);

INSERT INTO user_info (username, password, nickname) VALUES
('admin', '123456', 'Admin'),
('user1', '123456', 'User1');

INSERT INTO scenic_spot (name_zh, name_en, description_zh, description_en, images, total_score, score_count, avg_score, visit_count, collect_count, region, type, tags) VALUES
('Baise Uprising Memorial', 'Baise Uprising Memorial', 'Baise Uprising Memorial', 'The Baise Uprising Memorial', '/images/banner/banner1.jpg,/images/banner/banner2.jpg', 480, 100, 4.8, 5200, 256, 'Youjiang', 'red', 'red tourism,4A,patriotic education base'),
('Jingxi Tongling Grand Canyon', 'Jingxi Tongling Grand Canyon', 'Jingxi Tongling Grand Canyon', 'Jingxi Tongling Grand Canyon', '/images/banner/banner2.jpg,/images/banner/banner1.jpg', 470, 100, 4.7, 4500, 200, 'Jingxi', 'nature', 'natural scenery,canyon,4A'),
('Leye Dashiwei Tiankeng', 'Leye Dashiwei Tiankeng', 'Leye Dashiwei Tiankeng', 'Leye Dashiwei Tiankeng', '/images/banner/banner1.jpg,/images/banner/banner2.jpg', 460, 100, 4.6, 3800, 180, 'Leye', 'nature', 'natural scenery,sinkhole,world geopark');

INSERT INTO food (name_zh, name_en, description_zh, description_en, avg_price, images, total_score, score_count, avg_score, visit_count, collect_count, region, category, tags) VALUES
('Baise Roast Duck Rice Noodle', 'Baise Roast Duck Rice Noodle', 'Baise Roast Duck Rice Noodle', 'Baise Roast Duck Rice Noodle is a local specialty, featuring premium duck meat roasted with secret sauce, served with smooth rice noodles.', 15, '/images/banner/banner2.jpg', 450, 100, 4.5, 3200, 128, 'Youjiang', 'snack', 'local specialty,recommended'),
('Jingxi Rice Roll', 'Jingxi Rice Roll', 'Jingxi Rice Roll', 'Jingxi Rice Roll is a traditional food in Jingxi area, made from high-quality rice, with thin and transparent skin wrapping various fillings, and a delicate taste.', 8, '/images/banner/banner1.jpg', 430, 100, 4.3, 2500, 100, 'Jingxi', 'snack', 'local specialty,traditional food'),
('Tiandong Mango', 'Tiandong Mango', 'Tiandong Mango', 'Tiandong Mango is a specialty of Tiandong County, Guangxi, famous for its large size, thick flesh, small core, and sweet taste. It is a national geographical indication protected product.', 25, '/images/banner/banner2.jpg', 490, 100, 4.9, 5000, 300, 'Tiandong', 'local', 'local specialty,fruit,national geographical indication product');

INSERT INTO collect (user_id, type, target_id) VALUES
(2, 'attraction', 1),
(2, 'food', 1);

INSERT INTO comment (user_id, type, target_id, content, score, like_count) VALUES
(2, 'attraction', 1, 'Very meaningful red education base, worth bringing children to visit and learn', 5, 23),
(2, 'food', 1, 'The taste is very authentic, the roast duck skin is crispy and the meat is tender', 5, 18);

INSERT INTO footprint (user_id, type, target_id) VALUES
(2, 'attraction', 1),
(2, 'food', 1),
(2, 'attraction', 2);

INSERT INTO score (user_id, type, target_id, score) VALUES
(2, 'attraction', 1, 5),
(2, 'food', 1, 5);

INSERT INTO play_plan (user_id, name, description) VALUES
(2, 'Baise One Day Tour', 'Baise city classic attractions one day tour, including Baise Uprising Memorial, Baise Old Street, etc.'),
(2, 'Jingxi Two Day Tour', 'Jingxi Tongling Grand Canyon, Old Town two day tour');

INSERT INTO play_plan_detail (plan_id, type, target_id, order_num, remark) VALUES
(1, 'attraction', 1, 1, 'Visit in the morning, recommended 2 hours'),
(1, 'food', 1, 2, 'Lunch to taste Baise specialty roast duck rice noodle'),
(2, 'attraction', 2, 1, 'First day morning visit Tongling Grand Canyon'),
(2, 'food', 2, 2, 'First day lunch to taste Jingxi rice roll'),
(2, 'attraction', 3, 3, 'Second day morning visit Leye Tiankeng');

CREATE DATABASE IF NOT EXISTS bs_tourism CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bs_tourism;

CREATE TABLE IF NOT EXISTS user_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    avatar VARCHAR(255),
    phone VARCHAR(20),
    gender VARCHAR(10),
    age INT,
    intro VARCHAR(255),
    openid VARCHAR(100),
    login_type VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS scenic_spot (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_zh VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    description_zh TEXT,
    description_en TEXT,
    images VARCHAR(500),
    open_time VARCHAR(100),
    ticket_info VARCHAR(100),
    phone VARCHAR(20),
    total_score INT DEFAULT 0,
    score_count INT DEFAULT 0,
    avg_score DECIMAL(2, 1) DEFAULT 0,
    visit_count INT DEFAULT 0,
    collect_count INT DEFAULT 0,
    region VARCHAR(50),
    type VARCHAR(50),
    tags VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS food (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_zh VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    description_zh TEXT,
    description_en TEXT,
    avg_price DECIMAL(10, 2) DEFAULT 0,
    business_hours VARCHAR(100),
    phone VARCHAR(20),
    images VARCHAR(500),
    total_score INT DEFAULT 0,
    score_count INT DEFAULT 0,
    avg_score DECIMAL(2, 1) DEFAULT 0,
    visit_count INT DEFAULT 0,
    collect_count INT DEFAULT 0,
    region VARCHAR(50),
    category VARCHAR(50),
    tags VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS collect (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    content TEXT NOT NULL,
    score INT,
    like_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS footprint (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS play_plan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS play_plan_detail (
    id INT PRIMARY KEY AUTO_INCREMENT,
    plan_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    order_num INT DEFAULT 0,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS score (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    score INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO user_info (username, password, nickname, login_type) VALUES
('admin', '123456', 'Admin', 'account'),
('user1', '123456', 'User1', 'account');

INSERT INTO scenic_spot (name_zh, name_en, description_zh, description_en, images, open_time, ticket_info, phone, total_score, score_count, avg_score, visit_count, collect_count, region, type, tags) VALUES
('百色起义纪念馆', 'Baise Uprising Memorial', '百色起义纪念馆位于广西百色市，是为了纪念1929年12月11日邓小平、张云逸领导的百色起义而建立的。', 'The Baise Uprising Memorial is located in Baise City, Guangxi. It was established to commemorate the Baise Uprising led by Deng Xiaoping and Zhang Yunyi on December 11, 1929.', '/images/banner/banner1.jpg,/images/banner/banner2.jpg', '09:00-17:00', '免费', '0776-2824101', 480, 100, 4.8, 5200, 256, '右江区', 'red', '红色旅游,4A景区,爱国主义教育基地'),
('靖西通灵大峡谷', 'Jingxi Tongling Grand Canyon', '靖西通灵大峡谷位于广西靖西市湖润镇，是一个集峡谷、瀑布、溶洞、原始森林为一体的自然景区。', 'Jingxi Tongling Grand Canyon is located in Huren Town, Jingxi City, Guangxi. It is a natural scenic area integrating canyons, waterfalls, caves, and primeval forests.', '/images/banner/banner2.jpg,/images/banner/banner1.jpg', '08:00-18:00', '¥115', '0776-6221188', 470, 100, 4.7, 4500, 200, '靖西市', 'nature', '自然风光,峡谷,4A景区'),
('乐业大石围天坑', 'Leye Dashiwei Tiankeng', '乐业大石围天坑位于广西乐业县，是世界上最大的天坑群之一，被誉为"天坑博物馆"。', 'Leye Dashiwei Tiankeng is located in Leye County, Guangxi. It is one of the largest sinkhole groups in the world, known as the "Tiankeng Museum".', '/images/banner/banner1.jpg,/images/banner/banner2.jpg', '08:30-17:30', '¥90', '0776-7921168', 460, 100, 4.6, 3800, 180, '乐业县', 'nature', '自然风光,天坑,世界地质公园');

INSERT INTO food (name_zh, name_en, description_zh, description_en, avg_price, business_hours, phone, images, total_score, score_count, avg_score, visit_count, collect_count, region, category, tags) VALUES
('百色烧鸭粉', 'Baise Roast Duck Rice Noodle', '百色烧鸭粉是当地特色美食，选用优质鸭肉用秘制酱料烤制，搭配爽滑的米粉，味道独特。', 'Baise Roast Duck Rice Noodle is a local specialty, featuring premium duck meat roasted with secret sauce, served with smooth rice noodles.', 15, '06:00-21:00', '13800138001', '/images/banner/banner2.jpg', 450, 100, 4.5, 3200, 128, '右江区', 'snack', '地方特色,推荐'),
('靖西卷粉', 'Jingxi Rice Roll', '靖西卷粉是靖西地区的传统美食，用优质大米制作，薄而透明的皮包裹各种馅料，口感细腻。', 'Jingxi Rice Roll is a traditional food in Jingxi area, made from high-quality rice, with thin and transparent skin wrapping various fillings, and a delicate taste.', 8, '07:00-20:00', '13800138002', '/images/banner/banner1.jpg', 430, 100, 4.3, 2500, 100, '靖西市', 'snack', '地方特色,传统美食'),
('田东芒果', 'Tiandong Mango', '田东芒果是广西田东县的特产，以个大、肉厚、核小、味甜而闻名，是国家地理标志保护产品。', 'Tiandong Mango is a specialty of Tiandong County, Guangxi, famous for its large size, thick flesh, small core, and sweet taste. It is a national geographical indication protected product.', 25, '08:00-19:00', '13800138003', '/images/banner/banner2.jpg', 490, 100, 4.9, 5000, 300, '田东县', 'local', '地方特产,水果,国家地理标志产品');

INSERT INTO collect (user_id, type, target_id) VALUES
(2, 'attraction', 1),
(2, 'food', 1);

INSERT INTO comment (user_id, type, target_id, content, score, like_count) VALUES
(2, 'attraction', 1, '非常有意义的红色教育基地，值得带孩子来参观学习', 5, 23),
(2, 'food', 1, '味道非常正宗，烧鸭皮脆肉嫩', 5, 18);

INSERT INTO footprint (user_id, type, target_id) VALUES
(2, 'attraction', 1),
(2, 'food', 1),
(2, 'attraction', 2);

INSERT INTO score (user_id, type, target_id, score) VALUES
(2, 'attraction', 1, 5),
(2, 'food', 1, 5);

INSERT INTO play_plan (user_id, name, description) VALUES
(2, '百色一日游', '百色市区经典景点一日游，包括百色起义纪念馆、百色老街等。'),
(2, '靖西二日游', '靖西通灵大峡谷、旧州古镇二日游');

INSERT INTO play_plan_detail (plan_id, type, target_id, order_num, remark) VALUES
(1, 'attraction', 1, 1, '上午参观，推荐2小时'),
(1, 'food', 1, 2, '午餐品尝百色特色烧鸭粉'),
(2, 'attraction', 2, 1, '第一天上午参观通灵大峡谷'),
(2, 'food', 2, 2, '第一天午餐品尝靖西卷粉'),
(2, 'attraction', 3, 3, '第二天上午参观乐业天坑');

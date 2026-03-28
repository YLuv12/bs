-- 创建用户信息表
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
    login_type VARCHAR(20) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建景点信息表
CREATE TABLE IF NOT EXISTS scenic_spot (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_zh VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    description_zh TEXT,
    description_en TEXT,
    guide_zh TEXT,
    guide_en TEXT,
    images VARCHAR(500),
    open_time VARCHAR(100),
    ticket_info VARCHAR(100),
    phone VARCHAR(20),
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6),
    audio_intro VARCHAR(255),
    visit_duration VARCHAR(50),
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

-- 创建美食信息表
CREATE TABLE IF NOT EXISTS food (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_zh VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    description_zh TEXT,
    description_en TEXT,
    shop_name_zh VARCHAR(100),
    shop_name_en VARCHAR(100),
    signature_dishes VARCHAR(200),
    avg_price DECIMAL(10, 2) DEFAULT 0,
    business_hours VARCHAR(100),
    phone VARCHAR(20),
    images VARCHAR(500),
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6),
    parking_info VARCHAR(200),
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

-- 创建收藏表
CREATE TABLE IF NOT EXISTS collect (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建评论表
CREATE TABLE IF NOT EXISTS comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    content TEXT NOT NULL,
    score INT,
    images VARCHAR(500),
    tags VARCHAR(200),
    like_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建评论回复表
CREATE TABLE IF NOT EXISTS comment_reply (
    id INT PRIMARY KEY AUTO_INCREMENT,
    comment_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建意见反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    images VARCHAR(500),
    contact VARCHAR(100),
    status VARCHAR(20) DEFAULT 'pending',
    handle_result TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建足迹表
CREATE TABLE IF NOT EXISTS footprint (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户互动表
CREATE TABLE IF NOT EXISTS interact (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    action VARCHAR(20) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建资讯表
CREATE TABLE IF NOT EXISTS news (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title_zh VARCHAR(200) NOT NULL,
    title_en VARCHAR(200) NOT NULL,
    content_zh TEXT,
    content_en TEXT,
    cover_image VARCHAR(255),
    author VARCHAR(50),
    view_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建游玩计划表
CREATE TABLE IF NOT EXISTS play_plan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建游玩计划详情表
CREATE TABLE IF NOT EXISTS play_plan_detail (
    id INT PRIMARY KEY AUTO_INCREMENT,
    plan_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    order_num INT DEFAULT 0,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建评分表
CREATE TABLE IF NOT EXISTS score (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    score INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入用户数据
INSERT INTO user_info (username, password, nickname, avatar, phone, gender, age, intro, openid, login_type) VALUES
('admin', '123456', 'Admin', '/images/default-avatar.png', '13800138000', 'male', 25, 'System Administrator', '', 'account'),
('user1', '123456', 'User1', '/images/default-avatar.png', '13800138001', 'male', 20, 'Like traveling', '', 'account');

-- 插入景点数据
INSERT INTO scenic_spot (name_zh, name_en, description_zh, description_en, guide_zh, guide_en, images, open_time, ticket_info, phone, latitude, longitude, audio_intro, visit_duration, total_score, score_count, avg_score, visit_count, collect_count, region, type, tags) VALUES
('Baise Uprising Memorial', 'Baise Uprising Memorial', 'Baise Uprising Memorial is located in Baise City, Guangxi.', 'The Baise Uprising Memorial is located in Baise City, Guangxi. It was established to commemorate the Baise Uprising led by Deng Xiaoping and Zhang Yunyi on December 11, 1929.', 'Recommended visit time: 2-3 hours. Closed on Mondays.', 'Recommended visit time: 2-3 hours. Closed on Mondays. ID card required for entry.', '/images/banner/banner1.jpg,/images/banner/banner2.jpg', '09:00-17:00', 'Free', '0776-2824101', 23.9067, 106.6185, '', '2-3 hours', 480, 100, 4.8, 5200, 256, 'Youjiang', 'red', 'Red Tourism,4A Scenic Spot,Patriotic Education Base'),
('Jingxi Tongling Grand Canyon', 'Jingxi Tongling Grand Canyon', 'Jingxi Tongling Grand Canyon is located in Huren Town, Jingxi City, Guangxi.', 'Jingxi Tongling Grand Canyon is located in Huren Town, Jingxi City, Guangxi. It is a natural scenic area integrating canyons, waterfalls, caves, and primeval forests.', 'Recommended visit time: 3-4 hours. Ticket price: ¥115.', 'Recommended visit time: 3-4 hours. Ticket price: ¥115. Comfortable shoes recommended.', '/images/banner/banner2.jpg,/images/banner/banner1.jpg', '08:00-18:00', '¥115', '0776-6221188', 22.8187, 106.6314, '', '3-4 hours', 470, 100, 4.7, 4500, 200, 'Jingxi', 'nature', 'Natural Scenery,Canyon,4A Scenic Spot'),
('Leye Dashiwei Tiankeng', 'Leye Dashiwei Tiankeng', 'Leye Dashiwei Tiankeng is located in Leye County, Guangxi.', 'Leye Dashiwei Tiankeng is located in Leye County, Guangxi. It is one of the largest sinkhole groups in the world, known as the "Tiankeng Museum".', 'Recommended visit time: 4-5 hours. Ticket price: ¥90.', 'Recommended visit time: 4-5 hours. Ticket price: ¥90. Sun protection recommended.', '/images/banner/banner1.jpg,/images/banner/banner2.jpg', '08:30-17:30', '¥90', '0776-7921168', 24.7234, 106.4987, '', '4-5 hours', 460, 100, 4.6, 3800, 180, 'Leye', 'nature', 'Natural Scenery,Tiankeng,World Geopark');

-- 插入美食数据
INSERT INTO food (name_zh, name_en, description_zh, description_en, shop_name_zh, shop_name_en, signature_dishes, avg_price, business_hours, phone, images, latitude, longitude, parking_info, total_score, score_count, avg_score, visit_count, collect_count, region, category, tags) VALUES
('Baise Roast Duck Rice Noodle', 'Baise Roast Duck Rice Noodle', 'Baise Roast Duck Rice Noodle is a local specialty.', 'Baise Roast Duck Rice Noodle is a local specialty, featuring premium duck meat roasted with secret sauce, served with smooth rice noodles.', 'Lao Zhou Ji', 'Lao Zhou Ji Roast Duck Noodle', 'Roast Duck Noodle,Barbecued Pork Noodle,Beef Brisket Noodle', 15, '06:00-21:00', '13800138001', '/images/banner/banner2.jpg', 23.9067, 106.6185, 'Parking available', 450, 100, 4.5, 3200, 128, 'Youjiang', 'snack', 'Local Specialty,Recommended'),
('Jingxi Rice Roll', 'Jingxi Rice Roll', 'Jingxi Rice Roll is a traditional food in Jingxi area.', 'Jingxi Rice Roll is a traditional food in Jingxi area, made from high-quality rice, with thin and transparent skin wrapping various fillings, and a delicate taste.', 'Jingxi Old Taste', 'Jingxi Old Taste Rice Roll', 'Lean Meat Rice Roll,Barbecued Pork Rice Roll,Vegetable Rice Roll', 8, '07:00-20:00', '13800138002', '/images/banner/banner1.jpg', 22.8187, 106.6314, 'Parking nearby', 430, 100, 4.3, 2500, 100, 'Jingxi', 'snack', 'Local Specialty,Traditional Food'),
('Tiandong Mango', 'Tiandong Mango', 'Tiandong Mango is a specialty of Tiandong County, Guangxi.', 'Tiandong Mango is a specialty of Tiandong County, Guangxi, famous for its large size, thick flesh, small core, and sweet taste. It is a national geographical indication protected product.', 'Tiandong Mango Orchard', 'Tiandong Mango Orchard', 'Guiqi Mango,Tainong Mango,Jinhuang Mango', 25, '08:00-19:00', '13800138003', '/images/banner/banner2.jpg', 23.6667, 107.1167, 'Parking available', 490, 100, 4.9, 5000, 300, 'Tiandong', 'local', 'Local Specialty,Fruit,National Geographical Indication Product');

-- 插入资讯数据
INSERT INTO news (title_zh, title_en, content_zh, content_en, cover_image, author, view_count) VALUES
('Baise Red Tourism Routes', 'Baise Red Tourism Routes', 'Baise is a city with a glorious revolutionary history and rich red tourism resources.', 'Baise is a city with a glorious revolutionary history and rich red tourism resources. This article recommends several classic red tourism routes to help you experience Baise\'s red culture.', '/images/banner/banner1.jpg', 'Travel Editor', 1200),
('Leye Tiankeng Adventure Guide', 'Leye Tiankeng Adventure Guide', 'Leye Tiankeng Group is one of the largest sinkhole groups in the world.', 'Leye Tiankeng Group is one of the largest sinkhole groups in the world. This article provides detailed adventure guides, including transportation, accommodation, tickets, and tour routes.', '/images/banner/banner2.jpg', 'Outdoor Expert', 890);

-- 插入游玩计划数据
INSERT INTO play_plan (user_id, name, description) VALUES
(2, 'Baise One Day Tour', 'Baise city classic attractions one day tour, including Baise Uprising Memorial, Baise Old Street, etc.'),
(2, 'Jingxi Two Day Tour', 'Jingxi Tongling Grand Canyon, Jiuzhou Ancient Town two day tour');

-- 插入游玩计划详情数据
INSERT INTO play_plan_detail (plan_id, type, target_id, order_num, remark) VALUES
(1, 'attraction', 1, 1, 'Morning visit, recommended 2 hours'),
(1, 'food', 1, 2, 'Lunch品尝百色特色烧鸭粉'),
(2, 'attraction', 2, 1, 'First day morning visit Tongling Grand Canyon'),
(2, 'food', 2, 2, 'First day lunch品尝靖西卷粉'),
(2, 'attraction', 3, 3, 'Second day morning visit Leye Tiankeng');

-- 插入评论数据
INSERT INTO comment (user_id, type, target_id, content, score, images, tags, like_count) VALUES
(2, 'attraction', 1, 'Very meaningful red education base, worth bringing children to visit and learn', 5, '', 'Red Tourism,Education', 23),
(2, 'food', 1, 'The taste is very authentic, the roast duck skin is crispy and the meat is tender', 5, '', 'Food,Recommended', 18);

-- 插入收藏数据
INSERT INTO collect (user_id, type, target_id) VALUES
(2, 'attraction', 1),
(2, 'food', 1);

-- 插入足迹数据
INSERT INTO footprint (user_id, type, target_id) VALUES
(2, 'attraction', 1),
(2, 'food', 1),
(2, 'attraction', 2);

-- 插入评分数据
INSERT INTO score (user_id, type, target_id, score) VALUES
(2, 'attraction', 1, 5),
(2, 'food', 1, 5);

-- 插入互动数据
INSERT INTO interact (user_id, type, target_id, action) VALUES
(2, 'attraction', 1, 'like'),
(2, 'attraction', 1, 'punch'),
(2, 'food', 1, 'like'),
(2, 'food', 1, 'grass');

-- 插入意见反馈数据
INSERT INTO feedback (user_id, type, content, images, contact, status, handle_result) VALUES
(2, 'suggestion', 'Suggest adding more attraction introductions and travel guides', '', '13800138001', 'pending', '');

-- 插入评论回复数据
INSERT INTO comment_reply (comment_id, user_id, content) VALUES
(1, 1, 'Thank you for your evaluation, we will continue to strive to provide better services!');

-- 查看所有表结构
SHOW TABLES;

-- 查看景点表数据
SELECT * FROM scenic_spot;

-- 查看美食表数据
SELECT * FROM food;

-- 查看用户表数据
SELECT * FROM user_info;
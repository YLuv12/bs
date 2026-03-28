USE bs_tourism;

UPDATE scenic_spot SET 
    name_cn = '百色起义纪念馆',
    name_en = 'Baise Uprising Memorial',
    desc_cn = '百色起义纪念馆位于广西百色市，是为了纪念1929年12月11日邓小平、张云逸领导的百色起义而建立的。',
    desc_en = 'The Baise Uprising Memorial is located in Baise City, Guangxi. It was established to commemorate the Baise Uprising led by Deng Xiaoping and Zhang Yunyi on December 11, 1929.',
    strategy_cn = '推荐游览时间：2-3小时。周一闭馆。',
    strategy_en = 'Recommended visit time: 2-3 hours. Closed on Mondays. ID card required for entry.',
    cover_img = '/images/banner/banner1.jpg',
    imgs = '/images/banner/banner1.jpg,/images/banner/banner2.jpg',
    open_time = '09:00-17:00',
    ticket_info = '免费',
    total_score = 480,
    score_count = 100,
    avg_score = 4.8,
    visit_count = 5200,
    collect_count = 256
WHERE id = 1;

UPDATE scenic_spot SET 
    name_cn = '靖西通灵大峡谷',
    name_en = 'Jingxi Tongling Grand Canyon',
    desc_cn = '靖西通灵大峡谷位于广西靖西市湖润镇，是一个集峡谷、瀑布、溶洞、原始森林为一体的自然景区。',
    desc_en = 'Jingxi Tongling Grand Canyon is located in Huren Town, Jingxi City, Guangxi. It is a natural scenic area integrating canyons, waterfalls, caves, and primeval forests.',
    strategy_cn = '推荐游览时间：3-4小时。门票价格：¥115。',
    strategy_en = 'Recommended visit time: 3-4 hours. Ticket price: ¥115. Comfortable shoes recommended.',
    cover_img = '/images/banner/banner2.jpg',
    imgs = '/images/banner/banner2.jpg,/images/banner/banner1.jpg',
    open_time = '08:00-18:00',
    ticket_info = '¥115',
    total_score = 470,
    score_count = 100,
    avg_score = 4.7,
    visit_count = 4500,
    collect_count = 200
WHERE id = 2;

UPDATE scenic_spot SET 
    name_cn = '乐业大石围天坑',
    name_en = 'Leye Dashiwei Tiankeng',
    desc_cn = '乐业大石围天坑位于广西乐业县，是世界上最大的天坑群之一，被誉为"天坑博物馆"。',
    desc_en = 'Leye Dashiwei Tiankeng is located in Leye County, Guangxi. It is one of the largest sinkhole groups in the world, known as the "Tiankeng Museum".',
    strategy_cn = '推荐游览时间：4-5小时。门票价格：¥90。',
    strategy_en = 'Recommended visit time: 4-5 hours. Ticket price: ¥90. Sun protection recommended.',
    cover_img = '/images/banner/banner1.jpg',
    imgs = '/images/banner/banner1.jpg,/images/banner/banner2.jpg',
    open_time = '08:30-17:30',
    ticket_info = '¥90',
    total_score = 460,
    score_count = 100,
    avg_score = 4.6,
    visit_count = 3800,
    collect_count = 180
WHERE id = 3;

UPDATE food SET 
    name_cn = '百色烧鸭粉',
    name_en = 'Baise Roast Duck Rice Noodle',
    desc_cn = '百色烧鸭粉是当地特色美食，选用优质鸭肉用秘制酱料烤制，搭配爽滑的米粉，味道独特。',
    desc_en = 'Baise Roast Duck Rice Noodle is a local specialty, featuring premium duck meat roasted with secret sauce, served with smooth rice noodles.',
    merchant_name = '老周记',
    signature_dish = '烧鸭粉,叉烧粉,牛腩粉',
    per_capita = 15,
    business_time = '06:00-21:00',
    cover_img = '/images/banner/banner2.jpg',
    imgs = '/images/banner/banner2.jpg',
    total_score = 450,
    score_count = 100,
    avg_score = 4.5,
    visit_count = 3200,
    collect_count = 128
WHERE id = 1;

UPDATE food SET 
    name_cn = '靖西卷粉',
    name_en = 'Jingxi Rice Roll',
    desc_cn = '靖西卷粉是靖西地区的传统美食，用优质大米制作，薄而透明的皮包裹各种馅料，口感细腻。',
    desc_en = 'Jingxi Rice Roll is a traditional food in Jingxi area, made from high-quality rice, with thin and transparent skin wrapping various fillings, and a delicate taste.',
    merchant_name = '靖西老味',
    signature_dish = '瘦肉卷粉,叉烧卷粉,素菜卷粉',
    per_capita = 8,
    business_time = '07:00-20:00',
    cover_img = '/images/banner/banner1.jpg',
    imgs = '/images/banner/banner1.jpg',
    total_score = 430,
    score_count = 100,
    avg_score = 4.3,
    visit_count = 2500,
    collect_count = 100
WHERE id = 2;

UPDATE food SET 
    name_cn = '田东芒果',
    name_en = 'Tiandong Mango',
    desc_cn = '田东芒果是广西田东县的特产，以个大、肉厚、核小、味甜而闻名，是国家地理标志保护产品。',
    desc_en = 'Tiandong Mango is a specialty of Tiandong County, Guangxi, famous for its large size, thick flesh, small core, and sweet taste. It is a national geographical indication protected product.',
    merchant_name = '田东芒果园',
    signature_dish = '桂七芒果,台农芒果,金黄芒果',
    per_capita = 25,
    business_time = '08:00-19:00',
    cover_img = '/images/banner/banner2.jpg',
    imgs = '/images/banner/banner2.jpg',
    total_score = 490,
    score_count = 100,
    avg_score = 4.9,
    visit_count = 5000,
    collect_count = 300
WHERE id = 3;

SELECT * FROM scenic_spot;
SELECT * FROM food;

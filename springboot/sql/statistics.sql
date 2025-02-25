-- 1. 按废品分类统计回收重量和金额（比如用于饼图）
SELECT 
    wc.category_name,
    SUM(rr.weight) as total_weight,
    SUM(rr.amount) as total_amount
FROM recycling_record rr
JOIN waste_category wc ON rr.category_id = wc.id
WHERE rr.pickup_time BETWEEN :start_date AND :end_date
GROUP BY wc.category_name;

-- 2. 按时间统计每日/周/月回收订单数（比如用于折线图）
SELECT 
    DATE_FORMAT(pickup_time, '%Y-%m-%d') as date,
    COUNT(*) as order_count,
    SUM(amount) as daily_amount
FROM recycling_record
WHERE pickup_time BETWEEN :start_date AND :end_date
GROUP BY DATE_FORMAT(pickup_time, '%Y-%m-%d')
ORDER BY date;

-- 3. 回收员业绩排行（比如用于柱状图）
SELECT 
    u.username as recycler_name,
    COUNT(rr.id) as total_orders,
    SUM(rr.amount) as total_amount
FROM recycling_record rr
JOIN user u ON rr.recycler_id = u.id
WHERE rr.pickup_time BETWEEN :start_date AND :end_date
GROUP BY rr.recycler_id, u.username
ORDER BY total_amount DESC
LIMIT 10;

-- 4. 用户满意度分布（比如用于饼图）
SELECT 
    rating,
    COUNT(*) as rating_count
FROM order_review
WHERE created_at BETWEEN :start_date AND :end_date
GROUP BY rating;

-- 5. 各区域回收订单热力图数据
SELECT 
    latitude,
    longitude,
    COUNT(*) as order_count
FROM appointment_order
WHERE created_at BETWEEN :start_date AND :end_date
GROUP BY latitude, longitude; 
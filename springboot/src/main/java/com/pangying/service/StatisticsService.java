package com.pangying.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取废品分类统计数据
     */
    public List<Map<String, Object>> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                wc.category_name,
                SUM(rr.weight) as total_weight,
                SUM(rr.amount) as total_amount
            FROM recycling_record rr
            JOIN waste_category wc ON rr.category_id = wc.id
            WHERE rr.pickup_time BETWEEN ? AND ?
            GROUP BY wc.category_name
        """;
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    /**
     * 获取订单趋势数据
     */
    public List<Map<String, Object>> getOrderTrend(LocalDateTime startDate, LocalDateTime endDate, String timeUnit) {
        String dateFormat = switch (timeUnit) {
            case "week" -> "%Y-%u";
            case "month" -> "%Y-%m";
            default -> "%Y-%m-%d";
        };

        String sql = """
            SELECT 
                DATE_FORMAT(pickup_time, ?) as date,
                COUNT(*) as order_count,
                SUM(amount) as daily_amount
            FROM recycling_record
            WHERE pickup_time BETWEEN ? AND ?
            GROUP BY DATE_FORMAT(pickup_time, ?)
            ORDER BY date
        """;
        return jdbcTemplate.queryForList(sql, dateFormat, startDate, endDate, dateFormat);
    }

    /**
     * 获取回收员排行榜
     */
    public List<Map<String, Object>> getRecyclerRanking(Integer limit) {
        String sql = """
            SELECT 
                u.username as recycler_name,
                COUNT(rr.id) as total_orders,
                SUM(rr.amount) as total_amount
            FROM recycling_record rr
            JOIN user u ON rr.recycler_id = u.id
            GROUP BY rr.recycler_id, u.username
            ORDER BY total_amount DESC
            LIMIT ?
        """;
        return jdbcTemplate.queryForList(sql, limit);
    }

    /**
     * 获取用户满意度统计
     */
    public List<Map<String, Object>> getSatisfactionStats() {
        String sql = """
            SELECT 
                rating,
                COUNT(*) as rating_count
            FROM order_review
            GROUP BY rating
        """;
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 获取订单地理分布数据
     */
    public List<Map<String, Object>> getOrderHeatmap(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                latitude,
                longitude,
                COUNT(*) as order_count
            FROM appointment_order
            WHERE created_at BETWEEN ? AND ?
            GROUP BY latitude, longitude
        """;
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }
}

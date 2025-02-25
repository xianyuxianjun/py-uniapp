package com.pangying.controller;

import com.pangying.entity.R;
import com.pangying.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;  // 添加 Service 注入

    /**
     * 获取废品分类统计数据
     * 用于饼图展示各类废品的回收重量和金额分布
     */
    @GetMapping("/category-stats")
    public R getCategoryStatistics(  // 移除泛型，使用通用的 R
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate
    ) {
        // 如果未传入日期，默认统计最近30天
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return R.success(statisticsService.getCategoryStats(startDate, endDate));
    }

    /**
     * 获取订单趋势数据
     * 用于折线图展示订单数量和金额的时间趋势
     */
    @GetMapping("/order-trend")
    public R getOrderTrend(  // 移除泛型
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
        @RequestParam(defaultValue = "day") String timeUnit  // 支持 day, week, month
    ) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return R.success(statisticsService.getOrderTrend(startDate, endDate, timeUnit));
    }

    /**
     * 获取回收员排行榜
     * 用于柱状图展示回收员的业绩排名
     */
    @GetMapping("/recycler-ranking")
    public R getRecyclerRanking(
        @RequestParam(defaultValue = "10") Integer limit
    ) {
        return R.success(statisticsService.getRecyclerRanking(limit));
    }

    /**
     * 获取用户满意度分布
     * 用于饼图展示用户评分分布
     */
    @GetMapping("/satisfaction-stats")
    public R getSatisfactionStats() {
        return R.success(statisticsService.getSatisfactionStats());
    }

    /**
     * 获取订单地理分布数据
     * 用于地图热力图展示订单分布
     */
    @GetMapping("/order-heatmap")
    public R getOrderHeatmap(  // 移除泛型
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate
    ) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return R.success(statisticsService.getOrderHeatmap(startDate, endDate));
    }
}

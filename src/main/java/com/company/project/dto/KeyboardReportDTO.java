package com.company.project.dto;

import lombok.Data;

/**
 * 键盘敲击高频上报对象
 */
@Data
public class KeyboardReportDTO {

    /**
     * 增量敲击次数 (这段时间内敲了多少下)
     */
    private Integer taps;

    /**
     * 增量收获数量 (这段时间内收了多少南瓜/星星)
     */
    private Integer harvests;

    /**
     * 这段时间内最常用的键 (如 "SPACE", "J")
     */
    private String hotKey;

    /**
     * 客户端时间戳 (防止重放攻击，可选)
     */
    private Long timestamp;
}

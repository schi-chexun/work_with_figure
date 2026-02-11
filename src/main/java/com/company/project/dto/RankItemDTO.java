package com.company.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankItemDTO {
    private String username;
    private String avatar; // 既然是朋友圈排名，最好有头像，这里先预留
    private Integer score; // 敲击数或收获数
    private Integer rank;
}

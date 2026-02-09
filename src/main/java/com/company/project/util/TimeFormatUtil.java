package com.company.project.util;

/**
 * 时间格式化工具类
 */
public class TimeFormatUtil {

    /**
     * 将秒数格式化为可读字符串
     * @param seconds 秒数
     * @return 格式化字符串，如 "2小时30分钟"
     */
    public static String formatSeconds(int seconds) {
        if (seconds <= 0) {
            return "0分钟";
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        if (sb.length() == 0) {
            sb.append("不到1分钟");
        }

        return sb.toString();
    }

}

package com.sky.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 企业级订单号生成工具类（单机/小规模集群版）
 * 订单号格式：业务前缀 + 时间戳(yyyyMMddHHmmssSSS) + 机器标识 + 随机数
 * 示例：ORD20250905143025123001008（ORD_20250905143025123_001_008）
 */
public class NumberUtil {

    // 1. 业务前缀（区分不同业务的订单，如ORD=普通订单，SHP=售后订单）
    private static final String BUSINESS_PREFIX = "ORD";

    // 2. 机器标识（单机固定，集群可配置不同值，如001、002，避免同时间戳冲突）
    private static final String MACHINE_ID = "001";

    // 3. 随机数位数（根据并发量调整，3位=支持同一毫秒1000个订单）
    private static final int RANDOM_NUM_LENGTH = 3;

    // 4. 时间戳格式（精确到毫秒，确保可读性和唯一性）
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    // 随机数生成器（ThreadLocalRandom线程安全，性能优于Random）
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * 生成订单号
     * @return 全局唯一订单号
     */
    public static String generateOrderNumber() {
        // 步骤1：获取当前时间戳（yyyyMMddHHmmssSSS，如20250905143025123）
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

        // 步骤2：生成固定位数的随机数（不足补0，如3位：8→008，12→012）
        String randomNum = generateFixedLengthRandomNum(RANDOM_NUM_LENGTH);

        // 步骤3：拼接订单号（业务前缀+时间戳+机器标识+随机数）
        return String.join("", BUSINESS_PREFIX, timestamp, MACHINE_ID, randomNum);
    }

    /**
     * 生成固定位数的随机数（不足补0）
     * @param length 随机数位数
     * @return 固定位数的随机数字符串
     */
    private static String generateFixedLengthRandomNum(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        // 生成0~10^length -1 之间的随机数（如3位：0~999）
        int max = (int) Math.pow(10, length) - 1;
        int randomInt = RANDOM.nextInt(max + 1);
        // 不足位数补0（使用String.format格式化，%03d表示3位整数，不足补0）
        return String.format("%0" + length + "d", randomInt);
    }

}

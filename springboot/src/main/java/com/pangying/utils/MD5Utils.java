package com.pangying.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * MD5密码加密工具类（仅供学习参考，不推荐用于生产环境）
 *
 * 安全警告：
 * 1. MD5已被证实不安全，容易遭受彩虹表攻击
 * 2. 建议使用BCrypt、PBKDF2或SHA-256等更安全的哈希算法
 * 3. 实际应用中应结合随机盐值使用
 */
public class MD5Utils {

    // 私有构造方法防止实例化
    private MD5Utils() {
        throw new IllegalStateException("工具类禁止实例化");
    }

    /**
     * 将字符串进行MD5加密
     * @param input 原始字符串
     * @return 32位小写十六进制格式的MD5哈希值
     * @throws IllegalArgumentException 当输入为null时抛出
     */
    public static String encode(String input) {
        if (input == null) {
            throw new IllegalArgumentException("输入不能为null");
        }

        try {
            // 创建MD5摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算哈希值
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // 将字节数组转换为十六进制字符串
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // 理论上不会发生，因为MD5是标准算法
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串（小写）
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        final char[] hexArray = "0123456789abcdef".toCharArray();

        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}

package com.zg.common.util.reflect;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TypeConverter {

    public static Object convertStringToType(String value, Class<?> type) throws Exception {
        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(value);
        } else if (type == Float.class || type == float.class) {
            return Float.parseFloat(value);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == Character.class || type == char.class) {
            return value.charAt(0);
        } else if (type == String.class) {
            return value;
        } else if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, value);
        } else if (type == LocalDate.class) {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } else if (type == LocalDateTime.class) {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else if (Date.class.isAssignableFrom(type)) {
            // 这里假设输入的是 ISO8601 格式的日期字符串
            return new Date(Long.parseLong(value));
        } else {
            // 使用 Jackson 库处理自定义对象
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(value, type);
        }
    }

    public static void main(String[] args) throws Exception {
        // 测试不同的类型转换
        System.out.println(convertStringToType("123", Integer.class)); // 输出: 123
        System.out.println(convertStringToType("true", Boolean.class)); // 输出: true
        System.out.println(convertStringToType("A", Character.class)); // 输出: A
        System.out.println(convertStringToType("2023-05-20", LocalDate.class)); // 输出: 2023-05-20
        // ... 更多测试 ...
    }
}
package com.smooth.common.utils.json.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 基于时间戳的 LocalDateTime 反序列化器
 */
@Slf4j
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();
        if (token == JsonToken.VALUE_NUMBER_INT) {
            // 将 Long 时间戳，转换为 LocalDateTime 对象
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
        }

        if (token == JsonToken.VALUE_STRING) {
            String str = p.getText().trim();
            if (str.isEmpty()) {
                return null;
            }
            // String转Long 二次尝试 Long 时间戳格式
            try {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(str)), ZoneId.systemDefault());
            } catch (NumberFormatException ignored) {
                // 非数字，尝试转 ISO（时区/非时区偏移）
                try {
                    return LocalDateTime.ofInstant(Instant.parse(str), ZoneId.systemDefault());
                } catch (DateTimeParseException ignored2) {
                    try {
                        return LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } catch (DateTimeParseException ignored3) {
                        // 非ISO, 纯字符串处理
                        try {
                            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        } catch (DateTimeParseException ignored4) {
                            try {
                                return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                            } catch (DateTimeParseException ignored5) {
                                throw ctxt.weirdStringException(str, LocalDateTime.class, "Invalid LocalDateTime");
                                // 或者返回jackson标准钩子   return (LocalDateTime) ctxt.handleUnexpectedToken(LocalDateTime.class, p);
                            }
                        }
                    }
                }
            }
        }

        log.warn("时间入参反序列化, 格式未匹配, 走默认处置, 当前格式: {}", token);
        return null;
    }

}

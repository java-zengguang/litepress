package io.github.java_zengguang.litepress.web.entity;



import java.io.Serial;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String name;
    public String value;
    public String domain;
    public String path;
    public Long maxAge; // 使用Long以允许null值表示未设置
    public Boolean secure;
    public Boolean httpOnly;
    public String sameSite;

    public CookieEntity(String name, String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * 将CookieEntity转换为符合Set-Cookie响应头格式的字符串。
     *
     * @return 符合HTTP协议的Set-Cookie头字符串
     */
    public String toCookieString() {
        if (name == null || value == null) {
            throw new IllegalStateException("Name and value must not be null.");
        }

        StringBuilder cookieBuilder = new StringBuilder();

        try {
            // Name=Value pair, URL encoding the value
            cookieBuilder.append(URLEncoder.encode(name, StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));

            appendIfNotNull(cookieBuilder, "Domain", domain);
            appendIfNotNull(cookieBuilder, "Path", path);
            appendIfNotNull(cookieBuilder, "Max-Age", maxAge != null ? maxAge.toString() : null);
            appendIfTrue(cookieBuilder, "Secure", secure);
            appendIfTrue(cookieBuilder, "HttpOnly", httpOnly);
            appendIfNotNull(cookieBuilder, "SameSite", sameSite);

        } catch (Exception e) {
            throw new RuntimeException("Error encoding cookie", e);
        }

        return cookieBuilder.toString();
    }

    private void appendIfNotNull(StringBuilder builder, String key, String value) {
        if (value != null && !value.isEmpty()) {
            builder.append("; ").append(key).append("=").append(value);
        }
    }

    private void appendIfNotNull(StringBuilder builder, String key, Object value) {
        if (value != null) {
            builder.append("; ").append(key).append("=").append(value);
        }
    }

    private void appendIfTrue(StringBuilder builder, String key, Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            builder.append("; ").append(key);
        }
    }
}

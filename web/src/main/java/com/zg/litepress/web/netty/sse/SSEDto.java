package com.zg.litepress.web.netty.sse;

import com.zg.litepress.core.bean.entity.MainModel;

public class SSEDto extends MainModel {
    public String clientId;  //客户端ID
    public String event;
    public Integer id;
    public String data;
    public String flag;  //keep-保持  stop-终止


    public String toSseFormat() {
        StringBuilder sb = new StringBuilder();

        if (this.event != null && !this.event.isEmpty()) {
            sb.append("event: ").append(this.event).append("\n");
        }

        if (this.id != null) {
            sb.append("id: ").append(this.id).append("\n");
        }

        if (this.data != null && !this.data.isEmpty()) {
            // 如果数据中包含换行符，则需要将其拆分成多行
            String[] lines = this.data.split("\n");
            for (String line : lines) {
                sb.append("data: ").append(line).append("\n");
            }
        }

        // 添加两行换行符来表示消息结束
        sb.append("\n");

        return sb.toString();
    }
}

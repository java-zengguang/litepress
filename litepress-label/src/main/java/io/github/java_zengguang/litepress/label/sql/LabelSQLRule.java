package io.github.java_zengguang.litepress.label.sql;


import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.github.java_zengguang.litepress.db.dao.dao.SimpleEntityDao;
import io.github.java_zengguang.litepress.label.entity.LabelEntity;
import io.github.java_zengguang.litepress.label.entity.LabelMetadata;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;
import org.tinylog.Logger;

import java.util.*;

public class LabelSQLRule extends BasicRule implements Rule {

    private final String dataSource;
    private final String sql;

    LabelSQLRule(String name, String description, int priority, String dataSource, String sql) {
        super(name, description, priority);

        this.dataSource = dataSource;
        this.sql = sql;
    }

    public boolean evaluate(Facts facts) {
        Object input = facts.get("input");
        Map<String, Set<LabelEntity>> labelMap = facts.get("labelMap");
        if (labelMap == null) {
            labelMap = new HashMap<>();
        }
        try {
            SimpleEntityDao SimpleEntityDao = new SimpleEntityDao(dataSource);
            //缺少一个动态匹配字符串解析器，构造sql

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline(sql);
            String resultSQL = template.apply(input);
            List<String> businessNoList = SimpleEntityDao.selectOneColList(resultSQL);
            if (businessNoList != null && !businessNoList.isEmpty()) {
                Map<String, Set<LabelEntity>> finalLabelMap = labelMap;
                businessNoList.forEach((businessNo) -> {
                    finalLabelMap.computeIfAbsent(businessNo, k -> new HashSet<>()).add(new LabelEntity(businessNo, new LabelMetadata(name)));
                });
                facts.put("labelMap", labelMap);
                return true;
            }

        } catch (Exception e) {
            Logger.error(e, name + "  规则执行异常！");
        }
        return false;
    }
}
package io.github.java_zengguang.litepress.label.rule;

import io.github.java_zengguang.litepress.label.entity.LabelEntity;
import io.github.java_zengguang.litepress.label.entity.LabelMetadata;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//一个规则组对应一系列标签，对应一个事实类型，对应一个场景
public class RulesGroup {
    private static RulesEngine rulesEngine = new DefaultRulesEngine();
    private String groupName;

    private Rules rules = new Rules();

    public RulesGroup(String groupName) {
        this.groupName = groupName;
    }

    public Set<LabelEntity> doRuleGroup(Object obj) {
        Facts facts = new Facts();
        facts.put("input", obj);
        rulesEngine.fire(rules, facts);
        return facts.get("labels");
    }

    public void addRules(Rule... ruleArray) {
        rules.register(ruleArray);
    }

    public Map<String, List<LabelMetadata>> getLabelItemsMap() {
        Map<String, List<LabelMetadata>> labelItemsMap = new HashMap<>();
        rules.forEach((rule) -> {
            if (rule instanceof LabelRule labelRule) {
                labelItemsMap.put(labelRule.getName(), labelRule.getLabelItems());
            }
        });
        return labelItemsMap;
    }


}

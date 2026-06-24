package io.github.java_zengguang.litepress.label.rule;

import io.github.java_zengguang.litepress.label.entity.LabelEntity;
import org.jeasy.rules.api.Rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RulesGroupManager {
    private static RulesGroupManager rulesGroupManager;
    private static Map<String, RulesGroup> rulesGroupMap = new HashMap<>();

    private RulesGroupManager() {
    }

    public static RulesGroupManager getInstance() {
        if (rulesGroupManager == null) {
            rulesGroupManager = new RulesGroupManager();
        }
        return rulesGroupManager;
    }


    public void addRule(String groupName, Rule... rule) {
        if (rulesGroupMap.containsKey(groupName)) {
            rulesGroupMap.get(groupName).addRules(rule);
        } else {
            RulesGroup rulesGroup = new RulesGroup(groupName);
            rulesGroup.addRules(rule);
            rulesGroupMap.put(groupName, rulesGroup);
        }
    }


    public Set<LabelEntity> doRuleGroup(String groupName, Object obj) {
        Set<LabelEntity> labels = rulesGroupMap.get(groupName).doRuleGroup(obj);
        return labels;
    }


    public RulesGroup getRuleGroup(String groupName) {
        return rulesGroupMap.get(groupName);
    }


}

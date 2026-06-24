package io.github.java_zengguang.litepress.label.sql;

import io.github.java_zengguang.litepress.label.entity.LabelEntity;
import org.jeasy.rules.api.Rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SQLRulesGroupManager {
    private static SQLRulesGroupManager rulesGroupManager;
    private static final Map<String, SQLRulesGroup> rulesGroupMap = new HashMap<>();

    private SQLRulesGroupManager() {
    }

    public static SQLRulesGroupManager getInstance() {
        if (rulesGroupManager == null) {
            rulesGroupManager = new SQLRulesGroupManager();
        }
        return rulesGroupManager;
    }


    public void addRule(String groupName, Rule... rule) {
        if (rulesGroupMap.containsKey(groupName)) {
            rulesGroupMap.get(groupName).addRules(rule);
        } else {
            SQLRulesGroup rulesGroup = new SQLRulesGroup(groupName);
            rulesGroup.addRules(rule);
            rulesGroupMap.put(groupName, rulesGroup);
        }
    }


    public Map<String, Set<LabelEntity>> doRuleGroup(String groupName, Object obj) {
        return rulesGroupMap.get(groupName).doRuleGroup(obj);
    }

}

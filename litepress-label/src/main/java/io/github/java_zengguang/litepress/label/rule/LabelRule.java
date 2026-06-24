package io.github.java_zengguang.litepress.label.rule;


import io.github.java_zengguang.litepress.label.entity.LabelEntity;
import io.github.java_zengguang.litepress.label.entity.LabelMetadata;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;
import org.tinylog.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LabelRule extends BasicRule implements Rule {
    private final LabelCondition condition;
    private final List<LabelAction> actions;
    private final Map<String, List<LabelMetadata>> labelItemsMap;
    private final List<LabelMetadata> labelItems;


    LabelRule(String name, String description, int priority, LabelCondition condition, List<LabelAction> actions, List<LabelMetadata> labelItems) {
        super(name, description, priority);
        this.condition = condition;
        this.actions = actions;
        this.labelItems = labelItems;
        this.labelItemsMap = labelItems.stream().collect(Collectors.groupingBy(x -> x.name));
    }

    public boolean evaluate(Facts facts) {
        Object input = facts.get("input");
        try {
            String labelName = this.condition.evaluate(input);
            Set<LabelEntity> labels = facts.get("labels");
            if (labels == null) {
                labels = new HashSet<>();
                facts.put("labels", labels);
            }
            if (labelItemsMap.containsKey(labelName)) {
                labels.add(new LabelEntity(labelName, labelItemsMap.get(labelName).get(0)));
                return true;
            }
        } catch (Exception e) {
            Logger.error("规则执行异常！", e);
        }

        return false;
    }

    public void execute(Facts facts) throws Exception {
        for (LabelAction action : this.actions) {
            Set<LabelEntity> labels = facts.get("labels");
            action.execute(labels);
        }
    }

    public List<LabelMetadata> getLabelItems() {
        return labelItems;
    }
}

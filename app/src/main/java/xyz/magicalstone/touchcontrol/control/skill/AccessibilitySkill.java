package xyz.magicalstone.touchcontrol.control.skill;

import xyz.magicalstone.touchcontrol.control.AccessibilityOperator;
import xyz.magicalstone.touchcontrol.skill.Skill;

import java.util.Map;

public abstract class AccessibilitySkill extends Skill {
    protected AccessibilityOperator operator;

    public AccessibilitySkill(String id, String desc, Map<String, String> argsDesc, AccessibilityOperator operator) {
        super(id, desc, argsDesc);
        this.operator = operator;
    }

    void setOperator(AccessibilityOperator newOperator) {
        this.operator = newOperator;
    }
}

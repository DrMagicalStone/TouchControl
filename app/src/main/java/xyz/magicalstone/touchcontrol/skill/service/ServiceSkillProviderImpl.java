package xyz.magicalstone.touchcontrol.skill.service;

import xyz.magicalstone.touchcontrol.control.AccessibilityOperator;
import xyz.magicalstone.touchcontrol.control.skill.WeChatCallSkill;
import xyz.magicalstone.touchcontrol.skill.SkillRegistry;

public class ServiceSkillProviderImpl extends ServiceSkillProvider {

    @Override
    public void onCreate() {
        super.onCreate();
        //Demo code
        AccessibilityOperator operator = AccessibilityOperator.getServiceInstance();
        if (operator != null) {
            exportedSkills.registerSkill(new WeChatCallSkill(operator));
        }
    }
}

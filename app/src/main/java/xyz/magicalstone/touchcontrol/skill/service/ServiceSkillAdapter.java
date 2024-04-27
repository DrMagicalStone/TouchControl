package xyz.magicalstone.touchcontrol.skill.service;

import android.content.ComponentName;
import android.content.Intent;
import org.json.JSONObject;
import xyz.magicalstone.touchcontrol.skill.Skill;

import java.util.Map;

public class ServiceSkillAdapter extends Skill {

    private final ComponentName providerServiceName;

    ServiceSkillAdapter(String id, String desc, Map<String, String> args, ComponentName providerServiceName) {
        super(id, desc, args);
        this.providerServiceName = providerServiceName;
    }

    @Override
    protected Map<String, String> active(Map<String, String> optimizedArgs) {
        return null;
    }
}


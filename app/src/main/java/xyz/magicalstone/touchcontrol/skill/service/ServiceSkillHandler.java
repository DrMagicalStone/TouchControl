package xyz.magicalstone.touchcontrol.skill.service;

import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import org.json.JSONObject;
import xyz.magicalstone.touchcontrol.skill.Skill;
import xyz.magicalstone.touchcontrol.skill.SkillRegistry;

import java.util.Map;

public class ServiceSkillHandler {

    private final ServiceSkillProvider baseService;

    private final SkillRegistry baseRegistry;

    ServiceSkillHandler(ServiceSkillProvider baseService) {
        this.baseService = baseService;
        this.baseRegistry = baseService.exportedSkills;
    }

    public void handleMessage(@NonNull Message msg) {
        if (ServiceMessageType.values()[msg.what] != ServiceMessageType.ACTIVE_SKILL) {
            return;
        }
        Message reply = Message.obtain(null, ServiceMessageType.GET_SKILL_LIST.ordinal());
        Bundle bundle = new Bundle();
        try {
            System.out.println("SKill id received");
            SkillDataWrapper data = (SkillDataWrapper) msg.getData().getSerializable("skill");
            Skill skill = baseRegistry.getSkillById(data.id);
            if (skill == null) {

                bundle.putBoolean("activated", false);
                bundle.putString("reason", "Invalid skill id: " + data.id);
                reply.setData(bundle);
                msg.replyTo.send(reply);
                return;
            }
            baseService.serviceExecutor.submit(() -> {
                Map<String, String> result = skill.active(data.args, Skill.ActivatorType.NON_AI);
                bundle.putBoolean("activated", true);
                bundle.putString("result", new JSONObject(result).toString());
                reply.setData(bundle);
                try {
                    msg.replyTo.send(reply);
                } catch (RemoteException ignored) {
                }
            });
        } catch (RemoteException ignored) {
        }
    }
}

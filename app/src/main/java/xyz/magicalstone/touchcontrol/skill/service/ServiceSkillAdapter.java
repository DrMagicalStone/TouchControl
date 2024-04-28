package xyz.magicalstone.touchcontrol.skill.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.magicalstone.touchcontrol.skill.Skill;
import xyz.magicalstone.touchcontrol.skill.SkillRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServiceSkillAdapter extends Skill {

    private final SkillProvider baseService;

    private final ComponentName providerServiceName;

    ServiceSkillAdapter(String id, String desc, Map<String, String> args, SkillProvider baseService, ComponentName providerServiceName) {
        super(id, desc, args);
        this.providerServiceName = providerServiceName;
        this.baseService = baseService;
    }

    @Override
    protected Map<String, String> active(Map<String, String> optimizedArgs) {
        Map<String, String> returnValue = new HashMap<>();
        final Exception[] exception = {null};
        Messenger receiver = new Messenger(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                if(!data.getBoolean("activated")) {
                    exception[0] = new IllegalArgumentException("The Skill this adapter " + id + " linked to doesn't exist.");
                    returnValue.notify();
                    return;
                }
                try {
                    returnValue.putAll(jsonToStringMap(new JSONObject(data.getString("result"))));
                } catch (JSONException e) {
                    exception[0] = e;
                } finally {
                    returnValue.notify();
                }
            }
        });

        Intent intent = new Intent();
        intent.setComponent(providerServiceName);
        Message messageToSend = Message.obtain(null, ServiceMessageType.GET_SKILL_LIST.ordinal());
        Bundle bundle = new Bundle();
        bundle.putSerializable("skill", new SkillDataWrapper(id, "", optimizedArgs));
        messageToSend.setData(bundle);
        ServiceConnection connection = new OneTimeConnection(receiver, messageToSend);
        baseService.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        try {
            returnValue.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        baseService.unbindService(connection);
        if (exception[0] != null) {
            returnValue.put("succeed", Boolean.toString(false));
            returnValue.put("reason", exception[0].getMessage());
        } else {
            returnValue.put("succeed", Boolean.toString(true));
        }
        return returnValue;
    }

    static Map<String, String> jsonToStringMap(JSONObject json) {
        Map<String, String> res = new HashMap<>();
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            res.put(key, json.optString(key));
        }
        return res;
    }
}

class ServiceSkillHandler {

    private final SkillProvider baseService;

    private final SkillRegistry baseRegistry;

    ServiceSkillHandler(SkillProvider baseService) {
        this.baseService = baseService;
        this.baseRegistry = baseService.importedSkills0;
    }

    public void handleMessage(@NonNull Message msg) {
        if (ServiceMessageType.values()[msg.what] != ServiceMessageType.ACTIVE_SKILL) {
            return;
        }
        Message reply = Message.obtain(null, ServiceMessageType.GET_SKILL_LIST.ordinal());
        Bundle bundle = new Bundle();
        try {
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
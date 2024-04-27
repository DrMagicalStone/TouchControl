package xyz.magicalstone.touchcontrol.skill.service;

import android.os.*;
import android.support.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.magicalstone.touchcontrol.skill.Skill;
import xyz.magicalstone.touchcontrol.skill.SkillRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkillProvider {

    private static final ExecutorService serviceExecutor = Executors.newCachedThreadPool();

    static final Messenger serviceMessenger = new Messenger(new MessageHandler(Looper.getMainLooper()));

    public static class MessageHandler extends Handler {

        public MessageHandler(@NonNull @NotNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Message reply = Message.obtain(null, ServiceMessageType.GET_SKILL_LIST.ordinal());
            Bundle bundle = new Bundle();
            boolean replied = false;
            switch (ServiceMessageType.values()[msg.what]) {
                case GET_SKILL_LIST:
                    bundle.putStringArray("value", SkillRegistry.getExportedSkills().stream().map((skill -> skill.id)).toArray(String[]::new));
                    break;
                case ACTIVE_SKILL:
                    try {
                        String skillID = msg.getData().getString("skillID");
                        JSONObject jsonArgs = new JSONObject(msg.getData().getString("skillArgs"));
                        Map<String, String> args = new HashMap<>();
                        {
                            for (Iterator<String> it = jsonArgs.keys(); it.hasNext(); ) {
                                String key = it.next();
                                args.put(key, jsonArgs.optString(key));
                            }
                        }
                        Skill skill = SkillRegistry.getSkillById(skillID);
                        if (skill == null) {

                            bundle.putBoolean("activated", false);
                            bundle.putString("reason", "Invalid skill id: " + skillID);
                            break;
                        }
                        replied = true;
                        serviceExecutor.submit(() -> {
                            Map<String, String> result = skill.active(args, Skill.ActivatorType.NON_AI);
                            bundle.putBoolean("activated", false);
                            bundle.putString("reason", "Invalid skill id: " + skillID);
                            reply.setData(bundle);
                            reply.replyTo = SkillProvider.serviceMessenger;
                            try {
                                msg.replyTo.send(reply);
                            } catch (RemoteException ignored) {
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
            if (replied) {
                return;
            }
            reply.setData(bundle);
            reply.replyTo = SkillProvider.serviceMessenger;
            try {
                msg.replyTo.send(reply);
            } catch (RemoteException ignored) {
            }
        }
    }
}


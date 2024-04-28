package xyz.magicalstone.touchcontrol.skill.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import xyz.magicalstone.touchcontrol.skill.CombinedSkillRegistry;
import xyz.magicalstone.touchcontrol.skill.SkillRegistry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Usage:
 * 1. Extend this class.
 * 2. Register the extended class as a service.
 * 3. Register all skills to export after the service started.
 * 4. Call refreshImportedSkills();
 * 5. Other services' and the service's exported skills are all in importedSkills.
 */
public abstract class SkillProvider extends Service {

    /**
     * Skills that will be seen and invoked by other apps.
     * Should be all added just after the service created.
     */
    public final SkillRegistry exportedSkills = new SkillRegistry();
    /**
     * Skills that will be seen and invoked by other apps.
     * Should be all added just after the service created.
     */
    final SkillRegistry importedSkills0 = new SkillRegistry();

    /**
     * Unmodifiable version of importedSkills0.
     */
    final SkillRegistry importedSkills = new CombinedSkillRegistry(importedSkills0);

    final ExecutorService serviceExecutor = Executors.newCachedThreadPool();

    final Messenger serviceMessenger = new Messenger(new MessageHandler(Looper.getMainLooper()));

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    public class MessageHandler extends Handler {

        private final ServiceSkillHandler serviceSkillHandler = new ServiceSkillHandler(SkillProvider.this);

        public MessageHandler(@NonNull @NotNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (ServiceMessageType.values()[msg.what]) {
                case GET_SKILL_LIST:
                    Message reply = Message.obtain(null, ServiceMessageType.GET_SKILL_LIST.ordinal());
                    Bundle bundle = new Bundle();
                    SkillDataWrapper[] dataOfSkills = exportedSkills.getAllSkillsIdToSkill().values().stream().map(skill -> new SkillDataWrapper(skill.id, skill.desc, skill.argsDesc)).toArray(SkillDataWrapper[]::new);
                    bundle.putSerializable("skills", dataOfSkills);
                    reply.setData(bundle);
                    try {
                        msg.replyTo.send(reply);
                    } catch (RemoteException ignored) {
                    }
                    break;
                case ACTIVE_SKILL:
                    serviceSkillHandler.handleMessage(msg);
                    break;
            }
        }
    }

    public void refreshImportedSkills() {
        List<ResolveInfo> listOfServices = getPackageManager().queryIntentServices(new Intent("xyz.magicalstone.oldfriend.SkillProvider"), PackageManager.MATCH_DEFAULT_ONLY);
        final int[] queryingServiceCounter = {listOfServices.size()};
        ArrayBlockingQueue<SkillDataWrapper[]> servicesNameToAdd = new ArrayBlockingQueue<>(listOfServices.size());
        Object lock = new Object();
        for (ResolveInfo info : listOfServices) {
            String classOfService = info.serviceInfo.packageName;
            ComponentName componentNameOfService = new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
            Intent intent = new Intent();
            intent.setComponent(componentNameOfService);
            ServiceConnection[] connection = new ServiceConnection[]{null};
            Messenger receiver = new Messenger(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    Bundle data = msg.getData();
                    synchronized (lock) {
                        SkillDataWrapper[] skillData = (SkillDataWrapper[]) data.getSerializable("skills");
                        for (SkillDataWrapper skillDatum : skillData) {
                            skillDatum.providerServiceName = componentNameOfService;
                        }
                        servicesNameToAdd.offer(skillData);
                        queryingServiceCounter[0]--;
                    }
                    unbindService(connection[0]);
                }
            });
            Message messageToSend = new Message();
            messageToSend.what = ServiceMessageType.GET_SKILL_LIST.ordinal();
            connection[0] = new OneTimeConnection(receiver, messageToSend);
            bindService(intent, connection[0], Context.BIND_AUTO_CREATE);
        }

        try {
            while (true) {
                synchronized (lock) {
                    if (queryingServiceCounter[0] != 0) {
                        for (SkillDataWrapper skillData : servicesNameToAdd.take()) {
                            importedSkills0.registerSkill(new ServiceSkillAdapter(skillData.id, skillData.desc, skillData.args, this, skillData.providerServiceName));
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}



class OneTimeConnection implements ServiceConnection {

    private final Messenger receiver;
    private final Message message;

    OneTimeConnection(Messenger receiver, Message message) {
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Messenger sender = new Messenger(service);
        message.replyTo = receiver;
        try {
            sender.send(message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}

class SkillDataWrapper implements Externalizable {

    public String id;

    public String desc;

    public Map<String, String> args;

    public transient ComponentName providerServiceName;

    public SkillDataWrapper(String id, String desc, Map<String, String> args) {
        this.id = id;
        this.desc = desc;
        this.args = args;
    }

    public SkillDataWrapper() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(desc);
        out.writeObject(args);
    }

    @Override
    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
        id = in.readUTF();
        desc = in.readUTF();
        args = (Map<String, String>) in.readObject();
    }
}
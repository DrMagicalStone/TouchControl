package xyz.magicalstone.touchcontrol.skill.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

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

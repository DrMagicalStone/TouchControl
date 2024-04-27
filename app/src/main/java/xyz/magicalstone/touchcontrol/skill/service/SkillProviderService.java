package xyz.magicalstone.touchcontrol.skill.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SkillProviderService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return SkillProvider.serviceMessenger.getBinder();
    }
}

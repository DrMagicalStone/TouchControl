package xyz.magicalstone.applicationcontrol.control.skill;

import android.content.ComponentName;
import android.content.Intent;

public final class OpenAppSkill extends AccessibilitySkill {

    public OpenAppSkill(AccessibilityOperator operator) {
        super("xyz.magicalstone.applicationcontrol.OpenApp", "Open an application.", null, operator);
    }

    @Override
    protected Map<String, String> active(Map<String, String> optimizedArgs) {
        try {
            openApp();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void openApp() throws InterruptedException {
        String packageName = "com.example.app";
        String className = "com.example.app.SettingsActivity";

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));

        System.out.println("Opening application.");
        operator.startActivity(intent);
        System.out.println("Application opened.");
    }
}

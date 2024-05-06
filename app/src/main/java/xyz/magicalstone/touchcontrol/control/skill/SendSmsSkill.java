package xyz.magicalstone.smssendingcontrol.control.skill;

import android.content.Intent;
import android.net.Uri;

public final class SendSmsSkill extends AccessibilitySkill {

    public SendSmsSkill(AccessibilityOperator operator) {
        super("xyz.magicalstone.smssendingcontrol.SendSms", "Send an SMS.", null, operator);
    }

    @Override
    protected Map<String, String> active(Map<String, String> optimizedArgs) {
        try {
            sendSms();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void sendSms() throws InterruptedException {
        String phoneNumber = "1234567890";
        String message = "Hello world";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);

        System.out.println("Sending SMS.");
        operator.startActivity(intent);
        System.out.println("SMS sent.");
    }
}

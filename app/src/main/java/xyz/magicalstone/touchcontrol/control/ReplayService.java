package xyz.magicalstone.touchcontrol.control;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class ReplayService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public boolean onGesture(AccessibilityGestureEvent gestureEvent) {

        return super.onGesture(gestureEvent);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED |
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED |
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START |
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END |
                AccessibilityEvent.TYPE_GESTURE_DETECTION_START |
                AccessibilityEvent.TYPE_GESTURE_DETECTION_END;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            GestureDescription.Builder builder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(200, 800);
            path.lineTo(200, 1600);
            path.lineTo(800, 1200);
            path.lineTo(200, 800);
            builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1000));
            dispatchGesture(builder.build(), null, null);
        }).start();

    }
}

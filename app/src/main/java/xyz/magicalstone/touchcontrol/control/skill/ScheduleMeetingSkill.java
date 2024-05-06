package xyz.magicalstone.meetingcontrol.control.skill;

import android.content.Intent;
import android.provider.CalendarContract;

import java.util.Calendar;

public final class ScheduleMeetingSkill extends AccessibilitySkill {

    public ScheduleMeetingSkill(AccessibilityOperator operator) {
        super("xyz.magicalstone.meetingcontrol.ScheduleMeeting", "Schedule a meeting.", null, operator);
    }

    @Override
    protected Map<String, String> active(Map<String, String> optimizedArgs) {
        try {
            scheduleMeeting();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void scheduleMeeting() throws InterruptedException {
        String title = "Meeting with Team";
        String location = "Office";
        String description = "Discuss project updates";
        int startYear = 2024, startMonth = Calendar.MAY, startDay = 10, startHour = 9, startMinute = 0;
        int endYear = 2024, endMonth = Calendar.MAY, endDay = 10, endHour = 10, endMinute = 0;

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, description);

        Calendar startTime = Calendar.getInstance();
        startTime.set(startYear, startMonth, startDay, startHour, startMinute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(endYear, endMonth, endDay, endHour, endMinute);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());

        System.out.println("Scheduling meeting.");
        operator.startActivity(intent);
        System.out.println("Meeting scheduled.");
    }
}

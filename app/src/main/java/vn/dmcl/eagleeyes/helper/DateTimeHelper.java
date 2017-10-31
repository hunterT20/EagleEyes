package vn.dmcl.eagleeyes.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class DateTimeHelper {
    /**
     * Transform Calendar to ISO 8601 string.
     */
    private static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
                .format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Get current date and time formatted as ISO 8601 string.
     */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Transform ISO 8601 string to Calendar.
     */
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).parse(s);
        calendar.setTime(date);
        return calendar;
    }

    public static String convertSecondToTimeString(double second) {
        long time = Math.round(second);
        String result = "" + time;
        if (time < 60)
            result = time + " giây";
        else if (time < 60 * 60)
            result = time / 60 + " phút " + Math.round(((double) time % 60)) + " giây";
        else if (time < 60 * 60 * 24)
            result = time / (60 * 60) + " giờ " + Math.round(((double) time % 60)) + " phút " + Math.round(((double) time % (60 * 60))) + " giây";
        return result;
    }
}

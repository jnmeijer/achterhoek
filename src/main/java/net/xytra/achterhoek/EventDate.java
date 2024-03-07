package net.xytra.achterhoek;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EventDate {
    private Date date;
    private DatePrecision precision;

    private EventDate(Date date, DatePrecision precision) {
        this.date = date;
        this.precision = precision;
    }

    public Date getDate() {
        return date;
    }

    public DatePrecision getPrecision() {
        return precision;
    }

    private int getYear() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(getDate());
        return calendar.get(Calendar.YEAR);
    }

    public static EventDate parseDate(String dateString) {
        String[] dateParts = dateString.split("[.]");
        short year = Short.parseShort(dateParts[2]);
        short month = Short.parseShort(dateParts[1]);
        short day = Short.parseShort(dateParts[0]);

        DatePrecision precision = null;
        Calendar calendar = GregorianCalendar.getInstance();

        if (year > 0) {
            precision = DatePrecision.Y;
            calendar.set(Calendar.YEAR, year);

            if (month > 0) {
                precision = DatePrecision.YM;
                calendar.set(Calendar.MONTH, month-1);

                if (day > 0) {
                    precision = DatePrecision.YMD;
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                }
            }
        }

        if (precision == null) {
            return null;
        } else {
            return new EventDate(calendar.getTime(), precision);
        }
    }

    // Two or three parts: day, month, optional year
    public EventDate parseMdBornDate(String dateString) {
        String[] parts = dateString.split(" ");
        byte day = Byte.parseByte(parts[0]);
        byte month = -1;

        if (parts[1].startsWith("jan")) {
            month = 0;
        } else if (parts[1].startsWith("febr")) {
            month = 1;
        } else if (parts[1].startsWith("maart")) {
            month = 2;
        } else if (parts[1].startsWith("apr")) {
            month = 3;
        } else if (parts[1].startsWith("mei")) {
            month = 4;
        } else if (parts[1].startsWith("juni")) {
            month = 5;
        } else if (parts[1].startsWith("juli")) {
            month = 6;
        } else if (parts[1].startsWith("aug")) {
            month = 7;
        } else if (parts[1].startsWith("sept")) {
            month = 8;
        } else if (parts[1].startsWith("oct")) {
            month = 9;
        } else if (parts[1].startsWith("nov")) {
            month = 10;
        } else if (parts[1].startsWith("dec")) {
            month = 11;
        }

        int year = getYear();
        if (parts.length > 2) {
            year = Integer.parseInt(parts[2]);
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        DatePrecision precision = DatePrecision.Y;

        if (month >= 0) {
            calendar.set(Calendar.MONTH, month);
            precision = DatePrecision.YM;

            if (day > 0) {
                calendar.set(Calendar.DAY_OF_MONTH, day);
                precision = DatePrecision.YMD;
            }
        }

        return new EventDate(calendar.getTime(), precision);
    }

    public String toString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(getDate());
    }
}

package Models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cesar on 08/09/15.
 */
public class Reminder {
    public int frequency;
    public String time_unit, subject;
    private Date next_reminder;
    public boolean remind;

    public void setNext_reminder(String next_reminder)
    {
        try {
            SimpleDateFormat parserSDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.next_reminder = parserSDF.parse(next_reminder);
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }

    public Date getNext_reminder(){
        return next_reminder;
    }
}

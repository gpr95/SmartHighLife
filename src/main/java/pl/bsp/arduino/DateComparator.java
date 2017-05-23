package pl.bsp.arduino;

import pl.bsp.model.ParentalControlPolicy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by Kamil on 2017-05-22.
 */
public class DateComparator implements Comparator<ParentalControlPolicy>{

    @Override
    public int compare(ParentalControlPolicy x, ParentalControlPolicy y) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dateX = null;
        java.util.Date dateY = null;
        try {
            dateX = format.parse(x.getStartTime());
            dateY = format.parse(y.getEndTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateX.getTime() < dateY.getTime())
        {
            return -1;
        }
        if (dateX.getTime() > dateY.getTime())
        {
            return 1;
        }
        return 0;
    }

}

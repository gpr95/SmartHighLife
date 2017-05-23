package pl.bsp.arduino;

import pl.bsp.model.ParentalControlPolicy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by Kamil on 2017-05-22.
 */
public class DateComparator implements Comparator<Event>{

    @Override
    public int compare(Event x, Event y) {

        if (x.getTime() < y.getTime())
        {
            return -1;
        }
        if (x.getTime() > y.getTime())
        {
            return 1;
        }
        return 0;
    }

}

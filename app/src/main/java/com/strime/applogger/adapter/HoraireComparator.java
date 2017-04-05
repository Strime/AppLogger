package com.strime.applogger.adapter;

/**
 * Created by gsa13442 on 05/04/2017.
 */

public class HoraireComparator implements java.util.Comparator<com.strime.applogger.model.Horaire> {
    @Override
    public int compare(com.strime.applogger.model.Horaire o1, com.strime.applogger.model.Horaire o2) {
        if(o1.hour != o2.hour) {
            return Integer.valueOf(o1.hour).compareTo(o2.hour);
        }
        return Integer.valueOf(o1.minute).compareTo(o2.minute);
    }
}

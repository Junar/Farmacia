package cl.gob.datos.farmacias.helpers;

import java.util.Comparator;

import com.junar.searchpharma.Pharmacy;

public class PharmacyComparator<T> implements Comparator<Pharmacy> {

    @Override
    public int compare(Pharmacy lhs, Pharmacy rhs) {

        if (lhs.getDistance() < rhs.getDistance()) {
            return -1;
        } else if (lhs.getDistance() > rhs.getDistance()) {
            return 1;
        }
        return 0;
    }
}
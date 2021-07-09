package me.notom3ga.arc.util;

import java.util.ArrayList;
import java.util.List;

public class NullUtil {

    public static <T> List<T> listOrNull(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        return list;
    }
}

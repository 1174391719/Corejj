package com.zyzxsp.utils;

import java.lang.ref.Reference;
import java.util.Collection;

/**
 * Created by chenshuliang on 2018/4/24.
 */

public class Optionals {
    private Optionals() {
    }

    public static boolean isEmtpy(Reference<?> ref) {
        return ref == null || ref.get() == null;
    }

    public static boolean isEmtpy(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}

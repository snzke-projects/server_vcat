package com.vcat.common.utils;

import org.apache.commons.beanutils.BeanComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by: dong4j.
 * Date: 2016-12-04.
 * Time: 13:46.
 * Description: 对 List 元素的多个属性进行排序的类
 */

public class SortUtils {
    /**
     * List 元素的多个属性进行排序。例如 ListSorter.sort(list, "name", "age")，则先按
     * name 属性排序，name 相同的元素按 age 属性排序。
     *
     * @param list       包含要排序元素的 List
     * @param properties 要排序的属性。前面的值优先级高。
     */
    public static <V> void sort(List<V> list, final String... properties) {
        list.sort(new Comparator<V>() {
            public int compare(V o1, V o2) {
                if (o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                for (String property : properties) {
                    Comparator comparator = new BeanComparator(property);
                    int        result     = comparator.compare(o1, o2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        });
    }
}
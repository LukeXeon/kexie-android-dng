package org.kexie.android.common.databinding;

import java.util.List;
import java.util.Map;

public final class BT
{
    private BT()
    {
        throw new AssertionError();
    }

    public static boolean isEmpty(List<?> list)
    {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map)
    {
        return map == null || map.size() == 0;
    }
}
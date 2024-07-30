package ru.practicum.util.generator;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IdGenerator {
    private static Long maxItemsId = 1L;
    private static Long maxUsersId = 1L;
    private static Long maxBookingsId = 1L;

    public static Long getMaxItemsId() {
        return maxItemsId++;
    }

    public static String getMaxUsersId() {
        return String.valueOf(maxUsersId++);
    }

    public static Long getMaxBookingsId() {
        return maxBookingsId++;
    }
}

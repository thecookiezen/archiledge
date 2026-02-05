package com.thecookiezen.ladybugdb.spring;

import com.ladybugdb.LbugList;
import com.ladybugdb.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Utility class for mapping LadybugDB Value objects to Java types.
 * Provides static methods for common type conversions to eliminate duplication
 * in RowMappers.
 */
public final class ValueMappers {

    private ValueMappers() {
        // Utility class
    }

    /**
     * Maps a Value to a String.
     *
     * @param value the LadybugDB Value
     * @return the string representation
     */
    public static String asString(Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        return value.getValue().toString();
    }

    /**
     * Maps a Value to an Integer.
     *
     * @param value the LadybugDB Value
     * @return the integer value, or null if the value is null
     */
    public static Integer asInteger(Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        return Integer.parseInt(value.getValue().toString());
    }

    /**
     * Maps a Value to a Long.
     *
     * @param value the LadybugDB Value
     * @return the long value, or null if the value is null
     */
    public static Long asLong(Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        return Long.parseLong(value.getValue().toString());
    }

    /**
     * Maps a Value to a Double.
     *
     * @param value the LadybugDB Value
     * @return the double value, or null if the value is null
     */
    public static Double asDouble(Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        return Double.parseDouble(value.getValue().toString());
    }

    /**
     * Maps a Value to a Boolean.
     *
     * @param value the LadybugDB Value
     * @return the boolean value, or null if the value is null
     */
    public static Boolean asBoolean(Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        return Boolean.parseBoolean(value.getValue().toString());
    }

    /**
     * Maps a Value containing a list to a List of the specified type.
     *
     * @param value         the LadybugDB Value containing a list
     * @param elementMapper function to convert each element's string value to the
     *                      target type
     * @param <T>           the type of elements in the resulting list
     * @return the list of mapped elements, or empty list if null
     */
    public static <T> List<T> asList(Value value, Function<String, T> elementMapper) {
        if (value == null || value.isNull()) {
            return List.of();
        }

        try (LbugList lbugList = new LbugList(value)) {
            long size = lbugList.getListSize();
            List<T> result = new ArrayList<>((int) size);

            for (long i = 0; i < size; i++) {
                Value element = lbugList.getListElement(i);
                result.add(elementMapper.apply(element.getValue()));
            }

            return result;
        }
    }

    /**
     * Maps a Value containing a list to a List of Strings.
     */
    public static List<String> asStringList(Value value) {
        return asList(value, Function.identity());
    }

    /**
     * Maps a Value containing a list to a List of Integers.
     */
    public static List<Integer> asIntegerList(Value value) {
        return asList(value, Integer::parseInt);
    }

    /**
     * Maps a Value containing a list to a List of Longs.
     */
    public static List<Long> asLongList(Value value) {
        return asList(value, Long::parseLong);
    }

    /**
     * Maps a Value containing a list to a List of Doubles.
     */
    public static List<Double> asDoubleList(Value value) {
        return asList(value, Double::parseDouble);
    }
}

package com.bobocode;

import com.bobocode.data.Accounts;
import com.bobocode.model.Account;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * A generic comparator that is comparing a random field of the given class. The field is either primitive or
 * {@link Comparable}. It is chosen during comparator instance creation and is used for all comparisons.
 * <p>
 * By default it compares only accessible fields, but this can be configured via a constructor property. If no field is
 * available to compare, the constructor throws {@link IllegalArgumentException}
 *
 * @param <T> the type of the objects that may be compared by this comparator
 */
public class RandomFieldComparator<T> implements Comparator<T> {
    public static void main(String[] args) {
        Comparator<Account> accountComparator = new RandomFieldComparator<>(Account.class, false);
        System.out.println(accountComparator);
        Accounts.generateAccountList(10)
                .stream()
                .sorted(accountComparator)
                .forEach(System.out::println);
    }

    private final Field field;

    public RandomFieldComparator(Class<T> targetType) {
        this(targetType, true);
    }

    /**
     * A constructor that accepts a class and a property indicating which fields can be used for comparison. If property
     * value is true, then only public fields or fields with public getters can be used.
     *
     * @param targetType                  a type of objects that may be compared
     * @param compareOnlyAccessibleFields config property indicating if only publicly accessible fields can be used
     */
    @SneakyThrows
    public RandomFieldComparator(@NonNull Class<T> targetType, boolean compareOnlyAccessibleFields) {
        T instance = targetType.getDeclaredConstructor().newInstance();

        var fields = Arrays.stream(targetType.getDeclaredFields())
                .filter(f -> ClassUtils.isPrimitiveOrWrapper(f.getType()))
                .filter(f -> f.canAccess(instance) == compareOnlyAccessibleFields)
                .toList();

        if (fields.isEmpty()) {
            throw new IllegalArgumentException("No fields are available to compare");
        }

        this.field = fields.get(getRandomIndex(fields.size()));
        this.field.setAccessible(true);
    }

    private int getRandomIndex(int bound) {
        return new Random().nextInt(bound);
    }

    /**
     * Compares two objects of the class T by the value of the field that was randomly chosen. It allows null values
     * for the fields, and it treats null value grater than a non-null value (nulls last).
     */
    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public int compare(@NonNull T o1, @NonNull T o2) {
        Comparable first = (Comparable) field.get(o1);
        Comparable second = (Comparable) field.get(o2);
        if (first == null && second == null) {
            return 0;
        } else if (first == null) {
            return 1;
        } else if (second == null) {
            return -1;
        } else {
            return first.compareTo(second);
        }
    }

    /**
     * Returns a statement "Random field comparator of class '%s' is comparing '%s'" where the first param is the name
     * of the type T, and the second parameter is the comparing field name.
     *
     * @return a predefined statement
     */
    @Override
    public String toString() {
        return String.format("Random field comparator of class '%s' is comparing '%s'", field.getType(), field.getName());
    }
}
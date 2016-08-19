package gmjonker.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gmjonker.math.GeneralMath.max;
import static gmjonker.math.NaType.getValueOr;
import static gmjonker.math.NaType.isValue;
import static java.util.Collections.*;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * Various utility functions on collections.
 *
 * All functions are null-safe w.r.t. the passed collection, but results are never null.
 */
public class CollectionsUtil
{
    protected static final LambdaLogger log = new LambdaLogger(CollectionsUtil.class);

    @Nonnull
    public static <T, R> Collection<R> map(Collection<T> collection, Function<T, R> function)
    {
        if (collection == null)
            return emptySet();

        return collection.stream()
                .filter(o -> o != null)
                .map(function)
                .collect(Collectors.toList());
    }

    @Nonnull
    public static <T, R> List<R> map(List<T> list, Function<T, R> function)
    {
        if (list == null)
            return emptyList();

        return list.stream()
                .filter(o -> o != null)
                .map(function)
                .collect(Collectors.toList());
    }

    @Nonnull
    public static <T, R> Set<R> map(Set<T> set, Function<T, R> function)
    {
        if (set == null)
            return emptySet();

        return set.stream()
                .filter(o -> o != null)
                .map(function)
                .collect(Collectors.toSet());
    }

    /** Retains ordering **/
    @Nonnull
    public static <K1,V1,K2,V2> Map<K2,V2> map(Map<K1,V1> map, Function<K1,K2> keyMapper, Function<V1,V2> valueMapper)
    {
        if (map == null)
            return emptyMap();

        Map<K2,V2> newMap = new LinkedHashMap<>();
        for (Map.Entry<K1, V1> entry : map.entrySet()) {
            K1 key = entry.getKey();
            V1 value = entry.getValue();
            newMap.put(keyMapper.apply(key), valueMapper.apply(value));
        }
        return newMap;
    }

    @Nonnull
    public static <K1,V1,K2,V2> DefaultingHashmap<K2,V2> map(DefaultingMap<K1,V1> defaultingMap, Function<K1,K2> keyMapper,
            Function<V1,V2> valueMapper)
    {
        DefaultingHashmap<K2,V2> newMap = new DefaultingHashmap<>(valueMapper.apply(defaultingMap.getDefaultValue()));

        if (defaultingMap == null) {
            return newMap;
        }

        for (Map.Entry<K1, V1> entry : defaultingMap.entrySet()) {
            K1 key = entry.getKey();
            V1 value = entry.getValue();
            newMap.put(keyMapper.apply(key), valueMapper.apply(value));
        }
        return newMap;
    }

    @Nonnull
    public static <K1, K2, V> Map<K2, V> mapKeys(Map<K1, V> map, Function<K1, K2> keyMapper)
    {
        return map(map, keyMapper, Function.identity());
    }

    @Nonnull
    public static <K, V1, V2> Map<K, V2> mapValues(Map<K, V1> map, Function<V1, V2> valueMapper)
    {
        return map(map, Function.identity(), valueMapper);
    }

    @Nonnull
    public static <K1,V1,K2,V2> LinkedHashMap<K2,V2> map(LinkedHashMap<K1,V1> map, Function<K1,K2> keyMapper, Function<V1,V2> valueMapper)
    {
        if (map == null)
            return new LinkedHashMap<>();

        LinkedHashMap<K2,V2> newMap = new LinkedHashMap<>();
        for (Map.Entry<K1, V1> entry : map.entrySet()) {
            K1 key = entry.getKey();
            V1 value = entry.getValue();
            newMap.put(keyMapper.apply(key), valueMapper.apply(value));
        }
        return newMap;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T, R> R[] map(T[] inputArray, Function<T, R> function, Class outputClass)
    {
        if (inputArray == null)
            return (R[]) Array.newInstance(outputClass, 0);

        R[] outputArray = (R[]) Array.newInstance(outputClass, inputArray.length);
        for (int i = 0; i < inputArray.length; i++)
             outputArray[i] = function.apply(inputArray[i]);
        return outputArray;
    }

    @Nonnull
    public static double[] map(double[] inputArray, Function<Double, Double> function)
    {
        if (inputArray == null)
            return new double[0];

        double[] outputArray = new double[inputArray.length];
        for (int i = 0; i < inputArray.length; i++)
             outputArray[i] = function.apply(inputArray[i]);
        return outputArray;
    }

    @Nonnull
    public static <T> List<T> filter(List<T> list, Function<T, Boolean> function)
    {
        if (list == null)
            return emptyList();

        return list.stream().filter(function::apply).collect(Collectors.toList());
    }

    @Nonnull
    public static <T> Collection<T> filter(Collection<T> collection, Function<T, Boolean> function)
    {
        if (collection == null)
            return emptyList();

        return collection.stream().filter(function::apply).collect(Collectors.toList());
    }

    @Nonnull
    public static <T> List<T> filterNulls(List<T> list)
    {
        return filter(list, element -> element != null);
    }

    /**
     * Filter map by key and/or value. Entries must satify both the key filter and the value filter to be passed through.
     */
    @Nonnull
    public static <K, V> Map<K, V> filter(Map<K, V> map, Function<K, Boolean> keyFilter, Function<V, Boolean> valueFilter)
    {
        if (map == null)
            return new HashMap<>();

        HashMap<K,V> newMap = new HashMap<>();

        for (Map.Entry<K, V> kvEntry : map.entrySet()) {
            K key = kvEntry.getKey();
            if ( ! keyFilter.apply(key))
                continue;
            V value = kvEntry.getValue();
            if ( ! valueFilter.apply(value))
                continue;
            newMap.put(key, value);
        }

        return newMap;
    }

    /**
     * Returns the results of pairwise applying {@code function} on the elements of {@code list1} and {@code list2}.
     */
    @Nonnull
    public static <L1, L2, R> List<R> zipWith(List<L1> list1, List<L2> list2, BiFunction<L1, L2, R> function)
    {
        if (isEmpty(list1) && isEmpty(list2))
            return emptyList();
        if (list1.size() != list2.size())
            throw new RuntimeException("zipWith: lists must be equal size");

        List<R> result = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            L1 element1 = list1.get(i);
            L2 element2 = list2.get(i);
            result.add(i, function.apply(element1, element2));
        }
        return result;
    }

    @Nonnull
    @SafeVarargs
    public static <T> Set<T> asSet(T... objects)
    {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, objects);
        return set;
    }

    @Nonnull
    public static <K,V> LinkedHashMap<K,V> asMap(K key, V value)
    {
        LinkedHashMap<K,V> map = new LinkedHashMap<K, V>();
        map.put(key, value);
        return map;
    }

    @Nonnull
    public static <K,V> LinkedHashMap<K,V> asMap(K k1, V v1, K k2, V v2)
    {
        LinkedHashMap<K,V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    /**
     * Null-safe variant of {@code new ArrayList<T>(Collection<T> collection)}
     */
    @Nonnull
    public static <T> List<T> newListFrom(Collection<T> collection)
    {
        List<T> list = new ArrayList<>();
        if (collection == null)
            return list;
        list.addAll(collection);
        return list;
    }

    @Nonnull
    @SafeVarargs
    public static <T> List<T> asArrayList(T... objects)
    {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, objects);
        return list;
    }

    /** Filters nulls from objects **/
    @Nonnull
    @SafeVarargs
    public static <T> List<T> toList(T... objects)
    {
        List<T> list = new ArrayList<>();
        for (T t : objects)
            if (t != null)
                list.add(t);
        return list;
    }

    @Nonnull
    public static <R,C,V> Table<R,C,V> asTableSingleRow(R rowKey, Map<C,V> map)
    {
        Table<R,C,V> table = HashBasedTable.create();
        for (C columnKey : map.keySet()) {
            table.put(rowKey, columnKey, map.get(columnKey));
        }
        return table;
    }



    @Nonnull
    public static double[] toPrimitiveDoubleArray(List<Double> doubleList)
    {
        if (doubleList == null)
            return new double[0];
        double[] doubles = new double[doubleList.size()];
        for (int i = 0; i < doubleList.size(); i++) {
            Double aDouble = doubleList.get(i);
            doubles[i] = aDouble;
        }
        return doubles;
    }

    /**
     * Sublist of list from fromIndex, inclusive, to toIndex, exclusive.
     * Always returns a list. If fromIndex or toIndex is null, 0 or list.size() is used respectively.
     * Out of bounds indices are brought within range. Indices -1, -2, etc. may be used for
     * list.size()-1, list.size()-2, etc.
     */
    @Nonnull
    public static <T> List<T> sublist(List<T> list, @Nullable Integer fromIndex, @Nullable Integer toIndex)
    {
        if (list == null)
            return emptyList();

        int size = list.size();
        if (fromIndex == null) fromIndex = 0;
        if (toIndex == null) toIndex = size;
        if (fromIndex < 0) fromIndex = max(size + fromIndex, 0);
        if (toIndex < 0) toIndex = max(size + toIndex, 0);
        if (toIndex > size) toIndex = size;
        if (fromIndex >= toIndex)
            return emptyList();
        return list.subList(fromIndex, toIndex);
    }

    /**
     * Take the first X items of list, or less if there are less.
     */
    @Nonnull
    public static <T> List<T> take(List<T> list, int max)
    {
        return sublist(list, 0, max);
    }

    /**
     * Take the first X items of collection, or less if there are less.
     */
    @Nonnull
    public static <T> Collection<T> take(Iterable<T> iterable, int max)
    {
        Collection<T> collection = new ArrayList<>();
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext() && collection.size() < max)
            collection.add(iterator.next());
        return collection;
    }

    /**
     * Take the first X items of collection, or less if there are less.
     */
    @Nonnull
    public static <K,V> Map<K,V> take(Map<K,V> map, int max)
    {
        LinkedHashMap<K, V> newMap = new LinkedHashMap<K, V>();
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        while (iterator.hasNext() && newMap.size() < max) {
            Map.Entry<K, V> entry = iterator.next();
            newMap.put(entry.getKey(), entry.getValue());
        }
        return newMap;
    }

    /**
     * Selects the elements from {@code list} for which their corresponding element in {@code mask} is true.
     */
    @Nonnull
    public static <T> List<T> mask(List<T> list, List<Boolean> mask)
    {
        if (isEmpty(list))
            return emptyList();
        if (isEmpty(mask) || mask.size() != list.size())
            throw new RuntimeException("mask must be same size as list");

        List<T> maskedList = new ArrayList<>();
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            T element = list.get(i);
            Boolean bool = mask.get(i);
            if (bool)
                maskedList.add(element);
        }
        return maskedList;
    }

    @Nonnull
    public static <K,V> List<V> mapGetAll(Map<K,V> map,  List<K> keys)
    {
        if (map == null || keys == null)
            return emptyList();

        List<V> values = new ArrayList<>();
        for (K key : keys) {
            V value = map.get(key);
            if (value == null)
                log.debug("Key {} not found in map", key);
            else
                values.add(value);
        }
        return values;
    }

    //
    // ###################################################### SORTING ##################################################
    //

    /**
     * Sorts a map by value. Adapted from http://stackoverflow.com/a/2581754/1901037
     *
     * @return A map that, when iterated over, returns keys, values or entries sorted by value
     */
    @Nonnull
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortMapByValue(Map<K, V> map, boolean ascending)
    {
        LinkedHashMap<K,V> result = new LinkedHashMap<>();

        if (map == null)
            return result;

        Stream<Map.Entry<K,V>> st = map.entrySet().stream();
        Comparator<Map.Entry<K, V>> comparator = Comparator.comparing(Map.Entry::getValue);
        if ( ! ascending)
            comparator = comparator.reversed();
        st.sorted(comparator).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    /**
     * Sorts a map by a function on its values. Adapted from http://stackoverflow.com/a/2581754/1901037
     *
     * @return A map that, when iterated over, returns keys, values or entries sorted by value
     */
    @Nonnull
    public static <K, V> LinkedHashMap<K, V> sortMapByValue(Map<K, V> map, Function<V, Comparable> function, boolean ascending)
    {
        LinkedHashMap<K,V> result = new LinkedHashMap<>();

        if (map == null)
            return result;

        Stream<Map.Entry<K,V>> entries = map.entrySet().stream();
        Comparator<Map.Entry<K, V>> comparator = Comparator.comparing(
                (Function<Map.Entry<K, V>, Comparable>) (kvEntry) -> function.apply(kvEntry.getValue())
        );
        if ( ! ascending)
            comparator = comparator.reversed();
        entries.sorted(comparator).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    /**
     * Sorts a Map by a function on its value, descendingly. Can handle NA.
     *
     * <p>Example usage:</p>
     * <pre>
     *  Map&lt;ItemId, ContentBasedScore&gt; sortedcontentBasedScores =
     *      sortMap(recommendation.contentBasedScores, cbs -> cbs.contentBased);
     * </pre>
     * @return New hash map, sorted.
     */
    @Nonnull
    public static <K, V> LinkedHashMap<K, V> sortMap(Map<K, V> map, Function<V, Double> function)
    {
        LinkedHashMap<K,V> result = new LinkedHashMap<>();

        if (map == null)
            return result;

        Comparator<Map.Entry<K, V>> comparator =
                Comparator.comparing(
                        (Function<Map.Entry<K, V>, Double>) (kvEntry) -> {
                            Double value = function.apply(kvEntry.getValue());
                            if (!isValue(value))
                                value = Double.MIN_VALUE;
                            return value;
                        }
                ).reversed();
        Stream<Map.Entry<K,V>> entries = map.entrySet().stream();
        entries.sorted(comparator).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    /**
     * Sorts a Map by a function on its value, ascendingly.
     *
     * <p>Example usage:</p>
     * <pre>
     *  Map&lt;ItemId, ContentBasedScore&gt; sortedcontentBasedScores =
     *      sortMap(recommendation.contentBasedScores, cbs -> cbs.contentBasedScore);
     * </pre>
     *
     * @return List of keys
     */
    @Nonnull
    public static <K,V> List<K> sortMapByValueFunctionAscending(Map<K, V> map, Function<V, Double> function)
    {
        if (map == null)
            return emptyList();

        Comparator<Map.Entry<K, V>> comparator =
                Comparator.comparingDouble(e -> getValueOr(function.apply(e.getValue()), Double.MIN_VALUE));
        return map.entrySet().stream()
                .sorted(comparator)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Sorts a Map by a function on its value, descendingly.
     *
     * <p>Example usage:</p>
     * <pre>
     *  Map&lt;ItemId, ContentBasedScore&gt; sortedcontentBasedScores =
     *      sortMap(recommendation.contentBasedScores, cbs -> cbs.contentBasedScore);
     * </pre>
     *
     * @return List of keys
     */
    @Nonnull
    public static <K,V> List<K> sortMapByValueFunctionDescending(Map<K, V> map, Function<V, Double> function)
    {
        if (map == null)
            return emptyList();

        Comparator<Map.Entry<K, V>> comparator =
                Comparator.comparingDouble(e -> getValueOr(function.apply(e.getValue()), Double.MIN_VALUE));
        return map.entrySet().stream()
                .sorted(comparator.reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static <T> List<Multiset.Entry<T>> sortMultisetByCounts(Multiset<T> multiSet)
    {
        return multiSet.entrySet().stream().sorted((e1, e2) -> e2.getCount() - e1.getCount()).collect(Collectors.toList());
    }

    /**
     * Wrapper around Apache Commons CollectionUtils.isEqualCollection that accepts nulls and considers null equal to
     * an empty collection.
     */
    public static boolean isEqualCollection(Collection<?> a, Collection<?> b)
    {
        if (IterableUtils.isEmpty(a) != IterableUtils.isEmpty(b))
            return false;
        if (IterableUtils.isEmpty(a) && IterableUtils.isEmpty(b))
            return true;
        return CollectionUtils.isEqualCollection(a, b);
    }
}

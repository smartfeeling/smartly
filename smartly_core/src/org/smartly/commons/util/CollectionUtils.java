package org.smartly.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.Map.Entry;


public abstract class CollectionUtils {

    //---------------------------------------------------------------------
    // Convenience methods for working with String arrays
    //---------------------------------------------------------------------
    public static int[] resizeArray(int[] array, int newsize) {
        if (null == array) {
            return null;
        }
        int[] newArr = new int[newsize];
        if (newsize > array.length) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        } else {
            System.arraycopy(array, 0, newArr, 0, newsize);
        }

        return newArr;
    }

    public static String[] resizeArray(String[] array, int newsize) {
        if (null == array) {
            return null;
        }
        String[] newArr = new String[newsize];
        if (newsize > array.length) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        } else {
            System.arraycopy(array, 0, newArr, 0, newsize);
        }

        return newArr;
    }

    public static Object[] resizeArray(Object[] array, int newsize) {
        if (null == array) {
            return null;
        }
        Object[] newArr = new Object[newsize];
        if (newsize > array.length) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        } else {
            System.arraycopy(array, 0, newArr, 0, newsize);
        }

        return newArr;
    }

    /**
     * Append the given String to the given String array, returning a new array
     * consisting of the input array contents plus the given String.
     *
     * @param array the array to append to (can be
     *              <code>null</code>)
     * @param str   the String to append
     * @return the new array (never
     *         <code>null</code>)
     */
    public static String[] addStringToArray(final String[] array, final String str) {
        if (isEmpty(array)) {
            return new String[]{str};
        }
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = str;
        return newArr;
    }

    /**
     * Insert the given String to begin of the given String array, returning a
     * new array consisting of the input array contents plus the given String.
     *
     * @param array the array to insert to (can be
     *              <code>null</code>)
     * @param str   the String to insert in first position
     * @return the new array (never
     *         <code>null</code>)
     */
    public static String[] insertStringToArray(String[] array, String str) {
        if (isEmpty(array)) {
            return new String[]{str};
        }
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 1, array.length);
        newArr[0] = str;
        return newArr;
    }

    public static String[] removeTokenFromArray(String[] array, int index) {
        if (null == array || isEmpty(array)) {
            return new String[0];
        }
        String[] newArr = new String[array.length - 1];
        int newArrIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (index != i) {
                newArr[newArrIndex] = array[i];
                newArrIndex++;
            }
        }
        return newArr;
    }

    /**
     * Turn given source String array into sorted array.
     *
     * @param array the source array
     * @return the sorted array (never
     *         <code>null</code>)
     */
    public static String[] sortStringArray(String[] array) {
        if (isEmpty(array)) {
            return new String[0];
        }
        Arrays.sort(array);
        return array;
    }

    /**
     * Remove duplicate Strings from the given array. Also sorts the array, as
     * it uses a TreeSet.
     *
     * @param array the String array
     * @return an array without duplicates, in natural sort order
     */
    public static String[] removeDuplicateStrings(final String[] array) {
        if (isEmpty(array)) {
            return array;
        }
        final Set<String> set = new TreeSet<String>();
        Collections.addAll(set, array);
        return (String[]) set.toArray(new String[set.size()]);
    }

    /**
     * Take an array Strings and split each element based on the given
     * delimiter. A
     * <code>Properties</code> instance is then generated, with the left of the
     * delimiter providing the key, and the right of the delimiter providing the
     * value. <p>Will trim both the key and value before adding them to the
     * <code>Properties</code> instance.
     *
     * @param array     the array to process
     * @param delimiter to split each element using (typically the equals
     *                  symbol)
     * @return a
     *         <code>Properties</code> instance representing the array contents, or
     *         <code>null</code> if the array to process was null or empty
     */
    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
        return splitArrayElementsIntoProperties(array, delimiter, null);
    }

    /**
     * Take an array Strings and split each element based on the given
     * delimiter. A
     * <code>Properties</code> instance is then generated, with the left of the
     * delimiter providing the key, and the right of the delimiter providing the
     * value. <p>Will trim both the key and value before adding them to the
     * <code>Properties</code> instance.
     *
     * @param array         the array to process
     * @param delimiter     to split each element using (typically the equals
     *                      symbol)
     * @param charsToDelete one or more characters to remove from each element
     *                      prior to attempting the split operation (typically the quotation mark
     *                      symbol), or
     *                      <code>null</code> if no removal should occur
     * @return a
     *         <code>Properties</code> instance representing the array contents, or
     *         <code>null</code> if the array to process was null or empty
     */
    public static Properties splitArrayElementsIntoProperties(
            String[] array, String delimiter, String charsToDelete) {

        if (array == null || array.length == 0) {
            return null;
        }

        Properties result = new Properties();
        for (final String item : array) {
            String element = item;
            if (charsToDelete != null) {
                element = StringUtils.deleteAny(item, charsToDelete);
            }
            String[] splittedElement = StringUtils.splitFirst(element, delimiter);
            if (splittedElement == null) {
                continue;
            }
            result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
        }
        return result;
    }

    public static Map<String, String> splitArrayElementsIntoMap(
            final String[] array, final String delimiter) {
        return splitArrayElementsIntoMap(array, delimiter, null);
    }

    public static Map<String, String> splitArrayElementsIntoMap(
            String[] array, String delimiter, String charsToDelete) {

        if (array == null || array.length == 0) {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();
        for (final String item : array) {
            String element = item;
            if (charsToDelete != null) {
                element = StringUtils.deleteAny(item, charsToDelete);
            }
            String[] splittedElement = StringUtils.splitFirst(element, delimiter);
            if (splittedElement == null) {
                continue;
            }
            result.put(splittedElement[0].trim(), splittedElement[1].trim());
        }
        return result;
    }

    public static String toString(final Collection list) {
        final StringBuilder sb = new StringBuilder();
        if (null != list) {
            for (final Object item : list) {
                if (null != item) {
                    sb.append(item);
                }
            }
        }
        return sb.toString();
    }

    public static String toString(final Object[] list) {
        final StringBuilder sb = new StringBuilder();
        if (null != list) {
            for (final Object item : list) {
                if (null != item) {
                    sb.append(item);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Convenience method to convert a CSV string list to a set. Note that this
     * will suppress duplicates.
     *
     * @param str CSV String
     * @return a Set of String entries in the list
     */
    public static Set<String> commaDelimitedListToSet(final String str) {
        final Set<String> set = new TreeSet<String>();
        final String[] tokens = StringUtils.split(str, ",");
        Collections.addAll(set, tokens);
        return set;
    }

    public static String arrayToString(final Object[] arr) {
        if (arr == null) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (final Object item : arr) {
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a String array as a delimited (e.g. CSV)
     * String. E.g. useful for toString() implementations.
     *
     * @param arr   array to display. Elements may be of any type (toString will
     *              be called on each element).
     * @param delim delimiter to use (probably a ",")
     */
    public static String toDelimitedString(final Object[] arr,
                                           final String delim) {
        if (arr == null) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String toDelimitedString(final Object[] arr,
                                           final String delim,
                                           int startIndex, int endIndex) {
        if (arr == null) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        endIndex = arr.length - 1 < endIndex ? arr.length - 1 : endIndex;
        for (int i = startIndex; i < endIndex + 1; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String collectionToString(Collection<?> list) {
        if (null == list) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final Object item : list) {
            if (null != item) {
                sb.append(item.toString());
            }
        }
        return sb.toString();
    }

    public static int[] toIntArray(final List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static long[] toLongArray(final List<Long> list) {
        long[] array = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for toString() implementations.
     *
     * @param coll   Collection to display
     * @param delim  delimiter to use (probably a ",")
     * @param prefix string to start each element with
     * @param suffix string to end each element with
     */
    public static String tooDelimitedString(final Collection coll,
                                            final String delim,
                                            final String prefix,
                                            final String suffix) {
        if (coll == null) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        Iterator it = coll.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(prefix).append(it.next()).append(suffix);
            i++;
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for toString() implementations.
     *
     * @param coll  Collection to display
     * @param delim delimiter to use (probably a ",")
     */
    public static String toDelimitedString(final Collection coll,
                                           final String delim) {
        return tooDelimitedString(coll, delim, "", "");
    }

    /**
     * Convenience method to return a String array as a CSV String. E.g. useful
     * for toString() implementations.
     *
     * @param arr array to display. Elements may be of any type (toString will
     *            be called on each element).
     */
    public static String toCommaDelimitedString(final Object[] arr) {
        return toDelimitedString(arr, ",");
    }

    /**
     * Convenience method to return a Collection as a CSV String. E.g. useful
     * for toString() implementations.
     *
     * @param coll Collection to display
     */
    public static String toCommaDelimitedString(final Collection coll) {
        return toDelimitedString(coll, ",");
    }

    public static String mapToString(final Map map) {
        if (null == map) {
            return "";
        }
        final String text = map.toString();
        return text.substring(1, text.length() - 1);
    }

    public static <T, E> String mapToString(final Map<T, E> map,
                                            final String separator) {
        if (null == map) {
            return "";
        }
        if (null == separator) {
            return mapToString(map);
        }
        final StringBuilder result = new StringBuilder();
        final Set<Entry<T, E>> entries = map.entrySet();
        for (final Entry entry : entries) {
            final String name = StringUtils.toString(entry.getKey(), "");
            final String value = StringUtils.toString(entry.getValue(), "");
            if (StringUtils.hasText(name)) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(name).append("=").append(value);
            }
        }
        return result.toString();
    }

    public static Map<String, String> stringToMapOfStrings(final String data) {
        return stringToMapOfStrings(data, ",");
    }

    public static Map<String, String> stringToMapOfStrings(final String data, String separator) {
        if (StringUtils.hasText(data)) {
            if (".".equals(separator) || "|".equals(separator)) {
                separator = "\\" + separator;
            }
            final String[] tokens = data.split(separator);
            return toMapOfStrings(tokens);
        }
        return new HashMap<String, String>();
    }

    public static Map<String, String> toMapOfStrings(final String[] tokens) {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        if (null != tokens) {
            for (String token : tokens) {
                final String[] items = token.split("=");
                final String key;
                final String value;
                if (items.length == 1) {
                    key = items[0].trim();
                    value = "";
                } else if (items.length == 2) {
                    key = items[0].trim();
                    value = items[1].trim();
                } else {
                    continue;
                }
                result.put(key, value);
            }
        }
        return result;
    }

    public static Map<String, Object> stringToMap(final String data) {
        return stringToMap(data, ",");
    }

    public static Map<String, Object> stringToMap(final String data, String separator) {
        if (StringUtils.hasText(data)) {
            if (".".equals(separator) || "|".equals(separator)) {
                separator = "\\" + separator;
            }
            final String[] tokens = data.split(separator);
            return toMap(tokens);
        }
        return new HashMap<String, Object>();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(final String[] tokens) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (null != tokens) {
            for (String token : tokens) {
                final String[] items = token.split("=");
                final String key;
                final String value;
                if (items.length == 1) {
                    key = items[0].trim();
                    value = "";
                } else if (items.length == 2) {
                    key = items[0].trim();
                    value = items[1].trim();
                } else {
                    continue;
                }
                if (result.containsKey(key)) {
                    final List<Object> list;
                    final Object val = result.get(key);
                    if (val instanceof List) {
                        list = (List) val;
                    } else {
                        list = new LinkedList<Object>();
                        list.add(val);
                        result.put(key, list);
                    }
                    list.add(value);
                } else {
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    public static Object[] subtract(final Object[] valuestoremove,
                                    final Object[] targetarray) {
        final List<Object> result = new LinkedList<Object>();
        for (final Object value : targetarray) {
            if (!contains(valuestoremove, value)) {
                result.add(value);
            }
        }
        return result.toArray(new Object[result.size()]);
    }

    public static String[] subtract(final String[] valuestoremove,
                                    final String[] targetarray) {
        final List<String> result = new LinkedList<String>();
        for (final String value : targetarray) {
            if (!contains(valuestoremove, value)) {
                result.add(value);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Return new array containig all common fields in both arrays
     *
     * @param targetvalues First Array
     * @param checkvalues  Second Array
     * @return
     */
    public static String[] match(final String[] targetvalues,
                                 final String[] checkvalues) {
        final List<String> result = new LinkedList<String>();
        for (final String value : checkvalues) {
            if (contains(targetvalues, value)) {
                result.add(value);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Return new array containig all common fields in both arrays
     *
     * @param targetvalues First list
     * @param checkvalues  Second list
     * @return
     */
    public static <T> Collection<T> match(
            final Collection<T> targetvalues,
            final Collection<T> checkvalues) {
        final List<T> result = new LinkedList<T>();
        for (final T value : checkvalues) {
            if (targetvalues.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public static boolean contains(final Collection list, final String fieldName,
                                   final Object fieldValue) {
        return indexOf(list, fieldName, fieldValue) > -1;
    }

    public static boolean contains(final Object[] list, final String fieldName,
                                   final Object fieldValue) {
        return indexOf(list, fieldName, fieldValue) > -1;
    }

    public static boolean contains(final Collection list, Object value) {
        return null != list && list.size() > 0 && list.contains(value);
    }

    public static boolean contains(Class[] array, Class value) {
        return null != array && indexOf(array, value) >= 0;
    }

    public static boolean contains(char[] array, char value) {
        return null != array && indexOf(array, value) >= 0;
    }

    public static boolean contains(Object[] array, Object value) {
        return null != array && indexOf(array, value) >= 0;
    }

    public static boolean containsLike(Class[] array, Class value) {
        return null != array && indexOfLike(array, value) >= 0;
    }

    /**
     * Searches the specified array for the specified object using the binary
     * search algorithm. The array is sorted automatically. (If the array
     * contains elements that are not mutually comparable (for example, strings
     * and integers), it <i>cannot</i> be sorted according to the natural
     * ordering of its elements, hence results are undefined.) If the array
     * contains multiple elements equal to the specified object, there is no
     * guarantee which one will be found.
     *
     * @param a   the array to be searched
     * @param key the value to be searched for
     * @return index of the search key, if it is contained in the array;
     *         otherwise, <tt>(- 1)</tt>. The <i>insertion point</i> is defined as the
     *         point at which the key would be inserted into the array: the index of the
     *         first element greater than the key, or <tt>a.length</tt> if all elements
     *         in the array are less than the specified key. Note that this guarantees
     *         that the return value will be &gt;= 0 if and only if the key is found.
     * @throws ClassCastException if the search key is not comparable to the
     *                            elements of the array.
     */
    public static int binarySearch(Object[] a, Object key) {
        if (null == a || a.length == 0) {
            return -1;
        }
        Arrays.sort(a);
        final int index = Arrays.binarySearch(a, key);
        return index < 0 ? -1 : index;
    }

    public static int indexOf(Class[] a, Class key) {
        if (null == a || a.length == 0 || null == key) {
            return -1;
        }
        for (int i = 0; i < a.length; i++) {
            final Class item = a[i];
            if (key.equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(char[] a, char key) {
        if (null == a || a.length == 0) {
            return -1;
        }
        for (int i = 0; i < a.length; i++) {
            final char item = a[i];
            if (key == item) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(Object[] a, Object key) {
        if (null == a || a.length == 0 || null == key) {
            return -1;
        }
        for (int i = 0; i < a.length; i++) {
            final Object item = a[i];
            if (key.equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(final Object[] list, final String fieldName,
                              final Object fieldValue) {
        if (!isEmpty(list)) {
            int counter = 0;
            for (final Object item : list) {
                final Object value = BeanUtils.getValueIfAny(item, fieldName);
                if (CompareUtils.equals(value, fieldValue)) {
                    return counter;
                }
                counter++;
            }
        }
        return -1;
    }

    public static int indexOf(final Collection list, final String fieldName,
                              final Object fieldValue) {
        if (!isEmpty(list)) {
            final Iterator iterator = list.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                final Object item = iterator.next();
                final Object value = BeanUtils.getValueIfAny(item, fieldName);
                if (CompareUtils.equals(value, fieldValue)) {
                    return counter;
                }
                counter++;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static int indexOfLike(Class[] a, Class key) {
        if (null == a || a.length == 0 || null == key) {
            return -1;
        }
        for (int i = 0; i < a.length; i++) {
            final Class item = a[i];
            // is 'item' equal or super-class of 'key'?
            if (key.equals(item) || item.isAssignableFrom(key)) {
                return i;
            }
        }
        return -1;
    }

    public static Map merge(final Map... args) {
        Map result = null;
        for (final Map map : args) {
            if (null != map) {
                if (null == result) {
                    result = map;
                } else {
                    result.putAll(map);
                }
            }
        }
        return result;
    }

    /**
     * Combine a variable number of Arrays, returning a single array containing
     * all (not null) values of passed arrays.
     *
     * @param arrays A variable number of arrays
     * @return A single array, sum of passed parameters.
     */
    public static Class[] merge(Class[]... arrays) {
        List<Class> result = new LinkedList<Class>();
        if (null != arrays && arrays.length > 0) {
            for (Class[] array : arrays) {
                if (null != array) {
                    for (final Class item : array) {
                        if (null != item) {
                            result.add(item);
                        }
                    }
                }
            }
        }
        return result.toArray(new Class[result.size()]);
    }

    /**
     * Combine a variable number of Arrays, returning a single array containing
     * all (not null) values of passed arrays.
     *
     * @param arrays A variable number of arrays
     * @return A single array, sum of passed parameters.
     */
    public static Object[] merge(Object[]... arrays) {
        List<Object> result = new LinkedList<Object>();
        if (null != arrays && arrays.length > 0) {
            for (Object[] array : arrays) {
                if (null != array) {
                    for (final Object item : array) {
                        if (null != item) {
                            result.add(item);
                        }
                    }
                }
            }
        }
        return result.toArray(new Object[result.size()]);
    }

    /**
     * Combine a variable number of Collections.<br> Duplicates are not added to
     * result list, and order is mantained.
     *
     * @param collections Variable number of collections
     * @return LinkedList containig all collections items.
     */
    public static Collection<?> mergeNoDuplicates(Collection<?>... collections) {
        final Collection<Object> result = new LinkedList<Object>();
        if (null != collections && collections.length > 0) {
            for (final Collection<?> coll : collections) {
                if (!result.contains(coll)) {
                    result.add(coll);
                }
            }
        }
        return result;
    }

    public static <T> Collection<T> add(final Collection<T> collection,
                                        final T item) {
        if (null != item) {
            if (null != collection) {
                collection.add(item);
            }
        }
        return collection;
    }

    public static void add(final Collection<String> collection,
                           final String value,
                           final int minLength,
                           final boolean trim,
                           final boolean allowDuplicates) {
        add(collection, value, minLength, trim, allowDuplicates, null);
    }

    public static void add(final Collection<String> collection,
                           final String value,
                           final int minLenght,
                           final boolean trim,
                           final boolean allowDuplicates,
                           final String[] excludes) {
        if (null != value) {
            if (null != collection) {
                if (StringUtils.hasLength(value, minLenght)) {
                    if (!allowDuplicates) {
                        if (!collection.contains(value)) {
                            add(collection, value, trim, excludes);
                        }
                    } else {
                        add(collection, value, trim, excludes);
                    }

                }
            }
        }
    }

    public static void add(final Collection<String> collection,
                           final String value,
                           final boolean trim,
                           final String[] excludes) {
        if (isEmpty(excludes)) {
            collection.add(trim ? value.trim() : value);
        } else if (!contains(excludes, value)) {
            collection.add(trim ? value.trim() : value);
        }
    }

    /**
     * Add item if not null and if does not exists in list
     *
     * @param <T>
     * @param collection
     * @param item
     * @return
     */
    public static <T> Collection<T> addNoDuplicates(final Collection<T> collection,
                                                    final T item) {
        if (null != item) {
            if (null != collection) {
                if (!collection.contains(item)) {
                    collection.add(item);
                }
            }
        }
        return collection;
    }

    /**
     * Add items to a collection avoiding dupicates.
     *
     * @param <T>        Type of Objects
     * @param collection Collection to add items to.
     * @param items      Items to add to collection
     * @return Collection with all items
     */
    public static <T> Collection<T> addAllNoDuplicates(final Collection<T> collection,
                                                       final Collection<T> items) {
        if (null != items) {
            if (null != collection) {
                for (T item : items) {
                    if (!collection.contains(item)) {
                        collection.add(item);
                    }
                }
            }
        }
        return collection;
    }

    public static <T> Collection<T> addAllNoDuplicates(final Collection<T> collection,
                                                       final T[] items) {
        if (null != items) {
            if (null != collection) {
                for (final T item : items) {
                    if (!collection.contains(item)) {
                        collection.add(item);
                    }
                }
            }
        }
        return collection;
    }

    public static <T> Collection<T> addAll(final Collection<T> collection,
                                           final T[] items) {
        if (null != items) {
            if (null != collection) {
                Collections.addAll(collection, items);
            }
        }
        return collection;
    }

    public static <T> Set<List<T>> cartesianProduct(
            final Collection<Collection<T>> sets) {
        if (sets.size() < 2) {
            throw new IllegalArgumentException(
                    "Can't have a product of fewer than two sets (got "
                            + sets.size() + ")");
        }

        return _cartesianProduct(0, sets);
    }

    public static <T> Set<Map<String, T>> cartesianProduct(
            final Map<String, Collection<T>> mapOfColl) {
        if (mapOfColl.size() < 2) {
            throw new IllegalArgumentException(
                    "Can't have a product of fewer than two sets (got "
                            + mapOfColl.size() + ")");
        }

        return _cartesianProduct(0, mapOfColl);
    }

    public static Set<List<Object>> cartesianProduct(Collection<?>... sets) {
        if (sets.length < 2) {
            throw new IllegalArgumentException(
                    "Can't have a product of fewer than two sets (got "
                            + sets.length + ")");
        }

        return _cartesianProduct(0, sets);
    }

    public static Set<List<Object>> cartesianProduct(Object[]... arrays) {
        if (arrays.length < 2) {
            throw new IllegalArgumentException(
                    "Can't have a product of fewer than two sets (got "
                            + arrays.length + ")");
        }

        return _cartesianProduct(0, arrays);
    }

    public static Object[] toArray(final Object item) {
        if (null != item) {
            if (item.getClass().isArray()) {
                return (Object[]) item;
            }
            final List<?> result = toList(item);
            return result.toArray(new Object[result.size()]);
        }
        return new Object[0];
    }

    public static String[] toArrayOfString(final Iterator<?> item) {
        final List<String> result = new LinkedList<String>();
        while (item.hasNext()) {
            final Object val = item.next();
            if (null != val) {
                result.add(val.toString());
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Convert Array, JSONObject, Map, into list
     *
     * @param item
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List toList(final Object item) {
        final List result = new LinkedList();
        try {
            if (null != item) {
                if (item.getClass().isArray()) {
                    CollectionUtils.addAll(result, (Object[]) item);
                } else if (item instanceof Collection) {
                    result.addAll((Collection) item);
                } else if (item instanceof Map) {
                    final Collection values = ((Map) item).values();
                    result.addAll(values);
                } else if (item instanceof JSONObject) {
                    final Iterator<String> keys = ((JSONObject) item).keys();
                    while (keys.hasNext()) {
                        result.add(((JSONObject) item).opt(keys.next()));
                    }
                } else if (item instanceof JSONArray) {
                    final JSONArray array = (JSONArray) item;
                    for (int i = 0; i < array.length(); i++) {
                        result.add(array.get(i));
                    }
                } else if (item instanceof Iterator) {
                    final Iterator keys = ((Iterator) item);
                    while (keys.hasNext()) {
                        result.add(keys.next());
                    }
                } else {
                    result.add(item);
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static <T> List<T> toList(T... args) {
        return Arrays.asList(args);
    }

    public static boolean isEmpty(final Object variant) {
        if (null == variant) {
            return true;
        }

        if (variant instanceof Collection) {
            return isEmpty((Collection) variant);
        } else if (variant instanceof Map) {
            return isEmpty((Map) variant);
        } else if (variant.getClass().isArray()) {
            return isEmpty((Object[]) variant);
        }

        return false;
    }

    /**
     * Return whether the given array is empty: that is, null or of zero length.
     *
     * @param array the array to check
     */
    public static boolean isEmpty(final Object[] array) {
        return CollectionUtils.size(array) == 0;
    }

    /**
     * Return whether the given collection is empty: that is, null or of zero
     * length.
     *
     * @param collection the collection to check
     */
    public static boolean isEmpty(final Collection collection) {
        return CollectionUtils.size(collection) == 0;
    }

    /**
     * Return whether the given map is empty: that is, null or of zero length.
     *
     * @param map the map to check
     */
    public static boolean isEmpty(final Map map) {
        return CollectionUtils.size(map) == 0;
    }

    /**
     * Check if all items of current collection are assignable to passed class
     *
     * @param list   Collection to check
     * @param aclass Class
     * @return True if all items in collection are assignable to passed class
     */
    public static boolean isListOf(final Collection<?> list, final Class aclass) {
        if (null == list) {
            return false;
        }
        for (final Object item : list) {
            if (null == item || !BeanUtils.isAssignable(item, aclass)) {
                return false;
            }
        }
        return true;
    }

    public static int size(final Object[] array) {
        return null != array
                ? array.length
                : 0;
    }

    public static int size(final Collection collection) {
        return null != collection
                ? collection.size()
                : 0;
    }

    public static int size(final Map map) {
        return null != map
                ? map.size()
                : 0;
    }

    /**
     * Return token at index, or null if index is greater than tokens.
     *
     * @param commaseparated A comma separated string. i.e. "1,2,3,4,5"
     * @param index          Index of value in string.
     * @return Null or token value. i.e. s=getToken("a,b,c,d,e", 1); // s=='b'
     */
    public static String getToken(final String commaseparated,
                                  final int index) {
        return getToken(commaseparated, ",", index, null);
    }

    /**
     * Return token at index, or null if index is greater than tokens.
     *
     * @param delimitedString A delimited string. i.e. "a:s:d:f:v"
     * @param separator       the delimiter. i.e. ":", ",", "|", etc..
     * @param index           Index of value in string.
     * @return Null or token value. i.e. s=getToken("a:b:c:d:e", ":", 1); //
     *         s=='b'
     */
    public static String getToken(final String delimitedString,
                                  final String separator, final int index) {
        return getToken(delimitedString, separator, index, null);
    }

    /**
     * Return token at index, or null if index is greater than tokens.
     *
     * @param delimitedString A delimited string. i.e. "a:s:d:f:v"
     * @param separator       the delimiter. i.e. ":", ",", "|", etc..
     * @param index           Index of value in string.
     * @param defaultValue    Default value if result is null
     * @return Null or token value. i.e. s=getToken("a:b:c:d:e", ":", 1); //
     *         s=='b'
     */
    public static String getToken(final String delimitedString,
                                  final String separator,
                                  final int index,
                                  final String defaultValue) {
        final String[] array = StringUtils.split(delimitedString,
                separator,
                true);
        final String result = get(array, index);
        return null != result ? result : defaultValue;
    }

    public static <T> T get(T[] array, int index) {
        if (array.length < index + 1) {
            return null;
        }
        return array[index];
    }

    public static <T> T get(final T[] array, final int index,
                            final T defaultValue) {
        if (array.length < index + 1) {
            return defaultValue;
        }
        final T result = array[index];
        return null != result ? result : defaultValue; // array[index];
    }

    public static <T> T get(final Collection<T> collection, final int index) {
        if (collection.size() < index + 1) {
            return null;
        }
        int i = 0;
        for (T item : collection) {
            if (i == index) {
                return item;
            }
            i++;
        }
        return null;
    }

    /**
     * Return first item of an array. NULL if array is empty or is null.
     */
    public static <T> T getFirst(T[] array) {
        if (!isEmpty(array)) {
            return array[0];
        }

        return null;
    }

    /**
     * Return first item of a collection. NULL if collection is empty or is
     * null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        } else {
            return collection.iterator().next();
        }
    }

    /**
     * Return last item of an array. NULL if array is empty or is null.
     */
    public static <T> T getLast(final T[] array) {
        if (!isEmpty(array)) {
            return array[array.length - 1];
        }
        return null;
    }

    public static String getLast(final String delimitedText, final String delimiter) {
        final String[] tokens = StringUtils.split(delimitedText, delimiter);
        if (!isEmpty(tokens)) {
            return getLast(tokens);
        }
        return "";
    }

    /**
     * Return last item of a collection. NULL if collection is empty or has no
     * items.<br> To retrieve last item, iterate entire collection.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        } else {
            T result = null;
            Iterator<T> iterator = collection.iterator();
            while (iterator.hasNext()) {
                result = iterator.next();
            }
            return result;
        }
    }

    /**
     * Retrieve item in list by value of its property.
     *
     * @param properyName   KEY property
     * @param propertyValue VALUE of KEY property
     * @return NULL or retrieved item.
     */
    public static <T> T getByBeanProperty(
            final Collection<T> list, final String properyName,
            final Object propertyValue) {
        if (StringUtils.hasText(properyName)) {
            final Iterator<T> iterator = list.iterator();
            while (iterator.hasNext()) {
                final T item = iterator.next();
                final Object itemValue = CollectionUtils._getValue(item, properyName);
                if (null != itemValue && itemValue.equals(propertyValue)) {
                    return item;
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static Object _getValue(final Object item, final String propertyName) {
        try {
            return BeanUtils.getValue(item, propertyName);
        } catch (Exception ignored) {
        }
        return null;
    }

    private static <T> Set<List<T>> _cartesianProduct(int index,
                                                      final Collection<Collection<T>> sets) {
        final Set<List<T>> ret = new LinkedHashSet<List<T>>();
        if (index == sets.size()) {
            ret.add(new LinkedList<T>());
        } else {
            for (final T obj : get(sets, index)) {
                for (final List<T> set : _cartesianProduct(index + 1, sets)) {
                    set.add(0, obj);
                    ret.add(set);
                }
            }
        }
        return ret;
    }

    private static <T> Set<Map<String, T>> _cartesianProduct(int index,
                                                             final Map<String, Collection<T>> sets) {
        final Set<Map<String, T>> ret = new LinkedHashSet<Map<String, T>>();
        if (index == sets.size()) {
            ret.add(new LinkedHashMap<String, T>());
        } else {
            final Collection<T> setOfValues = get(sets.values(), index);
            final String keyOfValues = get(sets.keySet(), index);
            for (final T obj : setOfValues) {
                final Set<Map<String, T>> setOfMaps = _cartesianProduct(index + 1, sets);
                for (final Map<String, T> set : setOfMaps) {
                    set.put(keyOfValues, obj);
                    ret.add(set);
                }
            }
        }
        return ret;
    }

    private static Set<List<Object>> _cartesianProduct(int index,
                                                       Collection<?>... sets) {
        final Set<List<Object>> ret = new LinkedHashSet<List<Object>>();
        if (index == sets.length) {
            ret.add(new LinkedList<Object>());
        } else {
            for (final Object obj : sets[index]) {
                for (final List<Object> set : _cartesianProduct(index + 1, sets)) {
                    set.add(0, obj);
                    ret.add(set);
                }
            }
        }
        return ret;
    }

    private static Set<List<Object>> _cartesianProduct(int index,
                                                       Object[]... arrays) {
        final Set<List<Object>> ret = new LinkedHashSet<List<Object>>();
        if (index == arrays.length) {
            ret.add(new LinkedList<Object>());
        } else {
            for (final Object obj : arrays[index]) {
                for (final List<Object> set : _cartesianProduct(index + 1, arrays)) {
                    set.add(0, obj);
                    ret.add(set);
                }
            }
        }

        return ret;
    }
}

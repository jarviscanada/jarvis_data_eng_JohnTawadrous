package ca.jrvs.apps.grep;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface LambdaStreamExc {

    /**
     * Create a String stream from array
     *
     * note: arbitrary number of value will be stored in an array
     *
     * @param strings variable number of strings
     * @return a stream of strings
     */
    Stream<String> createStrStream(String ... strings);

    /**
     * Convert all strings to uppercase
     * please use createStrStream
     *
     * @param strings variable number of strings
     * @return a stream of strings in uppercase
     */
    Stream<String> toUpperCase(String ... strings);

    /**
     * Filter strings that contain the pattern
     * e.g. filter(stringStream, "a") will return another stream which no element contains "a"
     *
     * @param stringStream input string stream
     * @param pattern the pattern to filter by
     * @return a filtered stream of strings
     */
    Stream<String> filter(Stream<String> stringStream, String pattern);

    /**
     * Create an IntStream from an array
     *
     * @param arr input array of ints
     * @return an IntStream
     */
    IntStream createIntStream(int[] arr);

    /**
     * Convert a stream to a list
     *
     * @param stream input stream
     * @param <E> type of elements in the stream
     * @return a list of elements
     */
    <E> List<E> toList(Stream<E> stream);

    /**
     * Convert an IntStream to a list of integers
     *
     * @param intStream input IntStream
     * @return a list of integers
     */
    List<Integer> toList(IntStream intStream);

    /**
     * Create an IntStream range from start to end inclusive
     *
     * @param start the start of the range
     * @param end the end of the range
     * @return an IntStream
     */
    IntStream createIntStream(int start, int end);

    /**
     * Convert an IntStream to a DoubleStream
     * and compute the square root of each element
     *
     * @param intStream input IntStream
     * @return a DoubleStream of square roots
     */
    DoubleStream squareRootIntStream(IntStream intStream);

    /**
     * Filter all even numbers and return odd numbers from an IntStream
     *
     * @param intStream input IntStream
     * @return an IntStream of odd numbers
     */
    IntStream getOdd(IntStream intStream);

    /**
     * Return a lambda function that prints a message with a prefix and suffix
     * This lambda can be useful to format logs
     *
     * @param prefix prefix string
     * @param suffix suffix string
     * @return a Consumer that prints formatted messages
     */
    Consumer<String> getLambdaPrinter(String prefix, String suffix);

    /**
     * Print each message with a given printer
     *
     * @param messages array of messages to print
     * @param printer Consumer to print messages
     */
    void printMessages(String[] messages, Consumer<String> printer);

    /**
     * Print all odd numbers from an IntStream.
     *
     * @param intStream input IntStream
     * @param printer Consumer to print messages
     */
    void printOdd(IntStream intStream, Consumer<String> printer);

    /**
     * Square each number from the input.
     * Please write two solutions and compare the difference
     *   - using flatMap
     *
     * @param ints stream of lists of integers
     * @return stream of squared integers
     */
    Stream<Integer> flatNestedInt(Stream<List<Integer>> ints);
}

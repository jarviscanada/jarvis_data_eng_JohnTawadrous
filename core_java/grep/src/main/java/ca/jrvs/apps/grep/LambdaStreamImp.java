package ca.jrvs.apps.grep;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStreamImp implements LambdaStreamExc{

    @Override
    public Stream<String> createStrStream(String... strings) {
        return Arrays.stream(strings);
    }

    @Override
    public Stream<String> toUpperCase(String... strings) {
        return createStrStream(strings).map(String::toUpperCase);
    }

    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {
        return stringStream.filter(s -> s.contains(pattern));
    }

    @Override
    public IntStream createIntStream(int[] arr) {
        return Arrays.stream(arr);
    }

    @Override
    public <E> List<E> toList(Stream<E> stream) {
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<Integer> toList(IntStream intStream) {
        return intStream.boxed().collect(Collectors.toList());
    }

    @Override
    public IntStream createIntStream(int start, int end) {
        return IntStream.rangeClosed(start, end);
    }

    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {
        return intStream.mapToDouble(Math::sqrt);
    }

    @Override
    public IntStream getOdd(IntStream intStream) {
        return intStream.filter(n -> n % 2 != 0);
    }

    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {
        return s -> System.out.println(prefix + s + suffix);
    }

    @Override
    public void printMessages(String[] messages, Consumer<String> printer) {
        Arrays.stream(messages).forEach(printer);
    }

    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {
        getOdd(intStream).forEach(n -> printer.accept(String.valueOf(n)));
    }

    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
        return ints.flatMap(List::stream).map(n -> n * n);
    }

    public static void main(String[] args) {
        LambdaStreamImp lambdaStreamImp = new LambdaStreamImp();

        // Test createStrStream
        Stream<String> strStream = lambdaStreamImp.createStrStream("hello", "world");
        strStream.forEach(System.out::println);

        // Test toUpperCase
        Stream<String> upperStream = lambdaStreamImp.toUpperCase("hello", "world");
        upperStream.forEach(System.out::println);

        // Test filter
        Stream<String> filteredStream = lambdaStreamImp.filter(lambdaStreamImp.createStrStream("hello", "world", "java"), "o");
        filteredStream.forEach(System.out::println);

        // Test createIntStream
        IntStream intStream = lambdaStreamImp.createIntStream(new int[]{1, 2, 3});
        intStream.forEach(System.out::println);

        // Test toList with Stream
        List<String> list = lambdaStreamImp.toList(lambdaStreamImp.createStrStream("hello", "world"));
        System.out.println(list);

        // Test toList with IntStream
        List<Integer> intList = lambdaStreamImp.toList(lambdaStreamImp.createIntStream(new int[]{1, 2, 3}));
        System.out.println(intList);

        // Test createIntStream range
        IntStream rangeStream = lambdaStreamImp.createIntStream(1, 5);
        rangeStream.forEach(System.out::println);

        // Test squareRootIntStream
        DoubleStream sqrtStream = lambdaStreamImp.squareRootIntStream(lambdaStreamImp.createIntStream(new int[]{1, 4, 9}));
        sqrtStream.forEach(System.out::println);

        // Test getOdd
        IntStream oddStream = lambdaStreamImp.getOdd(lambdaStreamImp.createIntStream(new int[]{1, 2, 3, 4, 5}));
        oddStream.forEach(System.out::println);

        // Test getLambdaPrinter
        Consumer<String> printer = lambdaStreamImp.getLambdaPrinter("start>", "<end");
        printer.accept("Message body");

        // Test printMessages
        lambdaStreamImp.printMessages(new String[]{"a", "b", "c"}, lambdaStreamImp.getLambdaPrinter("msg:", "!"));

        // Test printOdd
        lambdaStreamImp.printOdd(lambdaStreamImp.createIntStream(0, 5), lambdaStreamImp.getLambdaPrinter("odd number:", "!"));

        // Test flatNestedInt
        Stream<List<Integer>> nestedIntStream = Stream.of(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6));
        Stream<Integer> flatIntStream = lambdaStreamImp.flatNestedInt(nestedIntStream);
        flatIntStream.forEach(System.out::println);
    }
}

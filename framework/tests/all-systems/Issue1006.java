// Test case for Issue 1006:
// https://github.com/typetools/checker-framework/issues/1006

// @skip-test // until issue is fixed

// @below-java8-jdk-skip-test

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Issue1006 {
    void foo(Stream<String> m, Map<String, Integer> im) {
        Map<String, Integer> l = m.collect(Collectors.toMap(Function.identity(), im::get));
    }

    // alternative version with same crash
    Map<String, Long> bar(String src) {
        return Stream.of(src)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
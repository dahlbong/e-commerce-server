package kr.hhplus.be.server.supporters.key;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kr.hhplus.be.server.supporters.key.KeyConstant.SEPERATOR;

public interface KeyGeneratable {

    KeyType type();

    List<String> namespaces();

    default String generate() {
        return Stream.concat(
                Stream.of(type().getKey()),
                namespaces().stream()
        ).collect(Collectors.joining(SEPERATOR));
    }
}
package com.github.dima00782.parser;

import org.antlr.v4.runtime.CharStream;

public interface Parser {
    Iterable<Command> parse(CharStream charStreams);
}

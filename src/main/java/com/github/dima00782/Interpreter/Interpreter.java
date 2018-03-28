package com.github.dima00782.Interpreter;

import com.github.dima00782.parser.Command;

public interface Interpreter {
    void run(Iterable<Command> commands);
}

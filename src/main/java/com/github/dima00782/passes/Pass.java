package com.github.dima00782.passes;

import com.github.dima00782.parser.Command;

public interface Pass {
    Iterable<Command> run(Iterable<Command> commands);
}

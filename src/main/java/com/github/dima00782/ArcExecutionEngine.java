package com.github.dima00782;

import com.github.dima00782.interpreter.ArcInterpreter;
import com.github.dima00782.interpreter.Dumper;
import com.github.dima00782.interpreter.Interpreter;
import com.github.dima00782.parser.Command;
import com.github.dima00782.parser.Parser;
import com.github.dima00782.parser.VisitorBasedParser;
import com.github.dima00782.passes.LiveRangeSetter;
import com.github.dima00782.passes.Pass;
import org.antlr.v4.runtime.CharStream;

public class ArcExecutionEngine {
    public void execute(CharStream stream, Dumper dumper) {
        Parser parser = new VisitorBasedParser();
        Interpreter interpreter = new ArcInterpreter();
        Pass liveRangeSetter = new LiveRangeSetter();

        Iterable<Command> commands = liveRangeSetter.run(parser.parse(stream));
        interpreter.run(commands, dumper);
    }
}

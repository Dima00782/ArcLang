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
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CharStream charStream = CharStreams.fromFileName(args[0]);
        Parser parser = new VisitorBasedParser();
        Interpreter interpreter = new ArcInterpreter();
        Pass liveRangeSetter = new LiveRangeSetter();

        Iterable<Command> commands = liveRangeSetter.run(parser.parse(charStream));
        interpreter.run(commands, new Dumper() {
            @Override
            public synchronized void dump(String string) {
                System.out.println(string);
            }
        });
    }
}

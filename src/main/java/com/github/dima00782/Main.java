package com.github.dima00782;

import com.github.dima00782.interpreter.Dumper;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CharStream charStream = CharStreams.fromFileName(args[0]);
        ArcExecutionEngine engine = new ArcExecutionEngine();
        engine.execute(charStream, new Dumper() {
            @Override
            public synchronized void dump(String string) {
                System.out.println(string);
            }
        });
    }
}

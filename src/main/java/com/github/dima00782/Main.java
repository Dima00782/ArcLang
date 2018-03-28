package com.github.dima00782;

import com.github.dima00782.Interpreter.ArcInterpreter;
import com.github.dima00782.Interpreter.Interpreter;
import com.github.dima00782.parser.Parser;
import com.github.dima00782.parser.VisitorBasedParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CharStream charStream = CharStreams.fromFileName(args[0]);
        Parser parser = new VisitorBasedParser();
        Interpreter interpreter = new ArcInterpreter();
        interpreter.run(parser.parse(charStream));
    }
}

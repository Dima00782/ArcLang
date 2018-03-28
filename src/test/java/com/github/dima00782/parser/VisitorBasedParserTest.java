package com.github.dima00782.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class VisitorBasedParserTest {
    private static Parser parser;

    @BeforeClass
    public static void initParser() {
        parser = new VisitorBasedParser();
    }

    private static ArrayList<Command> fromIterable(Iterable<Command> iterable) {
        ArrayList<Command> list = new ArrayList<>();
        for (Command comm : iterable) {
            list.add(comm);
        }
        return list;
    }

    @Test
    public void testAssignments() {
        CharStream charStream = CharStreams.fromString("a = object");
        ArrayList<Command> commands = fromIterable(parser.parse(charStream));

        assertEquals(1, commands.size());
        assertEquals(Opcode.DEF_REF, commands.get(0).getOpcode());
    }

    @Test
    public void testSleeps() {
        CharStream charStream = CharStreams.fromString("sleep");
        ArrayList<Command> commands = fromIterable(parser.parse(charStream));

        assertEquals(1, commands.size());
        assertEquals(Opcode.SLEEP, commands.get(0).getOpcode());
    }
}

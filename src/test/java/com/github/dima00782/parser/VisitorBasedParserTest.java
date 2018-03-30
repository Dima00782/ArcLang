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

    private static void testOneParam(ArrayList<Command> commands, Opcode opcode, String name) {
        assertEquals(1, commands.size());

        Command command = commands.get(0);
        assertEquals(opcode, command.getOpcode());
        assertEquals(1, command.argsSize());
        assertEquals(name, command.getArg(0));
    }

    private static void testTwoParam(ArrayList<Command> commands, Opcode opcode, String lhsName, String rhsName) {
        assertEquals(1, commands.size());

        Command command = commands.get(0);
        assertEquals(opcode, command.getOpcode());
        assertEquals(2, command.argsSize());
        assertEquals(lhsName, command.getArg(0));
        assertEquals(rhsName, command.getArg(1));
    }

    private static void testNoParam(ArrayList<Command> commands, Opcode opcode) {
        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals(0, command.argsSize());
        assertEquals(opcode, command.getOpcode());
    }

    @Test
    public void testAssignments() {
        CharStream charStream = CharStreams.fromString("a = object");
        ArrayList<Command> commands = fromIterable(parser.parse(charStream));
        testTwoParam(commands, Opcode.DEF_REF, "a", "object");

        charStream = CharStreams.fromString("a ~= b");
        commands = fromIterable(parser.parse(charStream));
        testTwoParam(commands, Opcode.DEF_WREF, "a", "b");
    }

    @Test
    public void testSleeps() {
        CharStream charStream = CharStreams.fromString("sleep");
        testNoParam(fromIterable(parser.parse(charStream)), Opcode.SLEEP);

        charStream = CharStreams.fromString("sleepr");
        testNoParam(fromIterable(parser.parse(charStream)), Opcode.SLEEPR);
    }

    @Test
    public void testDump() {
        CharStream charStream = CharStreams.fromString("dump a");
        testOneParam(fromIterable(parser.parse(charStream)), Opcode.DUMP, "a");
    }

    @Test
    public void testThread() {
        CharStream charStream = CharStreams.fromString("thread {}");
        testNoParam(fromIterable(parser.parse(charStream)), Opcode.THREAD);
    }
}

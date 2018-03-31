package com.github.dima00782.interpreter;

import com.github.dima00782.parser.Command;
import com.github.dima00782.parser.Opcode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ArcInterpreterTest {
    private static Interpreter interpreter;

    @BeforeClass
    public static void initParser() {
        interpreter = new ArcInterpreter();
    }

    private static String runInterpreter(ArrayList<Command> commands) {
        StringBuilder sb = new StringBuilder();
        interpreter.run(commands, new Dumper() {
            @Override
            public synchronized void dump(String string) {
                sb.append(string).append(" ");
            }
        });

        return sb.toString();
    }

    @Test
    public void testDeref() {
        ArrayList<Command> commands = new ArrayList<>(Arrays.asList(
                new Command(Opcode.DEF_REF, new Object[] {"a", "object"}),
                new Command(Opcode.DEREF, new Object[] {"a"}),
                new Command(Opcode.DUMP, new Object[] {"a"})
        ));
        assertEquals("null ", runInterpreter(commands));
    }

    @Test
    public void testDump() {
        ArrayList<Command> commands = new ArrayList<>(Arrays.asList(
                new Command(Opcode.DUMP, new Object[] {"a"}),
                new Command(Opcode.DEF_REF, new Object[] {"a", "object"}),
                new Command(Opcode.DUMP, new Object[] {"a"})
        ));

        assertEquals("null ArcObject{refCount=1, fields={}} ", runInterpreter(commands));
    }

    @Test
    public void testDefRef() {
        ArrayList<Command> commands = new ArrayList<>(Arrays.asList(
                new Command(Opcode.DEF_REF, new Object[] {"a", "object"}),
                new Command(Opcode.DEF_REF, new Object[] {"b", "a"}),
                new Command(Opcode.DUMP, new Object[] {"a"})
        ));

        assertEquals("ArcObject{refCount=2, fields={}} ", runInterpreter(commands));
    }

    @Test
    public void testDefWRef() {
        ArrayList<Command> commands = new ArrayList<>(Arrays.asList(
                new Command(Opcode.DEF_REF, new Object[] {"a", "object"}),
                new Command(Opcode.DEF_WREF, new Object[] {"b", "a"}),
                new Command(Opcode.DUMP, new Object[] {"a"})
        ));

        assertEquals("ArcObject{refCount=1, fields={}} ", runInterpreter(commands));
    }

    @Test
    public void testThread() {
        ArrayList<Command> commands = new ArrayList<>(Arrays.asList(
                new Command(Opcode.THREAD, new Object[] {
                        new Command(Opcode.DEF_REF, new Object[] {"a", "object"}),
                        new Command(Opcode.DUMP, new Object[] {"a"}),
                        new Command(Opcode.DEREF, new Object[] {"a"})
                }),
                new Command(Opcode.SLEEP, null),
                new Command(Opcode.DUMP, new Object[] {"a"})
        ));

        assertEquals("ArcObject{refCount=1, fields={}} null ", runInterpreter(commands));
    }
}

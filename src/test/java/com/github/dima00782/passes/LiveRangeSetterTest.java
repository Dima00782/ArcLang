package com.github.dima00782.passes;

import com.github.dima00782.parser.Command;
import com.github.dima00782.parser.Opcode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LiveRangeSetterTest {
    private static LiveRangeSetter pass;

    @BeforeClass
    public static void initParser() {
        pass = new LiveRangeSetter();
    }

    private static ArrayList<Command> runPass(ArrayList<Command> commands) {
        ArrayList<Command> list = new ArrayList<>();
        for (Command comm : pass.run(commands)) {
            list.add(comm);
        }
        return list;
    }

    @Test
    public void testCaptureDeref() {
        ArrayList<Command> commands = new ArrayList<>(Arrays.asList(
                new Command(Opcode.DEF_REF, new Object[] {"a", "object"}),
                new Command(Opcode.THREAD, new Object[] {
                        new Command(Opcode.DEF_WREF, new Object[] {"b", "a"}),
                })
        ));

        ArrayList<Command> expected = new ArrayList<>(Arrays.asList(
                new Command(Opcode.DEF_REF, new Object[] {"a", "object"}),
                new Command(Opcode.CAPTURE, new Object[] {"a"}),
                new Command(Opcode.THREAD, new Object[] {
                        new Command(Opcode.DEF_WREF, new Object[] {"b", "a"}),
                        new Command(Opcode.DEREF, new Object[] {"a"}),
                })
        ));

        assertEquals(expected, runPass(commands));
    }
}

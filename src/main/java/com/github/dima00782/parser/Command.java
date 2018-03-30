package com.github.dima00782.parser;

import java.util.Arrays;

public final class Command {
    private final Opcode opcode;
    private final Object[] args;

    public Command(Opcode opcode, Object[] args) {
        this.opcode = opcode;
        this.args = args;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public int argsSize() {
        if (args == null) {
            return 0;
        }
        return args.length;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getArg(int idx) {
        return args[idx];
    }

    @Override
    public String toString() {
        return "Command{" +
                "opcode=" + opcode +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

package com.github.dima00782.parser;

import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return opcode == command.opcode &&
                Arrays.equals(args, command.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(opcode);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}

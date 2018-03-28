package com.github.dima00782.parser;

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

    public Object getArg(int idx) {
        return args[idx];
    }
}

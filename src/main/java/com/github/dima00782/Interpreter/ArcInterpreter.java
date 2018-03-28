package com.github.dima00782.Interpreter;

import com.github.dima00782.parser.Command;

public class ArcInterpreter implements Interpreter {
    @Override
    public void run(Iterable<Command> commands) {
        for (Command command : commands) {
            if (command == null) {
                continue;
            }
            switch (command.getOpcode()) {
                case DEF_REF: {
                    System.out.println("DEF_REF");
                    break;
                }
                case DEF_WREF: {
                    System.out.println("DEF_WREF");
                    break;
                }
                case THREAD: {
                    System.out.println("THREAD");
                    break;
                }
                case SLEEP: {
                    System.out.println("SLEEP");
                    break;
                }
                case SLEEPR: {
                    System.out.println("SLEEPR");
                    break;
                }
                case DUMP: {
                    System.out.println("DUMP");
                    break;
                }
            }
        }
    }
}

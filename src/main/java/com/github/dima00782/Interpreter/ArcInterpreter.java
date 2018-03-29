package com.github.dima00782.Interpreter;

import com.github.dima00782.parser.Command;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ArcInterpreter implements Interpreter {
    private static final int SLEEP_INTERVAL = 100;
    private static final int SLEEPR_RANGE_START = 10;
    private static final int SLEEPR_RANGE_END = 100;

    @Override
    public void run(Iterable<Command> commands) {
        for (Command command : commands) {
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
                    Command[] threadCommands = Arrays.copyOf(command.getArgs(), command.argsSize(), Command[].class);
                    Thread thread = new Thread(() -> ArcInterpreter.this.run(Arrays.asList(threadCommands)));
                    thread.start();
                    break;
                }
                case SLEEP: {
                    System.out.println("SLEEP");
                    try {
                        Thread.sleep(SLEEP_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case SLEEPR: {
                    System.out.println("SLEEPR");
                    int sleepTime = ThreadLocalRandom.current().nextInt(SLEEPR_RANGE_START, SLEEPR_RANGE_END);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

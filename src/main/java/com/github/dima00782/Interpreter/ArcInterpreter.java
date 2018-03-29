package com.github.dima00782.Interpreter;

import com.github.dima00782.parser.Command;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ArcInterpreter implements Interpreter {
    private static final int SLEEP_INTERVAL = 100;
    private static final int SLEEPR_RANGE_START = 10;
    private static final int SLEEPR_RANGE_END = 100;

    private final ArcObject scopeObject = new ArcObject();

    private Pair<String, ArcObject> lookupObjectByName(String name) {
        String[] fields = name.split("\\.");
        ArcObject object = scopeObject;
        for (int i = 0; i + 1 < fields.length; ++i) {
            object = object.getField(fields[i]);
        }

        return new Pair<>(fields[fields.length - 1], object);
    }

    @Override
    public void run(Iterable<Command> commands) {
        for (Command command : commands) {
            switch (command.getOpcode()) {
                case DEF_REF: {
                    System.out.println("DEF_REF " + command.getArg(0) + " " + command.getArg(1));

                    String lhs = (String) command.getArg(0);
                    String rhs = (String) command.getArg(1);

                    if ("object".equals(rhs)) {
                        Pair<String, ArcObject> result = lookupObjectByName(lhs);
                        lhs = result.getKey();
                        ArcObject objectToInsert = result.getValue();
                        objectToInsert.addFiled(lhs, new ArcObject());
                    } else {
                        Pair<String, ArcObject> lhsSearchResult = lookupObjectByName(lhs);
                        lhs = lhsSearchResult.getKey();
                        ArcObject objectToInsert = lhsSearchResult.getValue();

                        Pair<String, ArcObject> rhsSearchResult = lookupObjectByName(rhs);
                        ArcObject rhsObject = rhsSearchResult.getValue();
                        rhs = rhsSearchResult.getKey();
                        ArcObject targetObject = rhsObject.getField(rhs);

                        targetObject.incrementRefCount();
                        objectToInsert.addFiled(lhs, targetObject);
                    }
                    break;
                }
                case DEF_WREF: {
                    System.out.println("DEF_WREF " + command.getArg(0) + " " + command.getArg(1));
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
                    System.out.print("DUMP " + command.getArg(0) + " ");
                    String lhs = (String) command.getArg(0);
                    if ("all".equals(lhs)) {
                        System.out.println(scopeObject);
                    } else {
                        Pair<String, ArcObject> result = lookupObjectByName(lhs);
                        lhs = result.getKey();
                        System.out.println(result.getValue().getField(lhs));
                    }
                    break;
                }
            }
        }
    }
}

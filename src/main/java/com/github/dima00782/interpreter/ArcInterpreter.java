package com.github.dima00782.interpreter;

import com.github.dima00782.parser.Command;
import com.github.dima00782.parser.Opcode;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class ArcInterpreter implements Interpreter {
    private static final int SLEEP_INTERVAL = 100;
    private static final int SLEEPR_RANGE_START = 10;
    private static final int SLEEPR_RANGE_END = 100;
    private static final Logger LOGGER = Logger.getLogger(ArcInterpreter.class);

    private final ArcObject scopeObject = new ArcObject();

    private Pair<String, ArcObject> lookupObjectByName(String name) {
        String[] fields = name.split("\\.");
        ArcObject object = scopeObject;
        for (int i = 0; i + 1 < fields.length; ++i) {
            object = object.getField(fields[i]);
        }

        return new Pair<>(fields[fields.length - 1], object);
    }

    private void sleep(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleAssignment(String lhs, String rhs, boolean isWeak) {
        LOGGER.info((!isWeak ? "DEF_REF " : "DEF_WREF ") + lhs + " " + rhs);
        Pair<String, ArcObject> lhsSearchResult = lookupObjectByName(lhs);
        if ("object".equals(rhs) && !isWeak) {
            lhs = lhsSearchResult.getKey();
            ArcObject objectToInsert = lhsSearchResult.getValue();
            objectToInsert.addFiled(lhs, new ArcObject());
        } else {
            lhs = lhsSearchResult.getKey();
            ArcObject objectToInsert = lhsSearchResult.getValue();

            Pair<String, ArcObject> rhsSearchResult = lookupObjectByName(rhs);
            ArcObject rhsObject = rhsSearchResult.getValue();
            rhs = rhsSearchResult.getKey();
            ArcObject targetObject = rhsObject.getField(rhs);

            if (!isWeak) {
                targetObject.incrementRefCount();
            }
            objectToInsert.addFiled(lhs, targetObject);
        }
    }

    @Override
    public void run(Iterable<Command> commands) {
        for (Command command : commands) {
            switch (command.getOpcode()) {
                case DEF_REF:
                case DEF_WREF: {
                    String lhs = (String) command.getArg(0);
                    String rhs = (String) command.getArg(1);
                    handleAssignment(lhs, rhs, command.getOpcode() == Opcode.DEF_WREF);
                    break;
                }
                case DEREF: {
                    String[] names = Arrays.copyOf(command.getArgs(), command.argsSize(), String[].class);
                    LOGGER.info("DEREF " + String.join(",", names));

                    IntStream.range(0, command.argsSize()).forEach(i -> {
                        int refCount = scopeObject.decrement(names[i]);
                        if (refCount == 0) {
                            scopeObject.removeField(names[i]);
                        }
                    });
                    break;
                }
                case CAPTURE: {
                    String[] names = Arrays.copyOf(command.getArgs(), command.argsSize(), String[].class);
                    LOGGER.info("CAPTURE " + String.join(",", names));
                    IntStream.range(0, command.argsSize()).forEach(i -> scopeObject.getField(names[i]).incrementRefCount());
                    break;
                }
                case THREAD: {
                    LOGGER.info("THREAD");
                    Command[] threadCommands = Arrays.copyOf(command.getArgs(), command.argsSize(), Command[].class);
                    Thread thread = new Thread(() -> ArcInterpreter.this.run(Arrays.asList(threadCommands)));
                    thread.start();
                    break;
                }
                case SLEEP: {
                    LOGGER.info("SLEEP");
                    sleep(SLEEP_INTERVAL);
                    break;
                }
                case SLEEPR: {
                    LOGGER.info("SLEEPR");
                    int sleepTime = ThreadLocalRandom.current().nextInt(SLEEPR_RANGE_START, SLEEPR_RANGE_END);
                    sleep(sleepTime);
                    break;
                }
                case DUMP: {
                    LOGGER.info("DUMP " + command.getArg(0));
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

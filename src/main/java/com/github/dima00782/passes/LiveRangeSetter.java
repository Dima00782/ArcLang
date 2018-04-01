package com.github.dima00782.passes;

import com.github.dima00782.parser.Command;
import com.github.dima00782.parser.Opcode;

import java.util.*;

public class LiveRangeSetter implements Pass {
    private String first(String name) {
        return name.split("\\.")[0];
    }

    private Set<String> getUseSet(Command[] commands) {
        Set<String> useSet = new HashSet<>();
        Arrays.stream(commands).forEach(current -> {
            if (current.getOpcode() == Opcode.DEF_REF) {
                String lhs = first((String) current.getArg(0));
                String rhs = first((String) current.getArg(1));
                useSet.add(lhs);
                if (!"object".equals(rhs)) {
                    useSet.add(rhs);
                }
            } else if (current.getOpcode() == Opcode.DEF_WREF) {
                String rhs = first((String) current.getArg(1));
                useSet.add(rhs);
            }
        });

        return useSet;
    }

    private Map<Integer, ArrayList<String>> reversedMap(Map<String, Integer> nameToFirstNotUseIdx) {
        Map<Integer, ArrayList<String>> idxToNames = new HashMap<>();
        nameToFirstNotUseIdx.forEach((name, value) -> {
            int idx = value;
            if (idxToNames.containsKey(idx)) {
                idxToNames.get(idx).add(name);
            } else {
                ArrayList<String> list = new ArrayList<>();
                list.add(name);
                idxToNames.put(idx, list);
            }
        });

        return idxToNames;
    }

    private void handleThreadCommand(Command thread, List<Command> list, Set<String> useSet) {
        list.add(new Command(Opcode.CAPTURE, useSet.toArray()));
        Object[] newThreadCommands = new Object[thread.argsSize() + 1];
        System.arraycopy(thread.getArgs(), 0, newThreadCommands, 0, thread.argsSize());
        newThreadCommands[thread.argsSize()] = new Command(Opcode.DEREF, useSet.toArray());
        list.add(new Command(Opcode.THREAD, newThreadCommands));
    }

    @Override
    public Iterable<Command> run(Iterable<Command> commands) {
        Map<String, Integer> nameToFirstNotUseIdx = new HashMap<>();
        int currentId = 0;
        List<Set<String>> useSetForThread = new ArrayList<>();
        for (Command command : commands) {
            if (command.getOpcode() == Opcode.DEF_REF
                    || command.getOpcode() == Opcode.DEF_WREF) {
                String lhs = first((String) command.getArg(0));
                String rhs = first((String) command.getArg(1));

                if (command.getOpcode() != Opcode.DEF_WREF) {
                    nameToFirstNotUseIdx.put(lhs, currentId + 1);
                }

                if (!"object".equals(rhs)) {
                    nameToFirstNotUseIdx.put(rhs, currentId + 1);
                }
            } else if (command.getOpcode() == Opcode.THREAD) {
                Command[] threadCommands = Arrays.copyOf(command.getArgs(), command.argsSize(), Command[].class);
                Set<String> useSet = getUseSet(threadCommands);
                for (String name : useSet) {
                    nameToFirstNotUseIdx.put(name, currentId + 1);
                }
                useSetForThread.add(useSet);
            }
            ++currentId;
        }

        Map<Integer, ArrayList<String>> idxToNames = reversedMap(nameToFirstNotUseIdx);

        currentId = 0;
        int currentThreadId = 0;
        List<Command> list = new ArrayList<>();
        for (Command command : commands) {
            List<String> names = idxToNames.get(currentId);
            if (names != null) {
                list.add(new Command(Opcode.DEREF, names.toArray()));
            }

            if (command.getOpcode() == Opcode.THREAD) {
                handleThreadCommand(command, list, useSetForThread.get(currentThreadId));
                ++currentThreadId;
            } else {
                list.add(command);
            }

            ++currentId;
        }

        List<String> names = idxToNames.get(currentId);
        if (names != null) {
            list.add(new Command(Opcode.DEREF, names.toArray()));
        }

        return list;
    }
}

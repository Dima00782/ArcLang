package com.github.dima00782.passes;

import com.github.dima00782.parser.Command;
import com.github.dima00782.parser.Opcode;

import java.util.*;

public class LiveRangeSetter implements Pass {
    private String first(String name) {
        return name.split("\\.")[0];
    }

    private Command handleThreadCommand(Command thread) {
        Command[] threadCommands = Arrays.copyOf(thread.getArgs(), thread.argsSize(), Command[].class);
        Set<String> useSet = new HashSet<>();
        Arrays.stream(threadCommands).forEach(current -> {
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

        ArrayList<Object> newCommands = new ArrayList<>();
        newCommands.add(new Command(Opcode.CAPTURE, useSet.toArray()));
        newCommands.addAll(Arrays.asList(threadCommands));
        newCommands.add(new Command(Opcode.DEREF, useSet.toArray()));

        return new Command(Opcode.THREAD, newCommands.toArray());
    }

    @Override
    public Iterable<Command> run(Iterable<Command> commands) {
        Map<String, Integer> nameToFirstNotUseIdx = new HashMap<>();
        int currentId = 0;
        for (Command command : commands) {
            if (command.getOpcode() == Opcode.DEF_REF
                    || command.getOpcode() == Opcode.DEF_WREF) {
                String lhs = first((String) command.getArg(0));
                String rhs = first((String) command.getArg(1));

                nameToFirstNotUseIdx.put(lhs, currentId + 1);

                if (!"object".equals(rhs)) {
                    nameToFirstNotUseIdx.put(rhs, currentId + 1);
                }
            }
            ++currentId;
        }

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

        currentId = 0;
        List<Command> list = new ArrayList<>();
        for (Command command : commands) {
            ArrayList<String> names = idxToNames.get(currentId);
            if (names != null) {
                list.add(new Command(Opcode.DEREF, names.toArray()));
            }

            if (command.getOpcode() == Opcode.THREAD) {
                command = handleThreadCommand(command);
            }

            list.add(command);
            ++currentId;
        }

        ArrayList<String> names = idxToNames.get(currentId);
        if (names != null) {
            list.add(new Command(Opcode.DEREF, names.toArray()));
        }

        return list;
    }
}

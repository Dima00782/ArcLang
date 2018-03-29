package com.github.dima00782.passes;

import com.github.dima00782.parser.Command;
import com.github.dima00782.parser.Opcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveRangeSetter implements Pass {
    private String first(String name) {
        return name.split("\\.")[0];
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

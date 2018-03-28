package com.github.dima00782.parser;

import com.github.dima00782.ArclangBaseVisitor;
import com.github.dima00782.ArclangLexer;
import com.github.dima00782.ArclangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class VisitorBasedParser implements Parser {
    @Override
    public Iterable<Command> parse(CharStream charStream) {
        ArclangLexer lexer = new ArclangLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        ArclangParser parser = new ArclangParser(tokens);

        ProgramVisitor programVisitor = new ProgramVisitor();
        return programVisitor.visit(parser.prog());
    }

    private static class ProgramVisitor extends ArclangBaseVisitor<Iterable<Command>> {
        @Override
        public Iterable<Command> visitProg(ArclangParser.ProgContext ctx) {
            final InstructionVisitor instructionVisitor = new InstructionVisitor();
            return ctx.instList().instr()
                    .stream()
                    .map(instr -> instr.accept(instructionVisitor))
                    .collect(toList());
        }
    }

    private static class InstructionVisitor extends ArclangBaseVisitor<Command> {
        @Override
        public Command visitSleepRandomOperator(ArclangParser.SleepRandomOperatorContext ctx) {
            return new Command(Opcode.SLEEPR, null);
        }

        @Override
        public Command visitSleepOperator(ArclangParser.SleepOperatorContext ctx) {
            return new Command(Opcode.SLEEP, null);
        }

        @Override
        public Command visitAssignmentoperator(ArclangParser.AssignmentoperatorContext ctx) {
            return null;
        }

        @Override
        public Command visitWeakassignmentoperator(ArclangParser.WeakassignmentoperatorContext ctx) {
            return null;
        }

        @Override
        public Command visitThreadOperator(ArclangParser.ThreadOperatorContext ctx) {
            return null;
        }

        @Override
        public Command visitDumpOperator(ArclangParser.DumpOperatorContext ctx) {
            return null;
        }
    }
}

package com.github.dima00782.parser;

import com.github.dima00782.ArclangBaseVisitor;
import com.github.dima00782.ArclangLexer;
import com.github.dima00782.ArclangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class VisitorBasedParser implements Parser {
    private static final Set<String> reserved;

    static {
        Set<String> reservedTmp = new HashSet<>();
        reservedTmp.add("object");
        reservedTmp.add("sleep");
        reservedTmp.add("sleepr");
        reservedTmp.add("dump");
        reservedTmp.add("thread");
        reserved = Collections.unmodifiableSet(reservedTmp);
    }


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
            String lhs = ctx.lhs().getText();
            String rhs = ctx.rhs().getText();
            if (reserved.contains(lhs) || (reserved.contains(rhs) && !"object".equals(rhs))) {
                throw new ParserException("using reserved word in assignment " + ctx.lhs());
            }
            return new Command(Opcode.DEF_REF, new Object[]{ctx.lhs(), ctx.rhs()});
        }

        @Override
        public Command visitWeakassignmentoperator(ArclangParser.WeakassignmentoperatorContext ctx) {
            if (reserved.contains(ctx.lhs().getText()) || reserved.contains(ctx.rhs().getText())) {
                throw new ParserException("using reserved word in weak assignment " + ctx.lhs());
            }
            return new Command(Opcode.DEF_WREF, new Object[]{ctx.lhs(), ctx.rhs()});
        }

        @Override
        public Command visitThreadOperator(ArclangParser.ThreadOperatorContext ctx) {
            return null;
        }

        @Override
        public Command visitDumpOperator(ArclangParser.DumpOperatorContext ctx) {
            if (reserved.contains(ctx.Identifier().getText())) {
                throw new ParserException("using dump with reserved words " + ctx.Identifier());
            }
            return new Command(Opcode.DUMP, new Object[]{ctx.Identifier().getText()});
        }
    }
}

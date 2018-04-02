package com.github.dima00782;

import com.github.dima00782.interpreter.ArcInterpreter;
import com.github.dima00782.interpreter.Dumper;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import static junit.framework.TestCase.assertEquals;

public class ArcExecutionEngineTest {
    private static ArcExecutionEngine engine;
    private static final Logger LOGGER = Logger.getLogger(ArcInterpreter.class);

    @BeforeClass
    public static void initEngine() {
        engine = new ArcExecutionEngine();
    }

    private static String execute(CharStream charStream) {
        StringBuilder sb = new StringBuilder();
        engine.execute(charStream, new Dumper() {
            @Override
            public synchronized void dump(String string) {
                sb.append(string).append(" ");
            }
        });

        return sb.toString();
    }

    private CharStream getCharStream(String fileName) throws IOException {
        URL url = this.getClass().getResource(fileName);
        return CharStreams.fromStream(new FileInputStream(url.getFile()));
    }

    @Test
    public void testAllCommands() throws IOException {
        CharStream charStream = getCharStream("/all_commands.arc");

        LOGGER.info("RUN: all_commands.arc");
        assertEquals("ArcObject{refCount=1, fields={}} " +
                "ArcObject{refCount=1, fields={" +
                "x=ArcObject{refCount=1, fields={}}=false, " +
                "y=ArcObject{refCount=1, fields={}}=false, " +
                "z=ArcObject{refCount=1, fields={}}=false}} " +
                "null ", execute(charStream));
    }

    @Test(expected = NullPointerException.class)
    public void testException() throws IOException {
        CharStream charStream = getCharStream("/raise_exception.arc");
        execute(charStream);
    }

    @Test
    public void testThreadCapture() throws IOException {
        CharStream charStream = getCharStream("/thread_capture.arc");

        LOGGER.info("RUN: thread_capture.arc");
        assertEquals("ArcObject{refCount=1, fields={}} " +
                "ArcObject{refCount=2, fields={}} " +
                "ArcObject{refCount=2, fields={}} " +
                "ArcObject{refCount=1, fields={}} ",
                execute(charStream));
    }

    @Test
    public void testConcurentChangeOfScope() throws IOException {
        CharStream charStream = getCharStream("/concurrent_change_scope.arc");

        LOGGER.info("RUN: concurrent_change_scope.arc");
        assertEquals("ArcObject{refCount=2, fields={}} " +
                        "ArcObject{refCount=1, fields={x=ArcObject{refCount=1, fields={}}=false}} ",
                execute(charStream));
    }

    @Test(expected = com.github.dima00782.parser.ParserException.class)
    public void testWrefObjectAssignment() throws IOException {
        CharStream charStream = getCharStream("/wref_object_assignment.arc");

        LOGGER.info("RUN: wref_object_assignment.arc");
        execute(charStream);
    }

    @Test
    public void testThreadChain() throws IOException {
        CharStream charStream = getCharStream("/thread_chain.arc");

        LOGGER.info("RUN: thread_chain.arc");
        assertEquals("ArcObject{refCount=1, " +
                        "fields={x=ArcObject{refCount=1, " +
                        "fields={y=ArcObject{refCount=1, " +
                        "fields={z=ArcObject{refCount=1, " +
                        "fields={}}=false}}=false}}=false}} ",
                execute(charStream));
    }

    @Test
    public void testThreadConcurrent() throws IOException {
        CharStream charStream = getCharStream("/thread_concurrent_write.arc");

        LOGGER.info("RUN: thread_concurrent_write.arc");
        assertEquals("ArcObject{refCount=1, fields={" +
                        "x=ArcObject{refCount=1, fields={}}=false, " +
                        "y=ArcObject{refCount=1, fields={}}=false}} ",
                execute(charStream));
    }

    @Test
    public void testReturnFromThread() throws IOException {
        CharStream charStream = getCharStream("/return_from_thread.arc");

        LOGGER.info("RUN: return_from_thread.arc");
        assertEquals("ArcObject{refCount=1, fields={x=ArcObject{refCount=1, fields={}}=false}} ",
                execute(charStream));
    }

    @Test
    public void testAtomicDelete() throws IOException {
        CharStream charStream = getCharStream("/atomic_delete.arc");

        LOGGER.info("RUN: atomic_delete.arc");
        assertEquals("null ",
                execute(charStream));
    }

    @Test
    public void testEmptyFile() throws IOException {
        CharStream charStream = getCharStream("/empty.arc");

        LOGGER.info("RUN: empty.arc");
        assertEquals("", execute(charStream));
    }

    @Test
    public void testThreadCapture2() throws IOException {
        CharStream charStream = getCharStream("/thread_capture2.arc");

        LOGGER.info("RUN: thread_capture.arc");
        assertEquals("ArcObject{refCount=1, fields={}} ", execute(charStream));
    }
}

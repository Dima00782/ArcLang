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

    @Test
    public void testAllCommands() throws IOException {
        URL url = this.getClass().getResource("/all_commands.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

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
        URL url = this.getClass().getResource("/raise_exception.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        execute(charStream);
    }

    @Test
    public void testThreadCapture() throws IOException {
        URL url = this.getClass().getResource("/thread_capture.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: thread_capture.arc");
        assertEquals("ArcObject{refCount=1, fields={}} " +
                "ArcObject{refCount=2, fields={}} " +
                "ArcObject{refCount=2, fields={}} " +
                "ArcObject{refCount=1, fields={}} ",
                execute(charStream));
    }

    @Test
    public void testConcurentChangeOfScope() throws IOException {
        URL url = this.getClass().getResource("/concurrent_change_scope.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: concurrent_change_scope.arc");
        assertEquals("ArcObject{refCount=2, fields={}} " +
                        "ArcObject{refCount=1, fields={x=ArcObject{refCount=1, fields={}}=false}} ",
                execute(charStream));
    }

    @Test(expected = com.github.dima00782.parser.ParserException.class)
    public void testWrefObjectAssignment() throws IOException {
        URL url = this.getClass().getResource("/wref_object_assignment.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: wref_object_assignment.arc");
        execute(charStream);
    }

    @Test
    public void testThreadChain() throws IOException {
        URL url = this.getClass().getResource("/thread_chain.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

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
        URL url = this.getClass().getResource("/thread_concurrent_write.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: thread_concurrent_write.arc");
        assertEquals("ArcObject{refCount=1, fields={" +
                        "x=ArcObject{refCount=1, fields={}}=false, " +
                        "y=ArcObject{refCount=1, fields={}}=false}} ",
                execute(charStream));
    }

    @Test
    public void testReturnFromThread() throws IOException {
        URL url = this.getClass().getResource("/return_from_thread.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: return_from_thread.arc");
        assertEquals("ArcObject{refCount=1, fields={x=ArcObject{refCount=1, fields={}}=false}} ",
                execute(charStream));
    }

    @Test
    public void testAtomicDelete() throws IOException {
        URL url = this.getClass().getResource("/atomic_delete.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: atomic_delete.arc");
        assertEquals("null ",
                execute(charStream));
    }

    @Test
    public void testEmptyFile() throws IOException {
        URL url = this.getClass().getResource("/empty.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: empty.arc");
        assertEquals("", execute(charStream));
    }

    @Test
    public void testThreadCapture2() throws IOException {
        URL url = this.getClass().getResource("/thread_capture2.arc");
        CharStream charStream = CharStreams.fromStream(new FileInputStream(url.getFile()));

        LOGGER.info("RUN: thread_capture.arc");
        assertEquals("ArcObject{refCount=1, fields={}} ", execute(charStream));
    }
}

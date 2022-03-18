package org.fife.rtext.plugins.buildoutput;

import org.fife.io.ProcessRunnerOutputListener;

import java.io.OutputStream;
import java.io.PrintStream;

public abstract class BuildTask {
    abstract public void run() throws Exception;

    private ProcessRunnerOutputListener listener;
    private boolean finished = false;

    public void stdout(String msg) {
        listener.outputWritten(null, msg, true);
    }

    public void stderr(String msg) {
        listener.outputWritten(null, msg, false);
    }

    public void exit(int code) {
        listener.processCompleted(null, code, null);
        finished = true;
    }

    void setListener(ProcessRunnerOutputListener listener) {
        this.listener = listener;
    }

    boolean isFinished() {
        return finished;
    }

    protected PrintStream getOutStream() {
        return new TaskStream(true);
    }

    protected PrintStream getErrStream() {
        return new TaskStream(false);
    }

    private class TaskStream extends PrintStream {
        final boolean stdout;
        TaskStream(boolean stdout) {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            });
            this.stdout = stdout;
        }

        @Override
        public void print(String s) {
            listener.outputWritten(null, s, stdout);
        }

        @Override
        public void println(String s) {
            listener.outputWritten(null, s + "\n", stdout);
        }
    }

}

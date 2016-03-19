package jetbrick.dao.schema.upgrade;

import jetbrick.commons.exception.SystemException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.io.RandomAccessFile;

public class UpgradeLogger {
    private static final String file = "db_upgrade.log";
    private RandomAccessFile out = null;
    private StopWatch stopWatch = new StopWatch();

    public UpgradeLogger() {
        stopWatch.start();
    }

    private void ensureOpen() throws IOException {
        if (out != null) return;

        out = new RandomAccessFile(file, "rw");
        out.seek(out.length());

        println("============================================================\n");
    }

    public void println(String format, Object... args) {
        try {
            ensureOpen();
            out.writeBytes(String.format(format, args));
            out.writeBytes("\n");
        } catch (IOException e) {
            throw SystemException.unchecked(e);
        }
    }

    public void close() {
        if (out != null) {
            stopWatch.stop();

            println(">>>>");
            println(">>>> All Tasks completed! times: %s", stopWatch.toString());
            println("@@@@\n\n");
        }

        IOUtils.closeQuietly(out);
        out = null;
    }
}

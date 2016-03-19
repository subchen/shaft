package shaft.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public final class AuditLogger implements Closeable {
    private final Logger log = LoggerFactory.getLogger(AuditLogger.class);

    public void println(String pattern, Object... args) {
        log.debug(String.format(pattern, args));
    }

    @Override
    public void close() {

    }
}

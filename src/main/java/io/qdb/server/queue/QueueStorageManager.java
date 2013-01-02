package io.qdb.server.queue;

import io.qdb.server.Util;
import io.qdb.server.model.Queue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

/**
 * Keeps track of where we can store queue data (one or more paths on the file system) and allocates storage
 * for queues.
 */
@Singleton
public class QueueStorageManager {

    private final File[] queueDataDirs;

    @Inject
    public QueueStorageManager(@Named("dataDir") String dataDir) throws IOException {
        queueDataDirs = new File[]{new File(dataDir, "queues")};
        for (File dir : queueDataDirs) Util.ensureDirectory(dir);
    }

    /**
     * If q already exists i.e. there is an existing directory for it then return that directory. Otherwise allocate
     * a new directory and return it.
     */
    public File findDir(Queue q) {
        for (File dir : queueDataDirs) {
            File f = new File(dir, q.getId());
            if (f.exists()) return f;
        }
        return new File(allocateDataDir(q), q.getId());
    }

    private File allocateDataDir(Queue q) {
        if (queueDataDirs.length == 1) return queueDataDirs[0];
        long maxUsableSpace = -1;
        File ans = null;
        for (File dir : queueDataDirs) {
            long usableSpace = dir.getUsableSpace();
            if (usableSpace > maxUsableSpace) {
                ans = dir;
                maxUsableSpace = usableSpace;
            }
        }
        return ans;
    }

}

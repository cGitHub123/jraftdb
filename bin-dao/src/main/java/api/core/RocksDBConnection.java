package api.core;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.createDirectories;

/**
 * author caibin
 * date 2021-07-15
 */
public abstract class RocksDBConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocksDBConnection.class);

    protected RocksDB rocksDB;

    public RocksDBConnection(final RocksDBConfiguration configuration) {
        RocksDB.loadLibrary();
        final Options options = new Options().setCreateIfMissing(true);
        options.setMaxFileOpeningThreads(3);
        final String root = System.getProperty("user.dir");
        final String rocksDirectory = root + configuration.url();
        final Path path = Paths.get(rocksDirectory);
        try {
            createDirectories(path);
            rocksDB = RocksDB.open(options, path.toString());
        } catch (final Exception exception) {
            LOGGER.error("Exception occurred during RocksDB initialization. Shutting down application...", exception);
            System.exit(1);
        }

    }
}

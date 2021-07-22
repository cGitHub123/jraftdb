package sqlite;

import api.core.AsyncKVStore;
import api.core.RocksDBConfiguration;
import raft.SqliteOperation;

import java.sql.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteHelper {

    public static RocksDBConfiguration rocksDBConfiguration;

    public static void asyncPut(SqliteOperation.Params kv) {
        //final RocksDBConfiguration rocksDBConfiguration = new RocksDBConfiguration("/src/main/resources/data/repositories", "db");
        final ItemRepository itemRepository = new ItemRepository(rocksDBConfiguration);
        itemRepository.save(kv.getK(), kv.getV());
    }

    public static CompletableFuture<Optional<String>> asyncGet(SqliteOperation.Params kv) {
        //final RocksDBConfiguration rocksDBConfiguration = new RocksDBConfiguration("/src/main/resources/data/repositories", "db");
        final ItemRepository itemRepository = new ItemRepository(rocksDBConfiguration);
        return itemRepository.findByKey(kv.getK());
    }

    public static void asyncDel(SqliteOperation.Params kv) {
        //final RocksDBConfiguration rocksDBConfiguration = new RocksDBConfiguration("/src/main/resources/data/repositories", "db");
        final ItemRepository itemRepository = new ItemRepository(rocksDBConfiguration);
        itemRepository.deleteByKey(kv.getK());
    }

    public static class ItemRepository extends AsyncKVStore<String, String> {

        public ItemRepository(final RocksDBConfiguration configuration) {
            super(configuration);
        }
    }

}



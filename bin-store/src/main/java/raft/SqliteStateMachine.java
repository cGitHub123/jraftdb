package raft;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.error.RaftException;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.alipay.sofa.jraft.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raft.snapshot.SqliteSnapshotFile;
import sqlite.SqliteHelper;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteStateMachine extends StateMachineAdapter {

    private static final Logger LOG        = LoggerFactory.getLogger(SqliteStateMachine.class);

    private final AtomicLong    value      = new AtomicLong(0);

    private final AtomicLong    leaderTerm = new AtomicLong(-1);

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }

    public long getValue() {
        return this.value.get();
    }

    @Override
    public void onApply(final Iterator iter) {
        while (iter.hasNext()) {
            long current = 0;
            SqliteOperation sqliteOperation = null;

            SqliteClosure closure = null;
            if (iter.done() != null) {
                // This task is applied by this node, get value from closure to avoid additional parsing.
                closure = (SqliteClosure) iter.done();
                sqliteOperation = closure.getSqliteOperation();
            } else {
                // Have to parse FetchAddRequest from this user log.
                final ByteBuffer data = iter.getData();
                try {
                    sqliteOperation = SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(
                        data.array(), SqliteOperation.class.getName());
                } catch (final CodecException e) {
                    LOG.error("Fail to decode IncrementAndGetRequest", e);
                }
            }
            if (sqliteOperation != null) {
                switch (sqliteOperation.getOp()) {
                    case SqliteOperation.GET:
                        ResultSet rs = SqliteHelper.get(sqliteOperation.getSql());
                        try {
                            while (rs.next()) {
                                System.out.println("id = " + rs.getInt("id"));
                            }
                        } catch (Exception ex) {

                        }
                        LOG.info("Get value={} at logIndex={}", current, iter.getIndex());
                        break;
                    case SqliteOperation.SET:
                        SqliteHelper.put(sqliteOperation.getSql());
                        break;
                    case SqliteOperation.DEL:
                        SqliteHelper.del(sqliteOperation.getSql());
                        break;
                }

                if (closure != null) {
                    closure.success(current);
                    closure.run(Status.OK());
                }
            }
            iter.next();
        }
    }

    @Override
  public void onSnapshotSave(final SnapshotWriter writer, final Closure done) {
    final long currVal = this.value.get();
    Utils.runInThread(() -> {
      final SqliteSnapshotFile snapshot = new SqliteSnapshotFile(writer.getPath() + File.separator + "data");
      if (snapshot.save(currVal)) {
        if (writer.addFile("data")) {
          done.run(Status.OK());
        } else {
          done.run(new Status(RaftError.EIO, "Fail to add file to writer"));
        }
      } else {
        done.run(new Status(RaftError.EIO, "Fail to save counter snapshot %s", snapshot.getPath()));
      }
    });
  }

    @Override
    public void onError(final RaftException e) {
        LOG.error("Raft error: {}", e, e);
    }

    @Override
    public boolean onSnapshotLoad(final SnapshotReader reader) {
        if (isLeader()) {
            LOG.warn("Leader is not supposed to load snapshot");
            return false;
        }
        if (reader.getFileMeta("data") == null) {
            LOG.error("Fail to find data file in {}", reader.getPath());
            return false;
        }
        final SqliteSnapshotFile snapshot = new SqliteSnapshotFile(reader.getPath() + File.separator + "data");
        try {
            this.value.set(snapshot.load());
            return true;
        } catch (final IOException e) {
            LOG.error("Fail to load snapshot from {}", snapshot.getPath());
            return false;
        }

    }

    @Override
    public void onLeaderStart(final long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);

    }

    @Override
    public void onLeaderStop(final Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

}

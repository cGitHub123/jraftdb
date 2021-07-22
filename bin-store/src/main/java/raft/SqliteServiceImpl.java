package raft;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.StoreEngineHelper;
import com.alipay.sofa.jraft.rhea.options.StoreEngineOptions;
import com.alipay.sofa.jraft.util.BytesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteServiceImpl implements SqliteService {
    private static final Logger LOG = LoggerFactory.getLogger(SqliteServiceImpl.class);

    private final SqliteServer sqliteServer;
    private final Executor      readIndexExecutor;

    public SqliteServiceImpl(SqliteServer sqliteServer) {
        this.sqliteServer = sqliteServer;
        this.readIndexExecutor = createReadIndexExecutor();
    }

    private Executor createReadIndexExecutor() {
        final StoreEngineOptions opts = new StoreEngineOptions();
        return StoreEngineHelper.createReadIndexExecutor(opts.getReadIndexCoreThreads());
    }

    @Override
    public void get(final boolean readOnlySafe, final SqliteClosure closure) {
        if(!readOnlySafe){
            closure.success(getValue());
            closure.run(Status.OK());
            return;
        }

        this.sqliteServer.getNode().readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(Status status, long index, byte[] reqCtx) {
                if(status.isOk()){
                    closure.success(getValue());
                    closure.run(Status.OK());
                    return;
                }
                SqliteServiceImpl.this.readIndexExecutor.execute(() -> {
                    if(isLeader()){
                        LOG.debug("Fail to get value with 'ReadIndex': {}, try to applying to the state machine.", status);
                        applyOperation(SqliteOperation.createGet(), closure);
                    }else {
                        handlerNotLeaderError(closure);
                    }
                });
            }
        });
    }

    @Override
    public void del(final boolean readOnlySafe, final SqliteClosure closure) {
        if(!readOnlySafe){
            closure.success(getValue());
            closure.run(Status.OK());
            return;
        }

        this.sqliteServer.getNode().readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(Status status, long index, byte[] reqCtx) {
                if(status.isOk()){
                    closure.success(getValue());
                    closure.run(Status.OK());
                    return;
                }
                SqliteServiceImpl.this.readIndexExecutor.execute(() -> {
                    if(isLeader()){
                        LOG.debug("Fail to get value with 'ReadIndex': {}, try to applying to the state machine.", status);
                        applyOperation(SqliteOperation.createGet(), closure);
                    }else {
                        handlerNotLeaderError(closure);
                    }
                });
            }
        });
    }

    private boolean isLeader() {
        return this.sqliteServer.getFsm().isLeader();
    }

    private long getValue() {
        return this.sqliteServer.getFsm().getValue();
    }

    private String getRedirect() {
        return this.sqliteServer.redirect().getRedirect();
    }

    @Override
    public void put(final SqliteClosure closure) {
        applyOperation(SqliteOperation.createIncrement(), closure);
    }

    private void applyOperation(final SqliteOperation op, final SqliteClosure closure) {
        if (!isLeader()) {
            handlerNotLeaderError(closure);
            return;
        }

        try {
            closure.setSqliteOperation(op);
            final Task task = new Task();
            task.setData(ByteBuffer.wrap(SerializerManager.getSerializer(SerializerManager.Hessian2).serialize(op)));
            task.setDone(closure);
            this.sqliteServer.getNode().apply(task);
        } catch (CodecException e) {
            String errorMsg = "Fail to encode SqliteOperation";
            LOG.error(errorMsg, e);
            closure.failure(errorMsg, StringUtils.EMPTY);
            closure.run(new Status(RaftError.EINTERNAL, errorMsg));
        }
    }

    private void handlerNotLeaderError(final SqliteClosure closure) {
        closure.failure("Not leader.", getRedirect());
        closure.run(new Status(RaftError.EPERM, "Not leader"));
    }
}

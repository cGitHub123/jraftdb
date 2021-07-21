package raft.rpc;

import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import module.ExecuteRequest;
import raft.SqliteClosure;
import raft.SqliteService;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class PutProcessor implements RpcProcessor<ExecuteRequest> {

    private final SqliteService sqliteService;

    public PutProcessor(SqliteService sqliteService) {
        super();
        this.sqliteService = sqliteService;
    }

    @Override
    public void handleRequest(final RpcContext rpcCtx, final ExecuteRequest request) {
        final SqliteClosure closure = new SqliteClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getValueResponse());
            }
        };

        this.sqliteService.execute(closure);
    }

    @Override
    public String interest() {
        return ExecuteRequest.class.getName();
    }
}

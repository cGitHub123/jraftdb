package raft.rpc;

import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import module.GetRequest;
import raft.SqliteClosure;
import raft.SqliteService;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class GetProcessor implements RpcProcessor<GetRequest> {

    private final SqliteService sqliteService;

    public GetProcessor(SqliteService sqliteService) {
        super();
        this.sqliteService = sqliteService;
    }

    @Override
    public void handleRequest(final RpcContext rpcCtx, final GetRequest request) {
        final SqliteClosure closure = new SqliteClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getValueResponse());
            }
        };
        this.sqliteService.get(request.isReadOnlySafe(), closure);
    }

    @Override
    public String interest() {
        return GetRequest.class.getName();
    }
}

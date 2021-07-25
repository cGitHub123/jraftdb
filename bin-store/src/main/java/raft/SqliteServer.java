package raft;

import api.Test;
import api.core.RocksDBConfiguration;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import org.apache.commons.io.FileUtils;
import raft.rpc.DelProcessor;
import raft.rpc.PutProcessor;
import raft.rpc.GetProcessor;
import raft.rpc.ValueResponse;
import sqlite.SqliteHelper;

import java.io.File;
import java.io.IOException;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteServer {

    private RaftGroupService raftGroupService;
    private Node node;
    private SqliteStateMachine fsm;

    public SqliteServer(final String dataPath, final String groupId, final PeerId serverId,
                        final NodeOptions nodeOptions) throws IOException {
        // 初始化路径
        FileUtils.forceMkdir(new File(dataPath));

        // 这里让 raft RPC 和业务 RPC 使用同一个 RPC server, 通常也可以分开
        final RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());
        // 注册业务处理器
        SqliteService sqliteService = new SqliteServiceImpl(this);
        rpcServer.registerProcessor(new GetProcessor(sqliteService));
        rpcServer.registerProcessor(new PutProcessor(sqliteService));
        rpcServer.registerProcessor(new DelProcessor(sqliteService));
        // 初始化状态机
        this.fsm = new SqliteStateMachine();
        // 设置状态机到启动参数
        nodeOptions.setFsm(this.fsm);
        // 设置存储路径
        // 日志, 必须
        nodeOptions.setLogUri(dataPath + File.separator + "log");
        // 元信息, 必须
        nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta");
        // snapshot, 可选, 一般都推荐
        nodeOptions.setSnapshotUri(dataPath + File.separator + "snapshot");
        // 初始化 raft group 服务框架
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOptions, rpcServer);
        // 启动
        this.node = this.raftGroupService.start();
    }

    public SqliteStateMachine getFsm() {
        return this.fsm;
    }

    public Node getNode() {
        return this.node;
    }

    public RaftGroupService RaftGroupService() {
        return this.raftGroupService;
    }

    /**
     * Redirect request to new leader
     */
    public ValueResponse redirect() {
        final ValueResponse response = new ValueResponse();
        response.setSuccess(false);
        if (this.node != null) {
            final PeerId leader = this.node.getLeaderId();
            if (leader != null) {
                response.setRedirect(leader.toString());
            }
        }
        return response;
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 5) {
            System.out.println("args is not 5");
            System.exit(1);
        }
        final String dataPath = args[0];
        final String groupId = args[1];
        final String serverIdStr = args[2];
        final String initConfStr = args[3];
        SqliteHelper.rocksDBConfiguration = new RocksDBConfiguration(args[4], "db");
        SqliteHelper.itemRepository = new SqliteHelper.ItemRepository(SqliteHelper.rocksDBConfiguration);
        final NodeOptions nodeOptions = new NodeOptions();
        // 为了测试,调整 snapshot 间隔等参数
        // 设置选举超时时间为 1 秒
        nodeOptions.setElectionTimeoutMs(1000);
        // 关闭 CLI 服务。
        nodeOptions.setDisableCli(false);
        // 每隔30秒做一次 snapshot
        nodeOptions.setSnapshotIntervalSecs(30);
        // 解析参数
        final PeerId serverId = new PeerId();
        if (!serverId.parse(serverIdStr)) {
            throw new IllegalArgumentException("Fail to parse serverId:" + serverIdStr);
        }
        final Configuration initConf = new Configuration();
        if (!initConf.parse(initConfStr)) {
            throw new IllegalArgumentException("Fail to parse initConf:" + initConfStr);
        }
        // 设置初始集群配置
        nodeOptions.setInitialConf(initConf);
        // 启动
        final SqliteServer sqliteServer = new SqliteServer(dataPath, groupId, serverId, nodeOptions);
        System.out.println("Started counter server at port:" + sqliteServer.getNode().getNodeId().getPeerId().getPort());
    }
}

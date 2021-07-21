package raft;

import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.InvokeCallback;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import raft.rpc.ExecuteRequest;

import java.util.Scanner;
import java.util.concurrent.Executor;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteClient {

    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Useage : java com.alipay.sofa.jraft.example.counter.SqliteClient {groupId} {conf}");
            System.out.println("Example: java com.alipay.sofa.jraft.example.counter.SqliteClient counter 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
            System.exit(1);
        }
        final String groupId = args[0];
        final String confStr = args[1];

        final Configuration conf = new Configuration();
        if (!conf.parse(confStr)) {
            throw new IllegalArgumentException("Fail to parse conf:" + confStr);
        }

        RouteTable.getInstance().updateConfiguration(groupId, conf);

        final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());

        if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
            throw new IllegalStateException("Refresh leader failed");
        }

        final PeerId leader = RouteTable.getInstance().selectLeader(groupId);
        GetReadLine(cliClientService, leader);
        System.exit(0);

    }

    private static void doRequest(final CliClientServiceImpl cliClientService, final PeerId leader, final String sql) throws RemotingException,
            InterruptedException {
        final ExecuteRequest request = new ExecuteRequest();
        request.setSql(sql);
        cliClientService.getRpcClient().invokeAsync(leader.getEndpoint(), request, new InvokeCallback() {

            @Override
            public void complete(Object result, Throwable err) {
                if (err == null) {
                    System.out.println("incrementAndGet result:" + result);
                } else {
                    err.printStackTrace();
                }
            }

            @Override
            public Executor executor() {
                return null;
            }
        }, 5000);
    }

    private static void GetReadLine(final CliClientServiceImpl cliClientService, final PeerId leader) {
        Scanner scanner = new Scanner(System.in);
        String sql = scanner.nextLine();
        while (true) {
            try {
                doRequest(cliClientService, leader, sql);
            } catch (Exception ex) {

            }
        }
    }
}
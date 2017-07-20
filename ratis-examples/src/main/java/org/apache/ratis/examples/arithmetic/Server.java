package org.apache.ratis.examples.arithmetic;

import org.apache.log4j.Level;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcConfigKeys;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.server.impl.RaftServerImpl;
import org.apache.ratis.statemachine.StateMachine;
import org.apache.ratis.util.LogUtils;

import java.util.stream.IntStream;

/**
 * Hello world!
 */
public class Server {
  static {
    LogUtils.setLogLevel(RaftServerImpl.LOG, Level.INFO);
    LogUtils.setLogLevel(RaftClient.LOG, Level.INFO);
  }

  public static void main(String[] args) throws Exception {
    new Server().run(args[0]);
  }

  private void run(String nodeIdx) throws Exception {
    int idx = Integer.valueOf(nodeIdx);
    RaftPeerId id = RaftPeerId.valueOf("node" + idx);
    RaftProperties properties = new RaftProperties(true);
    GrpcConfigKeys.Server.setPort(properties, 6000 + idx);
    properties
        .setInt(GrpcConfigKeys.OutputStream.RETRY_TIMES_KEY, Integer.MAX_VALUE);
    //   Configuration conf = HadoopConfigKeys.getConf(new Parameters());
    //   HadoopConfigKeys.setConf();
    RaftServerConfigKeys.setStorageDir(properties, "/tmp/ratis" + nodeIdx);
    //            properties.set(RaftServerConfigKeys.Rpc.TIMEOUT_MIN_KEY,
    // "5000");
    //            properties.set(RaftServerConfigKeys.Rpc.TIMEOUT_MAX_KEY,
    // "6000");
    StateMachine stateMachine = new ArithmeticStateMachine();
    RaftGroupId raftGroupId = new RaftGroupId("test123412341234".getBytes());
    RaftPeer[] raftPeers = IntStream.range(0, 3).mapToObj(
        i -> new RaftPeer(RaftPeerId.valueOf("node" + i),
            "localhost:" + (6000 + i))).toArray(RaftPeer[]::new);
    RaftGroup raftGroup = new RaftGroup(raftGroupId, raftPeers);
    RaftServer raftServer = RaftServer.newBuilder()
        .setServerId(RaftPeerId.valueOf("node" + nodeIdx))
        .setStateMachine(stateMachine).setProperties(properties)
        .setPeers(raftGroup).build();
    raftServer.start();
  }
}

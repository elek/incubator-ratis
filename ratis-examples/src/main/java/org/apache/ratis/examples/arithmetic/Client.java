package org.apache.ratis.examples.arithmetic;

import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.examples.arithmetic.expression.DoubleValue;
import org.apache.ratis.examples.arithmetic.expression.Expression;
import org.apache.ratis.examples.arithmetic.expression.Variable;
import org.apache.ratis.grpc.GrpcFactory;
import org.apache.ratis.protocol.*;

/**
 * Created by elek on 6/15/17.
 */
public class Client {
  public static void main(String[] args) throws Exception {
    new Client().run();
  }

  private void run() throws Exception {
    RaftProperties raftProperties = new RaftProperties(true);

    RaftClient.Builder builder =
        RaftClient.newBuilder().setProperties(raftProperties);
    RaftGroup raftGroup = new RaftGroup(RaftGroupId.createId(), new RaftPeer[] {
        new RaftPeer(RaftPeerId.valueOf("node0"), "localhost:6000")});

    builder.setRaftGroup(raftGroup);
    builder.setClientRpc(new GrpcFactory(new Parameters()).newRaftClientRpc());
    RaftClient client = builder.build();

    //    RaftClientReply asd = client
    //        .send(new AssignmentMessage(new Variable("asd"), new
    // DoubleValue(12d)));
//    RaftClientReply asd =
//        client.sendReadOnly(Expression.Utils.toMessage(new Variable("asd")));
//    System.out.println(asd.getMessage().getContent());
//    Expression response =
//        Expression.Utils.bytes2Expression(asd.getMessage().getContent().toByteArray(), 0);
//    System.out.println(((DoubleValue)response).toString());
//    System.exit(-1);

  }
}

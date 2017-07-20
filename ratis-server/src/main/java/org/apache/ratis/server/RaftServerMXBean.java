package org.apache.ratis.server;

import java.util.List;

/**
 * JMX information about the state of the current raft cluster.
 */
public interface RaftServerMXBean {
    String getId();
    String getLeaderId();
    long getCurrentTerm();
    String getGroupId();
    String getRole();

    /**
     * Only for leaders
     */
    List<RaftPeerInfo> getPeers();

    /**
     * Only for followers.
     */
    String getLeaderAddress();

    public static class RaftPeerInfo {
        private final String address;
        private final String id;

        public RaftPeerInfo(String id, String address) {
            this.address = address;
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public String getId() {
            return id;
        }
    }
}

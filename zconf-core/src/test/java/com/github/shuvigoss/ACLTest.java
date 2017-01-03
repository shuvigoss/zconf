package com.github.shuvigoss;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ACLTest {
  private static final String connectString = "127.0.0.1:2181";
  static TestingServer server;

  static {
    try {
      server = new TestingServer(9999);
    } catch (Exception ignored) {

    }
  }

  public static void main(String[] args) throws Exception {
    CuratorFramework serverClient = null;
    CuratorFramework clientClient = null;
    try {
//      server.start();

      serverClient = createServer();
      serverClient.start();
      serverClient.create().creatingParentsIfNeeded()
                  .forPath("/acltest/test", "test".getBytes());

      clientClient = createClient();
      clientClient.start();
      byte[] bytes = clientClient.getData().forPath("/acltest/test");
      System.out.println(new String(bytes));

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      CloseableUtils.closeQuietly(serverClient);
      CloseableUtils.closeQuietly(clientClient);
      CloseableUtils.closeQuietly(server);
    }

  }

  private static CuratorFramework createClient() throws NoSuchAlgorithmException {
    return CuratorFrameworkFactory
        .builder()
        .connectString(connectString)
        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
//        .aclProvider(new ServerACLProvider())
        .authorization("digest", "zconf:zconf".getBytes())
        .build();
  }

  private static CuratorFramework createServer() throws NoSuchAlgorithmException {

    return CuratorFrameworkFactory
        .builder()
        .connectString(connectString)
        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
        .aclProvider(new ServerACLProvider())
        .authorization("digest", "admin:admin".getBytes())
        .build();
  }

  private static class ServerACLProvider implements ACLProvider {

    private List<ACL> acls = Lists.newArrayList();

    private ServerACLProvider() throws NoSuchAlgorithmException {
      acls.add(new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider
          .generateDigest("admin:admin"))));
      acls.add(new ACL(ZooDefs.Perms.READ, new Id("digest", DigestAuthenticationProvider
          .generateDigest("zconf:zconf"))));
    }

    @Override
    public List<ACL> getDefaultAcl() {
      return acls;
    }

    @Override
    public List<ACL> getAclForPath(String path) {
      return acls;
    }
  }
}

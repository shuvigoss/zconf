# zconf
config base on zookeeper


add zconf.properties in classpath
```
zookeeper=127.0.0.1:2181
rootPath=zconf/abc
auth=zconf:5uxOvkE0
useLocalCache=true
#localCachePath=
#localCacheInitialDelay=
#localCachePeriod=
profiles=com.github.shuvigoss.zconf.profiles.DefaultProfilesCreator
```
``` Java
public static void main(String[] args) throws InterruptedException {
    final ZConf zConf = new ZConf();
    zConf.init();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        zConf.destory();
      }
    });
    for (; ; ) {
      System.out.println(zConf.get("test"));
      JSONObject object = zConf.getC("test1");
      System.out.println(object.getString("hello"));
      Thread.sleep(1000);
    }

  }
```

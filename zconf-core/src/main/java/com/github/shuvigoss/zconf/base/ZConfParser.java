package com.github.shuvigoss.zconf.base;

import com.alibaba.fastjson.JSON;
import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.base.data.ZConfHeader;
import com.github.shuvigoss.zconf.exception.IllegalCacheContentException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.shuvigoss.zconf.base.Const.CR;
import static com.github.shuvigoss.zconf.base.Const.CRLF;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.copyOfRange;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class ZConfParser {

  private final static Logger log = LoggerFactory.getLogger(ZConfParser.class);

  //header(6) + body(1) length
  private static final int LENGTH = 7;

  public static Map<String, ZConfData> parse(Map<String, byte[]> source) {
    Map<String, ZConfData> result = Maps.newHashMap();
    for (Map.Entry<String, byte[]> entry : source.entrySet()) {
      String key = entry.getKey();
      result.put(key, parseValue(entry.getValue(), key));
    }
    return result;
  }

  public static KVPair<String, ZConfData> parse(KVPair<String, byte[]> cache) {
    return new KVPair<>(cache.getKey(), parseValue(cache.getValue(), cache.getKey()));
  }

  private static ZConfData parseValue(byte[] value, String key) {
    List<String> lines = Lists.newArrayList();
    for (int index = 0, pre = 0; ; ) {
      byte b = value[index];
      if (b == CR) {
        int nextCR = index + 2;
        if (nextCR < value.length && value[nextCR] == CR) {
          byte[] line = copyOfRange(value, pre, index);
          lines.add(new String(line, UTF_8));

          //header end
          byte[] body = Arrays.copyOfRange(value, nextCR + 2, value.length);
          lines.add(new String(body, UTF_8));
          break;
        }

        byte[] line = copyOfRange(value, pre, index);
        lines.add(new String(line, UTF_8));
        index += 2;
        pre = index;
      } else index++;
    }
    return parseData(lines, key);
  }

  private static ZConfData parseData(List<String> lines, String key) {
    if (lines.size() != LENGTH) {
      //header(6) + body(1)
      String content = JSON.toJSONString(lines);
      log.error("error parse for value {}", content);
      throw new IllegalCacheContentException(key + ":" + content);
    }

    ZConfHeader zConfHeader = parseHeader(lines.subList(0, LENGTH - 1));
    return new ZConfData(zConfHeader, lines.get(LENGTH - 1));
  }

  static ZConfHeader parseHeader(List<String> headerLines) {
    ZConfHeader header = new ZConfHeader();
    header.setCzxid(Long.parseLong(headerLines.get(0)));
    header.setMzxid(Long.parseLong(headerLines.get(1)));
    header.setCtime(Long.parseLong(headerLines.get(2)));
    header.setMtime(Long.parseLong(headerLines.get(3)));
    header.setVersion(Integer.parseInt(headerLines.get(4)));
    header.setCvt(headerLines.get(5));
    return header;
  }

  public static byte[] toByte(Stat stat) {
    String header =
        stat.getCzxid() + CRLF +
            stat.getMzxid() + CRLF +
            stat.getCtime() + CRLF +
            stat.getMtime() + CRLF +
            stat.getVersion() + CRLF ;
    return header.getBytes(UTF_8);
  }

}

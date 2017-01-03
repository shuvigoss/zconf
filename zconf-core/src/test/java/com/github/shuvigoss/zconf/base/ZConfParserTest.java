package com.github.shuvigoss.zconf.base;

import com.github.shuvigoss.zconf.base.data.ZConfData;
import com.github.shuvigoss.zconf.exception.IllegalCacheContentException;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.github.shuvigoss.zconf.base.Const.CRLF;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfParserTest {

  private Logger log = LoggerFactory.getLogger(ZConfParserTest.class);

  @Test
  public void testSuccess() {
    String content =
        "1111" + CRLF +
            "2222" + CRLF +
            "3333" + CRLF +
            "4444" + CRLF +
            "5555" + CRLF +
            "com.github.shuvigoss.Test" + CRLF + CRLF +
            "some cache ";
    Map<String, ZConfData> result =
        ZConfParser.parse(ImmutableMap.of("test", content.getBytes()));
    ZConfData zConfData = result.get("test");
    log.info("cache content is \r\n" + zConfData.toString());
    assertThat(content, equalTo(zConfData.toString()));
  }

  @Test(expected = IllegalCacheContentException.class)
  public void testFailure() {
    String content =
        "1111" + CRLF +
            "2222" + CRLF +
            "3333" + CRLF +
            "4444" + CRLF + CRLF +
            "some cache ";
    ZConfParser.parse(ImmutableMap.of("test", content.getBytes()));
  }

  @Test(expected = IllegalCacheContentException.class)
  public void testFailure1() {
    String content =
        "1111" + CRLF +
            "2222" + CRLF +
            "3333" + CRLF +
            "4444" + CRLF +
            "5555" + CRLF +
            "6666" + CRLF +
            "com.github.shuvigoss.Test" + CRLF + CRLF +
            "some cache ";
    ZConfParser.parse(ImmutableMap.of("test", content.getBytes()));
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testNoDoubleCRLF() {
    String content =
        "1111" + CRLF +
            "2222" + CRLF +
            "3333" + CRLF +
            "4444" + CRLF +
            "5555" + CRLF +
            "some cache ";
    ZConfParser.parse(ImmutableMap.of("test", content.getBytes()));
  }
}

package com.github.shuvigoss.zconf.web.utils.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;

/**
 * @author shuwei@asiainfo.com
 */
public abstract class BaseCryptor {
  /*
   * 加解密密钥.
   */
  private String key;

  /**
   * 默认构造函数.
   */
  public BaseCryptor() {
    key = "C152E52CABAB3792";
  }

  /**
   * 带密钥的构造函数.
   *
   * @param key 密钥
   */
  public BaseCryptor(String key) {
    this.key = key == null ? "" : key;
  }

  /**
   * 取得Cipher抽象方法.
   *
   * @param isEncrypt 是否加密
   *
   * @return Cipher对象
   *
   * @throws Exception
   */
  protected abstract Cipher getCipher(boolean isEncrypt);

  /**
   * 加密操作.
   *
   * @param data 需要加密的字符串
   *
   * @return 已经加密的字符串
   */
  public String encrypt(String data) {
    try {
      byte[] cleartext = data.getBytes(Charset.forName("UTF-8"));
      byte[] ciphertext = getCipher(true).doFinal(cleartext);

      return toBase64(ciphertext);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }

  }

  private byte[] fromBase64(String s) {
    return DatatypeConverter.parseBase64Binary(s);
  }

  public String toBase64(byte[] array) {
    return DatatypeConverter.printBase64Binary(array);
  }

  /**
   * 解密操作.
   *
   * @param data 需要解密的字符串
   *
   * @return 解密后的字符串
   *
   * @throws Exception
   */
  public String decrypt(String data) {
    try {
      byte[] cleartext = fromBase64(data);
      byte[] ciphertext = getCipher(false).doFinal(cleartext);

      return new String(ciphertext, Charset.forName("UTF-8"));
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 设置密钥.
   *
   * @param key 密钥
   *
   * @return 加密器
   */
  public BaseCryptor setKey(String key) {
    this.key = key;
    return this;
  }

  /**
   * 获取密钥.
   *
   * @return 密钥
   */
  public String getKey() {
    return key;
  }
}

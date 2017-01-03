package com.github.shuvigoss.zconf;

import java.io.File;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class FileUtil {

  public static void deleteFile(File oldPath) {
    if (oldPath.isDirectory()) {
      File[] files = oldPath.listFiles();
      if (files != null) {
        for (File file : files) {
          deleteFile(file);
        }
      }
      oldPath.delete();
    } else {
      oldPath.delete();
    }
  }
}

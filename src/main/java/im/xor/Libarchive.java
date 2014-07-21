package im.xor;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Libarchive extends Library
{
  public int archive_version_number();
  public String archive_version_string();
  public void foo();
}


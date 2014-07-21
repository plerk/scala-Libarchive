package im.xor;

public interface Libarchive extends com.sun.jna.Library
{
  public int archive_version_number();
  public String archive_version_string();
}


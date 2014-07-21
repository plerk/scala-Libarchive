package im.xor.libarchive;

import im.xor.libarchive.ArchiveEntry;

public interface Library extends com.sun.jna.Library
{
  public int archive_version_number();
  public String archive_version_string();

  public ArchiveEntry archive_entry_new();
  public void archive_entry_clear(ArchiveEntry ae);
  public ArchiveEntry archive_entry_clone(ArchiveEntry ae);
  public void archive_entry_free(ArchiveEntry ae);

  public String archive_entry_pathname(ArchiveEntry ae);
  public void archive_entry_copy_pathname(ArchiveEntry ae, String pathname);
}


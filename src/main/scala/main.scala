package im.xor.libarchive

object Libarchive {
  val library = com.sun.jna.Native.loadLibrary("archive", classOf[im.xor.libarchive.Library]).asInstanceOf[im.xor.libarchive.Library];
  lazy val version_string = library.archive_version_string()
  lazy val version_number = library.archive_version_number()
} 

object ArchiveEntry {
  def apply() = Libarchive.library.archive_entry_new()
  /* TODO: archive_entry_new2 which takes an ArchiveRead or ArchiveWrite object as argument */
}

class ArchiveEntry extends com.sun.jna.PointerType {

  def clear() { Libarchive.library.archive_entry_clear(this) }
  override def clone() = Libarchive.library.archive_entry_clone(this)
  def free() {
    if(this.getPointer != com.sun.jna.Pointer.NULL)
    {
      Libarchive.library.archive_entry_free(this)
      this.setPointer(com.sun.jna.Pointer.NULL);
    }
  }
  
  def pathname = Libarchive.library.archive_entry_pathname(this)
  def pathname_= (s:String) = Libarchive.library.archive_entry_copy_pathname(this,s)
  
  override def finalize() = free()
}

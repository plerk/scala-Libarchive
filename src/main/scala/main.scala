package im.xor.libarchive

import com.sun.jna.{ Pointer, PointerType, NativeLong }

trait Library extends com.sun.jna.Library {
  def archive_version_number() : Int
  def archive_version_string() : String
  
  /* entry */
  def archive_entry_new(): ArchiveEntry
  def archive_entry_clear(ae: ArchiveEntry) : Unit
  def archive_entry_clone(ae: ArchiveEntry) : ArchiveEntry
  def archive_entry_free(ae: ArchiveEntry) : Unit
  
  def archive_entry_pathname(ae: ArchiveEntry) : String
  def archive_entry_copy_pathname(ae: ArchiveEntry, pathname: String) : Unit 
  
  /* archive */
  def archive_errno(ar: Archive) : Int
  def archive_error_string(ar: Archive) : String
  
  /* archive_read */
  def archive_read_new() : ArchiveRead
  def archive_read_free(ar: ArchiveRead) : Int
  def archive_read_close(ar: ArchiveRead) : Int

  def archive_read_next_header2(ar: ArchiveRead, ae: ArchiveEntry) : Int
  
  /* archive_read_open */
  def archive_read_open_filename(ar: ArchiveRead, filename: String, block_size: NativeLong) : Int
  
  /* archive_read_support */
  def archive_read_support_filter_all(ar: ArchiveRead) : Int
  def archive_read_support_format_all(ar: ArchiveRead) : Int
}

object Libarchive {
  val library = com.sun.jna.Native.loadLibrary(
    sys.env.get("SCALA_LIBARCHIVE_PATH") match {
      case Some(s) => s
      case _ => "archive"
    },
    classOf[im.xor.libarchive.Library]
  ).asInstanceOf[im.xor.libarchive.Library]

  lazy val version_string = library.archive_version_string()
  lazy val version_number = library.archive_version_number()
  
  val EOF    = 1
  val OK     = 0
  val RETRY  = -10
  val WARN   = -20
  val FAILED = -25
  val FATAL  = -30
} 

trait Freeable {
  def getPointer() : Pointer
  def setPointer(ptr: Pointer) : Unit
  def deallocate() : Unit
  def free() {
    if(this.getPointer != Pointer.NULL) {
      this.deallocate()
      this.setPointer(Pointer.NULL)
    }
  }
}

object ArchiveEntry {
  def apply() = Libarchive.library.archive_entry_new()
  /* TODO: archive_entry_new2 which takes an ArchiveRead or ArchiveWrite object as argument */
}

class ArchiveEntry extends PointerType with Freeable {

  def clear() { Libarchive.library.archive_entry_clear(this) }
  override def clone() = Libarchive.library.archive_entry_clone(this)
  def deallocate() = Libarchive.library.archive_entry_free(this)
  
  def pathname = Libarchive.library.archive_entry_pathname(this)
  def pathname_= (s:String) = Libarchive.library.archive_entry_copy_pathname(this,s)
  
  override def finalize() = free()
  
  override def toString() = "ArchiveEntry[" + this.pathname + "]"
}

class ArchiveException(val errno: Int, val message: String, val exception_type: Int) extends Throwable {
  override def toString() = "ArchiveException[" + this.message + "]"
}

abstract class Archive(warning_handler : Option[(ArchiveException) => Unit] = Some((e:ArchiveException) => System.err.println(e))) extends PointerType with Freeable {
  protected def wrapper(f: () => Int) = {
    val r = f()
    (r, warning_handler) match {
      case ( Libarchive.FAILED, _       ) => throw this.create_exception(r)
      case ( Libarchive.FATAL,  _       ) => throw this.create_exception(r)
      case ( Libarchive.WARN,   Some(h) ) => h(this.create_exception(r))
      case ( _,                 _       ) => None
    }
    r
  }
  
  private def create_exception(r: Int) = new ArchiveException(
    Libarchive.library.archive_errno(this),
    Libarchive.library.archive_error_string(this),
    r 
  )
}

class ArchiveRead extends Archive {
  def deallocate() = Libarchive.library.archive_read_free(this)
  def close() = Libarchive.library.archive_read_close(this)
  def support_format_all() = Libarchive.library.archive_read_support_format_all(this)
  def support_filter_all() = Libarchive.library.archive_read_support_filter_all(this)
  
  def next_header() = {
    val ae = ArchiveEntry()
    val r = wrapper( () => Libarchive.library.archive_read_next_header2(this, ae) )
    if(r == Libarchive.EOF) {
      ae.free()
      None
    } else {
      Some(ae)
    }
  }

  /* Note: you only get to do this once */
  def map[T](f: (ArchiveEntry) => T) : List[T] = {
    val ae = ArchiveEntry()
    val r = wrapper( () => Libarchive.library.archive_read_next_header2(this, ae) )
    if(r == Libarchive.EOF) {
      ae.free()
      List[T]()
    } else {
      List[T](f(ae)) ++ this.map(f)
    }
  }
  
  def open_filename(filename: String, block_size: Int = 10240) = {
    this.wrapper(() => Libarchive.library.archive_read_open_filename(this, filename, new NativeLong(block_size)))
  }
}

object ArchiveRead {
  def apply() = Libarchive.library.archive_read_new()
  def apply(filename: String, block_size: Int = 1024) = {
    val a = Libarchive.library.archive_read_new()
    a.support_format_all()
    a.support_filter_all()
    a.open_filename(filename, block_size)
    a
  }
}


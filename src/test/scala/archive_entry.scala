import org.scalatest.FlatSpec
import im.xor.libarchive.ArchiveEntry

class ArchiveEntryTest extends FlatSpec {

  "pathname method" should "set the pathname" in {
    val ae = ArchiveEntry()
    ae.pathname = "foo"
    assert(
      ae.pathname == "foo"
    )
    ae.free()
  }
  
  "free" should "set the pointer to NULL" in {
    val ae = ArchiveEntry()
    
    assert(ae.getPointer != com.sun.jna.Pointer.NULL)
    ae.free()
    assert(ae.getPointer == com.sun.jna.Pointer.NULL)    
  }

}

import org.scalatest.FlatSpec
import im.xor.libarchive.ArchiveRead
import im.xor.libarchive.ArchiveEntry

class ArchiveReadTest extends FlatSpec {

  val foo_tar = getClass.getResource("/foo.tar").getFile;

  "ArchiveRead" should "open with a filename" in {
    val ar = ArchiveRead(foo_tar)
    assert(true)
  }
  
  "next_header" should "retrieve the next header" in {
    val ar = ArchiveRead(foo_tar)
    
    assert(ar.next_header() match { 
      case Some(ae) => ae.pathname == "foo/foo.txt"
      case _ => false
    })

    assert(ar.next_header() match { 
      case Some(ae) => ae.pathname == "foo/bar.txt"
      case _ => false
    })

    assert(ar.next_header() match { 
      case Some(ae) => ae.pathname == "foo/baz.txt"
      case _ => false
    })
    
    assert(ar.next_header() == None)
    
    ar.free()
  }

}

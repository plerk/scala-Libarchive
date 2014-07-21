import org.scalatest.FlatSpec

class VersionTest extends FlatSpec {

  val libarchive = com.sun.jna.Native.loadLibrary("archive", classOf[im.xor.Libarchive]).asInstanceOf[im.xor.Libarchive]

  "archive_version_string" should "not be an empty string" in {
    assert(
      libarchive.archive_version_string != ""
    )
  }

  "archive_version_string" should "not start with libarchive and a version number" in {
    assert(
      """^libarchive [0-9]+\.[0-9]+""".r.findFirstIn(libarchive.archive_version_string) != None
    )
  }
  
  "archive_version_number" should "be greater than 2.7" in {
    assert(
      libarchive.archive_version_number >= 2007000
    )
  }

}

import org.scalatest.FlatSpec
import im.xor.libarchive.Libarchive

class VersionTest extends FlatSpec {

  "version_string" should "not be an empty string" in {
    assert(
      Libarchive.version_string != ""
    )
  }

  "archive_version_string" should "not start with libarchive and a version number" in {
    assert(
      """^libarchive [0-9]+\.[0-9]+""".r.findFirstIn(Libarchive.version_string) != None
    )
  }
  
  "version_number" should "be greater than 2.7" in {
    assert(
      Libarchive.version_number >= 2007000
    )
  }

}

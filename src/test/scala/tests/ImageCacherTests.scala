package tests

import org.specs2.mutable.Specification

import java.io.{ByteArrayInputStream, File}
import java.awt.Dimension

import javax.imageio.ImageIO
import java.nio.file.Paths
import wt.common.image.ImageCacher
import wt.common.DataStore

class ImageCacherTests extends Specification {
  "ImageCacher" should {
    "Return a cached image if any" in {
      val dataPath = "/users/tim/projects/wt.common/data"
      val ic = new ImageCacher(DataStore(dataPath), None)
      val originalImage = new File(Paths.get(dataPath, "original", "src.jpg").toString)
      def ok: String = "test"
      val sr = ic.cachedImage(originalImage, 1, new Dimension(400, 400), 0.8F, false) {
        imageData =>
          val bais = new ByteArrayInputStream(imageData)
          ImageIO.read(bais)
      }
      def cImage()(f: => String) = {
        ic.cachedImage(originalImage, 1, new Dimension(400, 400), 0.8F, false) { v =>
          if (v.length > 0) {
            ok
          } else {
            f
          }
        }
      }
      sr.getWidth mustEqual 400
      sr.getHeight mustEqual 400
    }


    "Cache" in {
      val dataPath = "/users/tim/projects/wt.common/data"
      val ic = new ImageCacher(DataStore(dataPath), None)
      ic.cache(
        ImageIO.read(new File("/users/tim/projects/wt.common/data/bike.png")),
        Paths.get("/users/tim/projects/wt.common/data/cache/bike.png"), 0.8F
      )
      0 mustEqual 0
    }
  }
}

package tests

import java.nio.file.Paths

import org.specs2.mutable.Specification

import java.awt.Dimension
import javax.imageio.ImageIO
import java.io.File
import wt.common.DataStore
import wt.common.image.{ImageCacher, ImageResizer}
import org.specs2.execute._

class ImageResizerTests extends Specification {
  "Image resizer" should {
    "Resize an image" in {
      val imageResizer = new ImageResizer(Some("/users/tim/projects/wt.common/data/no_image.jpg"))
      val sourceImg = new File("/users/tim/projects/web.common/data/original/src2.png")
      val img = imageResizer.resize(sourceImg, new Dimension(600, 600), false, false)

      val outputfile = new File("/users/tim/projects/web.common/data/image.jpg")

      val cacher = new ImageCacher(DataStore("/users/tim/projects/web.common/data"), None)
      cacher.cache(img, Paths.get("/users/tim/projects/web.common/data/cache/cached.jpg"), 0.8F)
      //ImageIO.write(img, "jpg", outputfile)
      img.getWidth mustEqual 600
      img.getHeight mustEqual 600
    }
  }
}

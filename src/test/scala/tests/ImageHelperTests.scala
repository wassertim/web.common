package tests

import java.nio.file.Paths

import org.specs2.mutable.Specification
import wt.common.DataStore
import wt.common.image.ImageHelper
import org.specs2.execute._

class ImageHelperTests extends Specification {
  "ImageHelper" should {
    "Save an image" in {
      lazy val dataStore = new DataStore(Paths.get(System.getProperty("user.home"), "data/digitalband").toString)
      val imageHelper = ImageHelper(dataStore).save("https://musicnota.ru/upload/iblock/71b/71b4da6df2f1058b1dc147c6d8f9760a.jpg").map {
        img =>
          val id = img.id
      }
      1 mustEqual 1
    }


  }
}

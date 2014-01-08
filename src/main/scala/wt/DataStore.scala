package wt.common

import java.nio.file.Paths

case class DataStore (val appPath: String) {
  val dataPath = Paths.get(appPath, "data").toString
  val imagesPath = Paths.get(dataPath, "images").toString
  val imageOriginalsPath = Paths.get(imagesPath, "originals").toString
  val productOriginalImagePath = Paths.get(imageOriginalsPath, "productimages").toString
}

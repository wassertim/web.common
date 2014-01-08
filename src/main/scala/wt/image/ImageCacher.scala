package wt.common.image
import java.io._
import java.awt.image.BufferedImage
import java.nio.file._
import java.awt.Dimension
import javax.imageio.ImageIO
import javax.imageio.stream.{ImageOutputStream, FileImageOutputStream}
import wt.common.closable
import wt.common.DataStore


case class ImageCacher(paths: DataStore, errorImagePath: Option[String]) {

  def cachedImage[T](originalImageFile: File, imageId: Int, outputDimension: Dimension, compressQuality: Float, crop: Boolean, preserveAlpha: Boolean = true)(f: (Array[Byte]) => T): T = {
    f(getCachedImage(originalImageFile, imageId, outputDimension, compressQuality, crop, preserveAlpha))
  }

  def getCachedImage(originalImageFile: File, imageId: Int, outputDimension: Dimension, compressQuality: Float, crop: Boolean, preserveAlpha: Boolean = true) = {
    val cachePath = buildCachePath(outputDimension, compressQuality, crop, imageId)
    if (Files.exists(cachePath)) {
      toByteArr(cachePath.toFile)
    } else {
      val resizedImage: BufferedImage = new ImageResizer(errorImagePath).resize(originalImageFile, outputDimension, preserveAlpha, crop)
      toByteArr(cache(resizedImage, cachePath, compressQuality))
    }
  }

  private def buildCachePath(outputDimension: Dimension, compressQuality: Float, crop: Boolean, imageId: Int) = {
    val fill = crop match {
      case true => "cropped"
      case false => "full"
    }
    val quality = (compressQuality * 100).toInt.toString
    Paths.get(
      paths.imagesPath,
      "cache",
      outputDimension.width + "x" + outputDimension.height,
      quality,
      fill,
      imageId + ".jpg"
    )
  }
  def toByteArr(file: File) = {
    closable(scala.io.Source.fromFile(file)(scala.io.Codec.ISO8859)) { source =>
      source.map(_.toByte).toArray
    }
  }

  def getStream(bi: BufferedImage) = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(bi, "png", baos)
    new ByteArrayInputStream(baos.toByteArray())
  }

  def convert(stream1: ImageOutputStream): InputStream = {
    new ByteArrayInputStream(stream1.asInstanceOf[ByteArrayOutputStream].toByteArray())
  }

  def cache(image: BufferedImage, cachePath: Path, quality: Float) = {
    val outputFile = new File(cachePath.toString)
    if (!outputFile.getParentFile.exists)
      outputFile.getParentFile.mkdirs
    ImageIO.write(image, "png", cachePath.toFile)
    //ImageHelper(paths).write(image, new FileImageOutputStream(outputFile), quality)
    cachePath.toFile
  }
}

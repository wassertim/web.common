package wt.common.image

import java.awt._
import java.awt.image.{FilteredImageSource, RGBImageFilter, BufferedImage}
import java.io._
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import javax.imageio.stream.ImageOutputStream

import java.nio.file.Paths

import java.net.URL

import org.apache.commons.codec.digest.DigestUtils.md5Hex
import java.util.UUID
import org.apache.commons.io.FileUtils

import wt.common.{disposable, closable}
import scala.Some
import wt.common.DataStore

case class ImageHelper(paths: DataStore, movingMethod: Option[String] = Some("copy")) {

  def deleteImage(relativePath: String): Any = {
    Paths.get(paths.imageOriginalsPath, relativePath).toFile.delete()
  }

  def deleteImage(image: ImageEntity, deleteFromRepo: Int => Any): Any = {
    deleteImage(image.path)
    deleteFromRepo(image.id)
  }
  def move(source: File, destination: File) = {
    movingMethod match {
      case Some(x) if x == "move" =>
        if (!destination.exists())
          FileUtils.moveFile(source, destination)
      case _ => {
        FileUtils.copyFile(source, destination)
        source.delete()
      }
    }
  }

  def imageType(file: File) = {
    ImageIO.getImageReaders(ImageIO.createImageInputStream(file)).next.getFormatName
  }

  def saveToTempFolder(imageUrl: String) = {
    val file = Paths.get(paths.imagesPath, "temp", UUID.randomUUID().toString + ".jpg").toFile
    org.apache.commons.io.FileUtils.copyURLToFile(new URL(imageUrl), file)
    file
  }

  def save(imageUrl: String): Option[ImageEntity] = {
    try {
      val file = saveToTempFolder(imageUrl)
      val imageEntity = getImageEntity(md5Hex(new FileInputStream(file)), imageType(file).toLowerCase)
      val fileName = Paths.get(paths.imageOriginalsPath, imageEntity.path).toString
      move(file, new File(fileName))
      Some(imageEntity)
    } catch {
      case e: IOException => None
    }
  }

  def getImageEntity(md5: String, imageExtension: String) = {
    val name = md5 + s".${imageExtension}"
    val relativePath = Paths.get("productimages", name).toString
    new ImageEntity(relativePath, md5)
  }

  def imageToBufferedImage(image: Image): BufferedImage = {
    val bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    disposable(bufferedImage.createGraphics()) {
      g2 =>
        g2.drawImage(image, 0, 0, null)
        bufferedImage
    }
  }

  def makeColorTransparent(im: BufferedImage): Image = {
    val filter = new RGBImageFilter() {
      val markerRGB = 0xFF000000

      def filterRGB(x: Int, y: Int, rgb: Int): Int = {
        if ((rgb | 0xFF000000) == markerRGB) {
          return 0x00FFFFFF & rgb
        } else {
          rgb
        }
      }
    }
    val ip = new FilteredImageSource(im.getSource(), filter)
    Toolkit.getDefaultToolkit().createImage(ip)
  }

  def save(file: File)(insertImage: ImageEntity => ImageEntity): ImageEntity = {
    val imageEntity = getImageEntity(md5Hex(new FileInputStream(file)), imageType(file))
    val fileName = Paths.get(paths.imageOriginalsPath, imageEntity.path).toString
    move(file, new File(fileName))
    insertImage(imageEntity)
  }

  def getDimension(imageSize: String) = {
    val arr = imageSize.split("x").map(_.toInt)
    new Dimension(arr(0), arr(1))
  }

  def isCropped(fill: String) = {
    fill match {
      case "cropped" => true
      case "full" => false
    }
  }

  def checkQuality(quality: Int): Float = {
    if (quality < 20)
      0.2f
    else if (quality > 100)
      1.0f
    else
      (quality.toFloat / 100)
  }

  @Deprecated
  def getImageId(imageNumber: String): Int = {
    //imageNumber.split("[\\.]")(0).toInt
    imageNumber.toInt
  }

  def makeTransparent(image: BufferedImage): BufferedImage = {
    val img = makeColorTransparent(image)
    imageToBufferedImage(img)
  }



  def write(image: BufferedImage, outputFile: ImageOutputStream, quality: Float): ImageOutputStream = {
    //TODO: Fix the problem with black background instead of transparent
    disposable(ImageIO.getImageWritersByFormatName("png").next()) {
      jpegWriter =>
        val param: ImageWriteParam = jpegWriter.getDefaultWriteParam()
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        param.setCompressionQuality(quality)
        closable(outputFile) {
          out =>
            jpegWriter.setOutput(out)
            jpegWriter.write(null, new IIOImage(image, null, null), param)
            outputFile
        }
    }
  }
}

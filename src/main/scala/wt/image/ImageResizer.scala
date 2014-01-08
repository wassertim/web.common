package wt.common.image

import java.awt._
import java.awt.image.BufferedImage

import java.io.File
import org.imgscalr.Scalr
import javax.imageio.ImageIO
import wt.common.disposable

case class ImageResizer(errorImagePath: Option[String] = None) {
  def resize(originalImageFile: File, outputSize: Dimension, preserveAlpha: Boolean, crop: Boolean): BufferedImage = {
    //Scalr.resize(ImageIO.read(originalImageFile), Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, outputSize.width, outputSize.height)
    val originalImageSize = getImageSize(ImageIO.read(originalImageFile))
    val scaledRectangle = getScaledRectangle(originalImageSize, outputSize, getRatio(crop, outputSize, originalImageSize))
    drawImage(ImageIO.read(originalImageFile), new BufferedImage(outputSize.width, outputSize.height, getImageType(preserveAlpha)), outputSize, scaledRectangle, preserveAlpha)
  }

  private def getImageType(preserveAlpha: Boolean) = preserveAlpha match {
    case true => BufferedImage.TYPE_INT_ARGB_PRE
    case false => BufferedImage.TYPE_INT_RGB
  }

  private def getImageSize(image: BufferedImage) = new Rectangle(image.getWidth(null), image.getHeight(null))

  private def getRatio(crop: Boolean, outputSize: Dimension, sourceSize: Rectangle): Double = {
    val widthRatio = outputSize.width.toDouble / sourceSize.width.toDouble
    val heightRatio = outputSize.height.toDouble / sourceSize.height.toDouble
    crop match {
      case true => Math.max(widthRatio, heightRatio)
      case false => Math.min(widthRatio, heightRatio)
    }
  }

  private def getScaledRectangle(sourceSize: Rectangle, outputSize: Dimension, ratio: Double) = {
    val scaledDimension = new Dimension((sourceSize.width * ratio).toInt, (sourceSize.height * ratio).toInt)
    val relativePoint = new Point((outputSize.width - scaledDimension.width) / 2, (outputSize.height - scaledDimension.height) / 2)
    new Rectangle(relativePoint, scaledDimension)
  }

  private def drawImage(sourceImage: BufferedImage, outputImage: BufferedImage, outputSize: Dimension, scaledRectangle: Rectangle, preserveAlpha: Boolean) = {
    disposable (outputImage.createGraphics()) { canvas =>
      //val scaledImage = sourceImage.getScaledInstance(scaledRectangle.width, scaledRectangle.height, Image.SCALE_SMOOTH)
      val scaledImage = Scalr.resize(sourceImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, outputSize.width, outputSize.height)
      if (preserveAlpha) canvas.setComposite(AlphaComposite.Src)
      canvas.setPaint(Color.white)
      canvas.fillRect(0, 0, outputSize.width, outputSize.height)
      canvas.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      canvas.drawImage(scaledImage, scaledRectangle.x, scaledRectangle.y, scaledRectangle.width, scaledRectangle.height, null)
      outputImage
    }
  }
}

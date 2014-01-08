package wt.common.image

case class ImageEntity(val path: String, val md5: String, val id: Int = 0)

case class PictureEntity(val id: Int, val path: String, val imageType: String)

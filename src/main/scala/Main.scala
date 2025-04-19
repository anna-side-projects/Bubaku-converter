import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object Main {
  def main(args: Array[String]): Unit = {
    if (args == null || args.length < 1 || args(0) == null) {
      println("Missing args for file path")
      return
    }
    val path = args(0)
    val file = os.Path(path)
    val wd = os.Path(path) / os.up
    val str = scala.io.Source.fromFile(path).mkString
    val newDir = wd/"converted"
    os.remove.all(newDir)
    os.makeDir.all(newDir)


    val json = Json.parse(str)
    val bubakLocations:JsArray = json("BubakLocations").as[JsArray]
    implicit val newBubakuPropsWrites = Json.writes[BubakProps]
    implicit val newBubakuWrites = Json.writes[NewBubaku]
    implicit val newFileWrites = Json.writes[NewFile]
    val oldFile = json.validate[OldFile]
    if(oldFile.isError) {
      println("Failed to parse json")
      return
    }
    val newFile = oldFile.get.convert()

    val newJson:JsValue = Json.toJson(newFile)
    os.write(newDir/file.getSegment(file.segmentCount-1), Json.prettyPrint(newJson))

  }
}

case class OldFile(
                    loglevel:Int,
                    BubakLocations:Array[OldBubaku]
                  ) {
  def convert():NewFile = {
    val bubakLocations:Array[NewBubaku] = BubakLocations.map(_.convert())
    NewFile(loglevel,bubakLocations)
  }
}
object OldFile {
  implicit val reads: Reads[OldFile] = (
    (__ \ "loglevel").read[Int] and
      (__ \ "BubakLocations").read[Array[OldBubaku]]
  )(OldFile.apply _)

}
case class OldBubaku(
                    name:String,
                    workinghours:String,
                    triggerdependency:Array[String],
                    triggerpos:String,
                    triggermins:String,
                    triggermaxs:String,
                    triggerradius:Float,
                    triggercylradius:Float,
                    triggercylheight:Float,
                    notification:String,
                    notificationtime:Int,
                    triggerdelay:Int,
                    spawnerpos:Array[String],
                    spawnradius:Float,
                    bubaknum:Int,
                    onlyfilluptobubaknum:Int,
                    itemrandomdmg:Int,
                    bubaci:Array[String],
                    bubakinventory:Array[String]
                    ) {
  implicit val reader:Reads[OldBubaku] = Json.reads[OldBubaku]
  def convert(): NewBubaku = {
    val bubakProps = BubakProps(this.bubaknum,this.onlyfilluptobubaknum,this.itemrandomdmg,this.bubaci,this.bubakinventory)
    NewBubaku(name,workinghours,triggerdependency,triggerpos, triggermins, triggermaxs,triggerradius,triggercylradius,triggercylheight, notification, notificationtime, triggerdelay, spawnerpos, spawnradius, bubakProps)
  }
}
object OldBubaku {
  implicit val reads:Reads[OldBubaku] = Json.reads[OldBubaku]
}

case class NewFile(
                  loglevel:Int,
                  BubakLocations:Array[NewBubaku]
                  )

case class NewBubaku(
                      name:String,
                      workinghours:String,
                      triggerdependency:Array[String],
                      triggerpos:String,
                      triggermins:String,
                      triggermaxs:String,
                      triggerradius:Float,
                      triggercylradius:Float,
                      triggerycylheight:Float,
                      notification:String,
                      notificationtime:Int,
                      triggerdelay:Int,
                      spawnerpos:Array[String],
                      spawnradius:Float,
                      bubakProps:BubakProps
                    )
case class BubakProps(
                       bubaknum:Int,
                       onlyfilluptobubaknum:Int,
                       itemrandomdmg:Int,
                       bubaci:Array[String],
                       bubakinventory:Array[String]
                     )
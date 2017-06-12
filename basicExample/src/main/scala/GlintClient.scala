/*
/**
  * Created by nouman on 5/28/17.
  */
import glint.Client

import scala.concurrent.ExecutionContext

object GlintClient {
  def main(args: Array[String]): Unit = {
    val gc = Client()
    implicit val ec = ExecutionContext.Implicits.global
    val serverList = gc.serverList()
    serverList.onSuccess {
      case values => println(values.mkString(", "))
    }
  }

}
*/

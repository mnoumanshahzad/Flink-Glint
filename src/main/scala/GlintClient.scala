import glint.Client
import glint.models.client.BigVector
import org.apache.flink.api.common.functions.{FlatMapFunction, RichMapFunction}
import org.apache.flink.api.scala._
import org.apache.flink.util.Collector

import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.concurrent.ExecutionContext

object GlintClient extends LazyLogging{
    def main (args: Array[String]) = {

        //Flink Execution Environment
        val env = ExecutionEnvironment.getExecutionEnvironment

        //Initial data for Flink
        val data = env.fromElements("0 0.0\n1 2.0\n")

        //Converts the input in Tuple2[Int, Double]
        val transformed = data.flatMap(new CreateTuple)

        //Prints the Tuple2[Int, Double]
        //transformed.print()

        //Initiates a Glint Client
        @transient val gc = Client()

        @transient implicit val ec = ExecutionContext.Implicits.global

        //Creates a Glint BigVector
        @transient val vector = gc.vector[Double](2)

        //Trying to push each value of DataSet to the Glint BigVector
        //transformed.map(new GlintVectorPush(vector)).print()


        if (gc.isInstanceOf[Client]) { println("GC is instance of Client") }

        if (vector.isInstanceOf[BigVector[Double]]) { println("Vector is instance of BigVector") }

        //Pushing some fixed values
        vector.push(Array(0L, 1L), Array(1.0, 2.0))(ec).onSuccess { case true => println("Push successful") }

        
        //val serverList = gc.serverList()
        //serverList.onSuccess { case values => println(values.mkString(", ")) }

        val vectorResult = vector.pull(Array(0L, 1L))

        vectorResult.onSuccess { case values => println(values.mkString(", ")) }
        vectorResult.onFailure { case _ => println("Failed") }

        /*vector.pull((0L to 1).toArray).onSuccess {
            case values => env.fromElements(values).print()
        }*/

        vector.destroy()
        gc.stop()
    }

    final class CreateTuple extends FlatMapFunction[String, Tuple2[Int, Double]] {
        override def flatMap(value: String, out: Collector[(Int, Double)]) {
            val listOfCoords = value.split("\\n+")
            for (coord <- listOfCoords) {
                val t = coord.split("\\W+")
                out.collect(new Tuple2(t(0).toInt, t(1).toDouble))
            }
        }
    }

    final class GlintVectorPush(glintVector: BigVector[Double]) extends RichMapFunction[(Int, Double), (Int, Double)] {

        @transient implicit val ec = ExecutionContext.Implicits.global

        override def map(value: (Int, Double)): (Int, Double) = {
            glintVector.push(Array(value._1), Array(value._2))
            value
        }
    }
}

import glint.Client
import glint.models.client.BigVector
import org.apache.flink.api.common.functions.{FlatMapFunction, RichMapFunction}
import org.apache.flink.api.scala._
import org.apache.flink.util.Collector

import scala.concurrent.ExecutionContext

object Main {
  def main(args: Array[String]): Unit = {

    /**
      * Initializing a Glint Client and a Glient Vector
      * Declared as transient so that Flink does not try to serialize these
      */
    @transient val gc = Client()
    @transient implicit val ec = ExecutionContext.Implicits.global
    @transient val vector = gc.vector[Double](10)

    /**
      * Flink Execution Environment and sample input data
      */
    val env = ExecutionEnvironment.getExecutionEnvironment
    val data = env.fromElements("0 0.0\n1 2.0\n2 -9.0\n3 5.5\n4 3.14\n5 55.5\n6 0.01\n7 10.0\n8 100.0\n9 1000.0")

    /**
      * Converting the input data to Flink data structures (i.e Tuple2)
      */
    val transformed = data.flatMap(new CreateTuple2)

    /**
      * An alternative to Apache Spark's rdd.forEach construct
      * Further description found in the respective class
      */
    transformed.map(new GlintVectorPush(vector)).print()

    /**
      * Pulling the values from Glint
      */
    val pulledValues = vector.pull((0L until 10).toArray)

    /**
      * Simply printing the values to console
      */
    pulledValues.onSuccess {
      case values => {
        print("Values fetched from the Glint Vector: ")
        println(values.mkString(", "))
      }
    }

    /**
      * Cleaning up Glint
      */
    vector.destroy()
    gc.stop()
  }


  /**
    * Takes input a String representing key/value pairs
    * Outputs a Tuple2[Int, Double]
    */
  final class CreateTuple2 extends FlatMapFunction[String, Tuple2[Int, Double]] {
    override def flatMap(value: String, out: Collector[(Int, Double)]) = {
      val listOfCoords = value.split("\\n+")
      for (coord <- listOfCoords) {
        val t = coord.split("\\W+")
        out.collect(new Tuple2(t(0).toInt, t(1).toDouble))
      }
    }
  }

  /**
    * Each value is passed through a mapper that pushes the value to the Glint vector
    * Returns back the actual value unchanged
    * @param glintVector  Reference to the glint.BigVector to push values to
    */
  final class GlintVectorPush(glintVector: BigVector[Double]) extends RichMapFunction[(Int, Double), (Int, Double)] {
    override def map(value: (Int, Double)): (Int, Double) = {
      implicit val ec = ExecutionContext.Implicits.global
      glintVector.push(Array(value._1), Array(value._2))
      value
    }
  }
}

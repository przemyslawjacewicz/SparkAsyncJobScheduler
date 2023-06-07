package pl.epsilondeltalimit.dep.v4_1

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import pl.epsilondeltalimit.SparkSessionProvider
import pl.epsilondeltalimit.dep.v4_1.Dep.{map2, mapN, run, unit}

object Runner4_1a extends SparkSessionProvider {
  def main(args: Array[String]): Unit = {
    val d = mapN[DataFrame]("d")("c", "b") { case c :: d :: Nil => c.unionByName(d) }

    val c = map2[DataFrame]("c")("a", "b")((a, b) => a.unionByName(b))

    val b = unit("b") {
      println("evaluating b")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val a = unit("a") {
      println("evaluating a")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    run(new SimpleRegister[DataFrame])(d, c, b, a).get("d")().show()

  }
}
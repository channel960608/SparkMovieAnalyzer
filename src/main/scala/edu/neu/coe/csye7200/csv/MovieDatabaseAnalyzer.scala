package edu.neu.coe.csye7200.csv

import com.phasmidsoftware.table.Table
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.util._


/**
 * @author scalaprof
 */
case class MovieDatabaseAnalyzer(resource: String) {

  val spark: SparkSession = SparkSession
          .builder()
          .appName("SparkMovieRating")
          .master("local[*]")
          .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR") // We want to ignore all of the INFO and WARN messages.

  import MovieParser._
  import spark.implicits._

  private val mty: Try[Table[Movie]] = Table.parseResource(resource, getClass)

  val ratings: Try[Dataset[Double]] = mty map {
    mt =>
      spark.createDataset(mt.map(_.reviews.imdbScore).rows.toSeq)
  }

  def getSumCount = ratings map {
    _.rdd.map((_, 1)).reduce((a, b) => (a._1 + b._1, a._2 + b._2))
  }

  def getMean2 = ratings map {
    _.filter(Option(_).isDefined).select(mean("value")).head.getDouble(0)
  }

  def getMean: Try[Double] = getSumCount map {
    t => t._1 / t._2
  }
//
  def getSqrtDevSumCount = ratings map {
    r =>
      val m = getMean.get
      r.rdd.filter(Option(_).isDefined).map(x => (math.pow(x - m, 2), 1)).reduce((a, b) => (a._1 + b._1, a._2 + b._2))
  }
  def getStdDev = getSqrtDevSumCount map {
    t => math.sqrt(t._1 / t._2)
  }
//
  def getStdDev2 = ratings map {
    _.filter(Option(_).isDefined).select(stddev("value")).head.getDouble(0)
  }

}


/**
 * @author scalaprof
 */
object MovieDatabaseAnalyzer extends App {

  def apply(resource: String): MovieDatabaseAnalyzer = new MovieDatabaseAnalyzer(resource)

  val mda = apply("/movie_metadata.csv")
  mda
//  println("The mean for ratings is " + mda.getMean.get)
//  println("The standard deviation for ratings is " + mda.getStdDev.get)
}

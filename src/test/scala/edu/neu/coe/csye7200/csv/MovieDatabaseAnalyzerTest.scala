package edu.neu.coe.csye7200.csv

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovieDatabaseAnalyzerTest extends AnyFlatSpec with Matchers {

  behavior of "mean"
  it should "calculate mean of ratings" in {

    val db = new MovieDatabaseAnalyzer("/movie_metadata.csv")

    val mean = db.getMean.get
    val meanCheck = db.getMean2.get

    mean shouldBe meanCheck
  }

  behavior of "std dev"
  it should "calculate standard deviance of ratings" in {

    val db = new MovieDatabaseAnalyzer("/movie_metadata.csv")

    val stdDev = db.getStdDev.get
    val stdDevCheck = db.getStdDev2.get

    math.abs(stdDev - stdDevCheck) < 0.01 shouldBe true
  }

}

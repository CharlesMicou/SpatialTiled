package base

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
  * voxoid on stackoverflow figured this out
  * https://stackoverflow.com/questions/18823855/cant-run-scalatest-with-gradle
  */
@RunWith(classOf[JUnitRunner])
abstract class BaseTest extends FlatSpec with Matchers

package bayanat

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class ApigFGSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://10.132.172.234") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val feeder = Iterator.continually(Map("id" -> Random.nextInt()))

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .feed(feeder)
    .exec(http("reverse geocode FG")
      .get("/testFG")
      .queryParam("id", "${id}")
      .check(status.is(200))
      .check(jsonPath("$..error").notExists))

  setUp(scn.inject(atOnceUsers(10000),
//                    nothingFor(15.minutes),
    rampUsersPerSec(1) to(100) during(10.minutes),
    constantUsersPerSec(100) during(30.minutes),
    rampUsersPerSec(100) to(400) during(30.minutes),
    constantUsersPerSec(400) during(2.hours),
    rampUsersPerSec(400) to(800) during(30.minutes),
    constantUsersPerSec(800) during(2.hours),
    rampUsersPerSec(800) to(900) during(5.minutes),
    constantUsersPerSec(900) during(2.hours),
    rampUsersPerSec(900) to(1000) during(5.minutes),
    constantUsersPerSec(1000) during(1.hours),
    rampUsersPerSec(1000) to(1) during(10.minutes)
//                    constantUsersPerSec(400) during(5.minutes),
//                    constantUsersPerSec(500) during(5.minutes),
//                    constantUsersPerSec(600) during(5.minutes),
//                    constantUsersPerSec(700) during(5.minutes),
//                    constantUsersPerSec(800) during(5.minutes),
//                    nothingFor(30.minutes),
//                    constantUsersPerSec(400) during(4.hours))
  )).throttle(
    reachRps(100).in(10.minutes),
    holdFor(30.minutes),
    reachRps(400).in(30.minutes),
    holdFor(2.hours),
    reachRps(800).in(30.minutes),
    holdFor(2.hours),
    reachRps(900).in(5.minutes),
    holdFor(2.hours),
    reachRps(1000).in(5.minutes),
    holdFor(1.hours),
    reachRps(100).in(10.minutes)
//    reachRps(400).in(5.seconds),
//    holdFor(5.minutes),
//    reachRps(500).in(5.seconds),
//    holdFor(5.minutes),
//    reachRps(600).in(5.seconds),
//    holdFor(5.minutes),
//    reachRps(700).in(5.seconds),
//    holdFor(5.minutes),
    reachRps(1200).in(5.minutes),
    holdFor(10.minutes)

    //    ,
//    reachRps(0).in(30.minutes)
//    ,
//    reachRps(400).in(5.seconds),
//    holdFor(10.hours),

//    holdFor(30.seconds),
//    jumpToRps(100),
//    holdFor(30.seconds),
//    jumpToRps(200),
//    holdFor(30.seconds),
//    jumpToRps(300),
//    holdFor(30.seconds),
//    jumpToRps(400),
//    holdFor(30.minutes)
//    ,
//    jumpToRps(500),
//    holdFor(30.seconds),
//    jumpToRps(600),
//    holdFor(30.seconds),
//    jumpToRps(700),
//    holdFor(30.seconds),
//    jumpToRps(800),
//    holdFor(30.seconds)
  ).protocols(httpProtocol)

}

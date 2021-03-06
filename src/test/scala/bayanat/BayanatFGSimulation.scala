package bayanat

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BayanatFGSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://10.132.170.225") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val csvFeeder = separatedValues("geolocations.csv", '#').eager.random

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .feed(csvFeeder)
    .exec(http("reverse geocode FG")
      .get("/geocode")
      .queryParam("callerid", "1")
      .queryParam("lat", "${lat}")
      .queryParam("lon", "${lon}")
      .check(status.is(200))
      .check(jsonPath("$..error").notExists)
      .check(jsonPath("$..status").is("200"))
      .check(jsonPath("$..address").is("${address}")))

  setUp(scn.inject(atOnceUsers(100000),
    nothingFor(15.minutes),
    constantUsersPerSec(400) during (15.minutes)
//    nothingFor(30.minutes),
//    constantUsersPerSec(400) during (4.hours))
  ))
    .throttle(
    reachRps(100).in(5.seconds),
    holdFor(15.minutes),
    reachRps(400).in(5.seconds),
    holdFor(15.minutes)
//    reachRps(0).in(5.seconds),
//    holdFor(30.minutes),
//    reachRps(400).in(5.seconds),
//    holdFor(4.hours),
//    reachRps(0).in(30.minutes)
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

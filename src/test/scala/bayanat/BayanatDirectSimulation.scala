package bayanat

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BayanatDirectSimulation extends Simulation {

  val token = "-ebFG8c_Tiogz75w9uDrcegjO58cnoFfvHidXU0YEpv64U5-IvAAoTGFdKO1zKmiGEcxC2Z5jgw8mq937agqWkfqDU6eWXP5y8pYKy3yiR3-D2VaKZm1o3NhjL-5pErQhzE1lR_54sAfnFU16TO9xw.."

  val httpProtocol = http
    .baseUrl("https://maps.bayanat.co.ae") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val csvFeeder = separatedValues("matchAddrGeolocations.csv", '#').eager.random

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .feed(csvFeeder)
    .exec(http("reverse geocode")
      .post("/arcgis/rest/services/FederalCustomsAuthority/ReverseGeocoder/GeocodeServer/reverseGeocode")
      .queryParam("location", "${lon},${lat}")
      .queryParam("token", token)
      .queryParam("f", "json")
      .check(status.is(200))
      .check(jsonPath("$..error").notExists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..Match_addr").is("${address}")))

  setUp(scn.inject(atOnceUsers(80000))).throttle(
    reachRps(1).in(5.seconds),
    jumpToRps(10),
    holdFor(5.minutes),
    jumpToRps(20),
    holdFor(5.minutes),
    jumpToRps(30),
    holdFor(5.minutes),
    jumpToRps(60),
    holdFor(5.minutes),
    jumpToRps(100),
//    holdFor(5.minutes),
//    jumpToRps(200),
//    holdFor(5.minutes),
//    jumpToRps(300),
//    holdFor(5.minutes),
//    jumpToRps(400),
    holdFor(5.minutes)
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

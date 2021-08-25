package bayanat

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val token = "YSBZ3hzexjSV8rOutQ510B_2adhafY3GRdE8YXcoqTXn-KSrIQRww1TyrxkqQ-2ySRij4AAVDl2nxI8T2JEOeBWcr_28cnrjJbcdb448GngNck69izS2j7__BNHANPCukPopilmc4sv2QF3fCwrV_w.."

  val httpProtocol = http
    .baseUrl("https://maps.bayanat.co.ae") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val csvFeeder = separatedValues("geolocations.csv", '#').eager.random

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .feed(csvFeeder)
    .exec(http("reverse geocode")
      .post("/arcgis/rest/services/FederalCustomsAuthority/POIs_Address_Locator/GeocodeServer/reverseGeocode")
      .queryParam("location", "${lon},${lat}")
      .queryParam("token", token)
      .queryParam("f", "json")
      .check(status.is(200))
      .check(jsonPath("$..error").notExists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..LongLabel").is("${address}")))

  setUp(scn.inject(atOnceUsers(50000))).throttle(
    reachRps(100).in(5.seconds),
    holdFor(30.seconds),
    jumpToRps(200),
    holdFor(30.seconds),
    jumpToRps(300),
    holdFor(30.seconds),
    jumpToRps(400),
    holdFor(30.seconds),
    jumpToRps(1000),
    holdFor(30.seconds),
    jumpToRps(1500),
    holdFor(30.seconds),
    jumpToRps(2000),
    holdFor(30.seconds)
  ).protocols(httpProtocol)

}

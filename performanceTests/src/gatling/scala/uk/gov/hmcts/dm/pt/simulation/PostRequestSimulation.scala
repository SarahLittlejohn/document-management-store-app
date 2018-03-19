package uk.gov.hmcts.dm.pt.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef.http
import uk.gov.hmcts.dm.pt.scenarios.PostRequest
import uk.gov.hmcts.dm.pt.util.{Environments, Headers}

import scala.concurrent.duration._
import scala.language.postfixOps

class PostRequestSimulation extends Simulation {

  val httpConf = http.disableWarmUp.baseURL(Environments.dmStoreApp).headers(Headers.commonHeader)

  val testScenarioFinal = scenario("Post").exec(PostRequest.storeScn, PostRequest.fetchScn)

  val randomPostRequest = scenario("Random Post").exec(PostRequest.storeScn)

  val randomGetRequest = scenario("Random GetRequest").exec(PostRequest.fetchScn)

  val postAndGetInSeq = scenario("Post and Get in sequence").exec(PostRequest.storeInSeq, PostRequest.fetchInSeq)

  val testScenarios = List(postAndGetInSeq.inject(rampUsers(18) over (50 minutes)))

  val testScenarioForPostRecords = List(PostRequest.postRequestScenario.inject(atOnceUsers(1),rampUsers(38) over (2 minute)))

  val postRequests2000 = List(PostRequest.postRequestScenario.inject(atOnceUsers(1), rampUsersPerSec(1) to 200 during (1 minute)))

  setUp(testScenarios)
    .protocols(httpConf)
    .maxDuration(50 minutes)
    .assertions(
      global.responseTime.max.lte(Environments.maxResponseTime.toInt),
      global.successfulRequests.percent.gte(99))


  //  val httpConf = http
  //    .baseURL("http://computer-database.gatling.io") // Here is the root for all relative URLs
  //    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
  //    .acceptEncodingHeader("gzip, deflate")
  //    .acceptLanguageHeader("en-US,en;q=0.5")
  //    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

}
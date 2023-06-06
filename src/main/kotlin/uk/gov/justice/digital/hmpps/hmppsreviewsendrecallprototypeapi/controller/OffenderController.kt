package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.controller

import org.apache.commons.lang3.StringUtils.normalizeSpace
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.initialiseDriver
import java.util.UUID

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
internal class OffenderController {

  val ppudUrl = "https://uat.ppud.justice.gov.uk/"

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("/search")
  suspend fun search(
    @RequestParam(required = false) sleepDuration: Long?,
    @RequestParam(required = false) croNumber: String?,
    @RequestParam(required = false) nomsId: String?,
    @RequestParam(required = false) familyName: String?,
    @RequestParam(required = false) dateOfBirth: String?,
  ): List<PpudClient.Offender> {
    val identifier = UUID.randomUUID()
    log.info(normalizeSpace("Offender search endpoint hit $identifier"))
    return performClientOperation(sleepDuration, identifier) { ppudClient ->
      ppudClient.searchForOffender(
        croNumber = croNumber ?: "",
        nomsId = nomsId ?: "",
        familyName = familyName ?: "",
        dateOfBirth = dateOfBirth ?: "",
      )
    }
  }

  @PostMapping("/create", "text/plain")
  suspend fun create(
    @RequestParam(required = false) sleepDuration: Long?,
    @RequestBody(required = true) newOffender: PpudClient.NewOffender,
  ): PpudClient.Offender {
    val identifier = UUID.randomUUID()
    log.info(normalizeSpace("Offender create endpoint hit"))
    return performClientOperation(sleepDuration, identifier) { ppudClient ->
      ppudClient.createOffender(newOffender)
    }
  }

  suspend fun <T> performClientOperation(
    sleepDuration: Long?,
    identifier: UUID?,
    operation: suspend (ppudClient: PpudClient) -> T,
  ): T {
    val driver = initialiseDriver()
    try {
      log.info("Starting performClientOperation $identifier")
      val ppudClient = PpudClient()
      ppudClient.driver = driver
      ppudClient.ppudUrl = ppudUrl
      ppudClient.sleepDurationInMilliseconds = sleepDuration ?: 0
      return operation(ppudClient)
    } finally {
      driver.close()
      log.info("Finally of performClientOperation $identifier")
    }
  }
}

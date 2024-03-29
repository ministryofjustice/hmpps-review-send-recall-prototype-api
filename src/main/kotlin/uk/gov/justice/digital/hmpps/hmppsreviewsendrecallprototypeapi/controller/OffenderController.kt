package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.controller

import org.apache.commons.lang3.StringUtils.normalizeSpace
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import java.util.*

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
internal class OffenderController {

  val ppudUrl = "https://internaltest.ppud.justice.gov.uk/"

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

  @PostMapping("/create", "application/json")
  suspend fun create(
    @RequestParam(required = false) sleepDuration: Long?,
    @RequestBody(required = true) newOffender: PpudClient.NewOffender,
  ): PpudClient.Offender {
    val identifier = UUID.randomUUID()
    log.info(normalizeSpace("Offender create endpoint hit $identifier"))
    return performClientOperation(sleepDuration, identifier) { ppudClient ->
      ppudClient.createOffender(newOffender)
    }
  }

  @PostMapping("/recall", "application/json")
  suspend fun createRecall(
    @RequestParam(required = false) sleepDuration: Long?,
    @RequestBody(required = true) newRecall: PpudClient.NewRecall,
  ): PpudClient.Recall {
    val identifier = UUID.randomUUID()
    log.info(normalizeSpace("Recall create endpoint hit $identifier"))
    return performClientOperation(sleepDuration, identifier) { ppudClient ->
      ppudClient.createRecall(newRecall)
    }
  }

  suspend fun <T> performClientOperation(
    sleepDuration: Long?,
    identifier: UUID?,
    operation: suspend (ppudClient: PpudClient) -> T,
  ): T {
    log.info("Starting performClientOperation $identifier")
    val ppudClient = PpudClient(ppudUrl, sleepDuration ?: 0)
    try {
      return operation(ppudClient)
    } finally {
      ppudClient.quit()
      log.info("Finally of performClientOperation $identifier")
    }
  }
}

package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.controller

import org.apache.commons.lang3.StringUtils.normalizeSpace
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
internal class OffenderCreateController(
  private val ppudClient: PpudClient,
) {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @PostMapping("/create", "text/plain")
  suspend fun create(
    @RequestParam(required = false) sleepDuration: Long?,
    @RequestBody(required = true) newOffender: PpudClient.NewOffender,
  ): PpudClient.Offender {
    log.info(normalizeSpace("Offender create endpoint hit"))
    ppudClient.ppudUrl = "https://uat.ppud.justice.gov.uk/"
    ppudClient.sleepDurationInMilliseconds = sleepDuration ?: 0
    return ppudClient.createOffender(newOffender)
  }
}

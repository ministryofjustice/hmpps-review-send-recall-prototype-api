package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.controller

import org.apache.commons.lang3.StringUtils.normalizeSpace
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
internal class OffenderSearchController(
  private val ppudClient: PpudClient,
) {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("/search")
  suspend fun search(
    @RequestParam(required = false) croNumber: String?,
    @RequestParam(required = false) nomsId: String?,
    @RequestParam(required = false) familyName: String?,
    @RequestParam(required = false) dateOfBirth: String?,
  ): List<PpudClient.Offender> {
    log.info(normalizeSpace("Offender search endpoint hit"))
    ppudClient.ppudUrl = "https://uat.ppud.justice.gov.uk/"
    return ppudClient.searchForOffender(
      croNumber = croNumber ?: "",
      nomsId = nomsId ?: "",
      familyName = familyName ?: "",
      dateOfBirth = dateOfBirth ?: "",
    )
  }
}
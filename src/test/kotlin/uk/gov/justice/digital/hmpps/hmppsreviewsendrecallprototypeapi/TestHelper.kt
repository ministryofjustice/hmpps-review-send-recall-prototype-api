package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi

import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.RiskOfSeriousHarmLevel

fun generateValidNewRecall(documents: List<PpudClient.DocumentForUpload> = emptyList()): PpudClient.NewRecall {
  return PpudClient.NewRecall(
    offenderId = "4F6666656E64657269643D313236313234G709H670",
    sentenceDate = "12/06/2008",
    releaseDate = "07/01/2009",
    probationArea = "PS - Accrington",
    isInCustody = true,
    mappaLevel = "Level 1 – Multi-Agency Support",
    decisionDateTime = "12/06/2023 09:10",
    receivedDateTime = "12/06/2023 11:30",
    recommendedToOwner = "Peter Jones(WAM - Panel Member)",
    policeForce = "West Yorkshire Police",
    documents = documents,
    isExtendedSentence = false,
    riskOfSeriousHarmLevel = RiskOfSeriousHarmLevel.Low,
  )
}

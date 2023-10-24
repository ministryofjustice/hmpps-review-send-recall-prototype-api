package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi

import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.RiskOfSeriousHarmLevel

fun generateValidNewRecall(documents: List<PpudClient.DocumentForUpload> = emptyList()): PpudClient.NewRecall {
  return PpudClient.NewRecall(
    offenderId = "4F6666656E64657249643D313731383133G688H664",
    sentenceDate = "16/10/2023",
    releaseDate = "17/10/2023",
    probationArea = "PS - Croydon",
    isInCustody = true,
    mappaLevel = "Level 3 – MAPPP",
    decisionDateTime = "12/06/2023 09:10",
    receivedDateTime = "12/06/2023 11:30",
    recommendedToOwner = "Peter Bowes(WAM - Panel Member)",
    policeForce = "West Yorkshire Police",
    documents = documents,
    isExtendedSentence = false,
    riskOfSeriousHarmLevel = RiskOfSeriousHarmLevel.Low,
  )
}

package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi

import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

fun generateValidNewRecall(): PpudClient.NewRecall {
  return PpudClient.NewRecall(
    offenderId = "4F6666656E64657269643D313937363935G722H678",
    sentenceDate = "10/07/2014",
    releaseDate = "27/10/2014",
    probationArea = "PS - Accrington",
    isInCustody = false,
    mappaLevel = "Level 1 – Multi-Agency Support",
    decisionDateTime = "12/06/2023 09:10",
    receivedDateTime = "12/06/2023 11:30",
    recommendedToOwner = "Peter Jones(WAM - Panel Member)",
    policeForce = "West Yorkshire Police",
    isPartAMissing = true,
    isOASysMissing = true,
    isPreSentenceReportMissing = true,
    isPreConsMissing = true,
    isLicenceMissing = true,
    isChargeSheetMissing = true,
  )
}

import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  private val excludedPackages: Seq[String] = Seq(
    "<empty>",
    "Reverse.*",
    "app.*",
    "uk.gov.hmrc.BuildInfo",
    "prod.*",
    ".*Routes.*",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*",
    ".*.views.html.*",
    ".*models.*",
    ".*viewmodels.*",
    ".*queries.*"
  )

  private val excludedFiles: Seq[String] = Seq(
    ".*Algebra",
    ".*ViewUtils",
    ".*ErrorTemplate",
    ".*Layout",
    ".*Heading",
    ".*forms.mappings.Formatters",
    ".*forms.mappings.Constraints",
    ".*forms.mappings.Mappings"
  )

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageExcludedFiles := excludedFiles.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 79,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

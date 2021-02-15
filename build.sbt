// gallia-clinvar

// ===========================================================================
lazy val root = (project in file("."))
  .settings(
    name         := "gallia-clinvar",
    version      := "0.1.0",
    scalaVersion := "2.13.4" /* TODO: inherit from core */)
  .dependsOn(RootProject(file("../gallia-core")))

// ===========================================================================
// TODO: more + inherit from core
scalacOptions in Compile ++=
  Seq("-Ywarn-value-discard") ++ 
  (scalaBinaryVersion.value match {
    case "2.13" => Seq("-Ywarn-unused:imports")
    case _      => Seq("-Ywarn-unused-import" ) })

// ===========================================================================

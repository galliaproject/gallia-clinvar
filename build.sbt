// gallia-clinvar

// ===========================================================================
lazy val root = (project in file("."))
  .settings(
    organizationName     := "Gallia Project",
    organization         := "io.github.galliaproject", // *must* match groupId for sonatype
    name                 := "gallia-clinvar",
    version              := "0.3.0",    
    homepage             := Some(url("https://github.com/galliaproject/gallia-clinvar")),
    scmInfo              := Some(ScmInfo(
        browseUrl  = url("https://github.com/galliaproject/gallia-clinvar"),
        connection =     "scm:git@github.com:galliaproject/gallia-clinvar.git")),
    licenses             := Seq("BSL 1.1" -> url("https://github.com/galliaproject/gallia-clinvar/blob/master/LICENSE")),
    description          := "A Scala library for data manipulation" )
  .settings(GalliaCommonSettings.mainSettings:_*)

// ===========================================================================    
lazy val galliaVersion = "0.3.0"

// ---------------------------------------------------------------------------
libraryDependencies += "io.github.galliaproject" %% "gallia-core" % galliaVersion // in turns depends on aptus-core

// ===========================================================================
sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"
sonatypeCredentialHost :=         "s01.oss.sonatype.org"        
publishMavenStyle      := true
publishTo              := sonatypePublishToBundle.value

// ===========================================================================

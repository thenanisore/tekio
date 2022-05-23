// Root package versions
val catsVersion            = "1.6.0"
val circeVersion           = "0.11.1"
val circeConfigVersion     = "0.6.1"
val doobieVersion          = "0.7.0-M2"
val enumeratumVersion      = "1.5.13"
val enumeratumCirceVersion = "1.5.20"
val enumeratumCatsVersion  = "1.5.14"
val fuuidVersion           = "0.2.0-M8"
val http4sVersion          = "0.20.0-M5"
val log4catsVersion        = "0.3.0"
val logbackVersion         = "1.2.3"
val monixVersion           = "3.0.0-RC2"
val shapelessVersion       = "2.3.3"
val tsecVersion            = "0.1.0-M2"

lazy val rootDependencies = Seq(
  "ch.qos.logback"    % "logback-classic"       % logbackVersion,
  "com.chuusai"       %% "shapeless"            % shapelessVersion,
  "com.beachape"      %% "enumeratum"           % enumeratumVersion,
  "com.beachape"      %% "enumeratum-circe"     % enumeratumCirceVersion,
  "com.beachape"      %% "enumeratum-cats"      % enumeratumCatsVersion,
  "io.circe"          %% "circe-generic"        % circeVersion,
  "io.circe"          %% "circe-generic-extras" % circeVersion,
  "io.circe"          %% "circe-config"         % circeConfigVersion,
  "io.chrisdavenport" %% "log4cats-log4s"       % log4catsVersion,
  "io.chrisdavenport" %% "fuuid"                % fuuidVersion,
  "io.chrisdavenport" %% "fuuid-doobie"         % fuuidVersion,
  "io.chrisdavenport" %% "fuuid-circe"          % fuuidVersion,
  "io.monix"          %% "monix"                % monixVersion,
  "org.http4s"        %% "http4s-blaze-server"  % http4sVersion,
  "org.http4s"        %% "http4s-circe"         % http4sVersion,
  "org.http4s"        %% "http4s-dsl"           % http4sVersion,
  "org.typelevel"     %% "cats-core"            % catsVersion,
  // db
  "org.tpolecat" %% "doobie-core"      % doobieVersion,
  "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
  "org.tpolecat" %% "doobie-scalatest" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari"    % doobieVersion,
  // authentication dependencies
  "io.github.jmcardon" %% "tsec-common"     % tsecVersion,
  "io.github.jmcardon" %% "tsec-password"   % tsecVersion,
  "io.github.jmcardon" %% "tsec-mac"        % tsecVersion,
  "io.github.jmcardon" %% "tsec-signatures" % tsecVersion,
  "io.github.jmcardon" %% "tsec-jwt-mac"    % tsecVersion,
  "io.github.jmcardon" %% "tsec-jwt-sig"    % tsecVersion,
  "io.github.jmcardon" %% "tsec-http4s"     % tsecVersion
)

// Test package versions
val scalaTestVersion  = "3.0.5"
val scalacheckVersion = "1.14.0"
val scalaMockVersion  = "4.1.0"

lazy val testDependencies = Seq(
  "org.http4s"     %% "http4s-blaze-client" % http4sVersion,
  "org.scalatest"  %% "scalatest"           % scalaTestVersion,
  "org.scalacheck" %% "scalacheck"          % scalacheckVersion,
  "org.scalamock"  %% "scalamock"           % scalaMockVersion
).map(_ % Test)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Xcheckinit",
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xfuture",
  "-Xlint:adapted-args",
  "-Xlint:by-name-right-associative",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Xlint:unsound-match",
  "-Yno-adapted-args",
  "-Ypartial-unification"
)

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    organization := "me.oeshiro",
    name := "tekio",
    version := "0.0.11",
    scalaVersion := "2.12.15",
    scalacOptions ++= compilerOptions,
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies ++= rootDependencies ++ testDependencies,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "me.oeshiro.tekio.utils",
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.3.0-M4")
  )

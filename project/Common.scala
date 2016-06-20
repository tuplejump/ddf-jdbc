import sbt._
import sbt.Keys._
import sbt.Classpaths.publishTask

object Common {
  lazy val rootOrganization = "io"
  lazy val rootProjectName = "ddf"
  lazy val ddfVersion = "1.4.19-SNAPSHOT"
  lazy val theScalaVersion = "2.10.4"
  lazy val MavenCompile = config("m2r") extend(Compile)
  lazy val publishLocalBoth = TaskKey[Unit]("publish-local", "publish local for m2 and ivy")
  lazy val submodulePom = (
    <!--
      **************************************************************************************************
      IMPORTANT: This file is generated by "sbt make-pom" (bin/make-poms.sh). Edits will be overwritten!
      **************************************************************************************************
      -->
      <parent>
        <groupId>{rootOrganization}</groupId>
        <artifactId>{rootProjectName}</artifactId>
        <version>{ddfVersion}</version>
      </parent>
      <build>
        <plugins>
          <plugin>
            <groupId>net.alchim31.maven</groupId>
            <artifactId>scala-maven-plugin</artifactId>
            <version>3.1.5</version>
            <configuration>
              <recompileMode>incremental</recompileMode>
            </configuration>
          </plugin>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.7</version>
            <configuration>
              <skipTests>true</skipTests>
            </configuration>
          </plugin>
          <plugin>
            <groupId>org.scalatest</groupId>
            <artifactId>scalatest-maven-plugin</artifactId>
            <version>1.0</version>
            <configuration>
              <reportsDirectory>${{basedir}}/surefire-reports</reportsDirectory>
              <junitxml>.</junitxml>
              <filereports>scalatest_reports.txt</filereports>
            </configuration>
            <executions>
              <execution>
                <id>test</id>
                <goals>
                  <goal>test</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    )
  lazy val commonSettings = Seq(
    organization := "io.ddf",
    version := ddfVersion,
    retrieveManaged := true, // Do create a lib_managed, so we have one place for all the dependency jars to copy to slaves, if needed
    scalaVersion := theScalaVersion,
    scalacOptions := Seq("-unchecked", "-optimize", "-deprecation"),
    fork in Test := true,
    parallelExecution in ThisBuild := false,
    libraryDependencies ++= Seq(
    "io.ddf" %% "ddf_core" % ddfVersion % "provided"),
    javaOptions in Test ++= Seq("-Xmx2g"),
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    otherResolvers := Seq(Resolver.file("dotM2", file(Path.userHome + "/.m2/repository"))),
    publishLocalConfiguration in MavenCompile <<= (packagedArtifacts, deliverLocal, ivyLoggingLevel) map {
      (arts, _, level) => new PublishConfiguration(None, "dotM2", arts, Seq(), level)
    },
    publishMavenStyle in MavenCompile := true,
    publishLocal in MavenCompile <<= publishTask(publishLocalConfiguration in MavenCompile, deliverLocal),
    publishLocalBoth <<= Seq(publishLocal in MavenCompile, publishLocal).dependOn
  )
  scalaVersion in ThisBuild := "2.10.X"
  
}

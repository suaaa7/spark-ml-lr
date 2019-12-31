logLevel := Level.Warn

resolvers += "twitter-repo" at "https://maven.twttr.com"

addSbtPlugin("com.github.gseitz"  % "sbt-release"         % "1.0.12")
addSbtPlugin("com.lucidchart"     % "sbt-scalafmt"        % "1.16")
addSbtPlugin("com.timushev.sbt"   % "sbt-updates"         % "0.5.0")
addSbtPlugin("com.twitter"        % "scrooge-sbt-plugin"  % "19.10.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager" % "1.4.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"             % "0.3.7")

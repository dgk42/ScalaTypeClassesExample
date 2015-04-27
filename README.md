      _________             .__
     /   _____/ ____ _____  |  | _____
     \_____  \_/ ___\\__  \ |  | \__  \
     /        \  \___ / __ \|  |__/ __ \_
    /_______  /\___  >____  /____(____  /
            \/     \/     \/          \/
    ___________
    \__    ___/__.__.______   ____
      |    | <   |  |\____ \_/ __ \
      |    |  \___  ||  |_> >  ___/
      |____|  / ____||   __/ \___  >
              \/     |__|        \/
    _________ .__
    \_   ___ \|  | _____    ______ ______ ____   ______
    /    \  \/|  | \__  \  /  ___//  ___// __ \ /  ___/
    \     \___|  |__/ __ \_\___ \ \___ \\  ___/ \___ \
     \______  /____(____  /____  >____  >\___  >____  >
            \/          \/     \/     \/     \/     \/
    ___________                             .__
    \_   _____/__  ________    _____ ______ |  |   ____
     |    __)_\  \/  /\__  \  /     \\____ \|  | _/ __ \
     |        \>    <  / __ \|  Y Y  \  |_> >  |_\  ___/
    /_______  /__/\_ \(____  /__|_|  /   __/|____/\___  >
            \/      \/     \/      \/|__|             \/

# Type classes example in Scala
***by dgk42***

## Requirements

- JDK 1.7 or higher

- Sbt
  For instructions on installing sbt on your system, see [this](http://www.scala-sbt.org/0.13/tutorial/Manual-Installation.html).

## Contents

Goal: We want to write a class to express approximate equality. We don't want the example to be dead trivial, so we used an auxiliary trait that expresses the distance property for a type. Please bear in mind that this is just an example - it's easy to understand that a pragmatic implementation of approximate equality could deviate from this example.

How-to: We implement the above in 2 different ways:
a) using parametric polymorphism. For implementation details, see the ApproxG.scala file
b) using ad-hoc polymorphism with type classes. For implementation details, see the ApproxTC.scala file

Rule of thumb: The `G` in a class name implies (a), while the `TC` implies (b).

Property testing using [ScalaCheck](https://github.com/rickynils/scalacheck/wiki/User-Guide) is also implemented for both (a) and (b).

Note 1: We deliberately import the [Scalaz](https://github.com/scalaz/scalaz) framework, since most of the time one shall need stuff from there when working with these.

Note 2: To see important comments, issue the following:
`grep -nr NOTE src`
in the project's root directory.

## Building and running

`sbt compile test`
`sbt "runMain dgk.approxequal.GExample"`
`sbt "runMain dgk.approxequal.TCExample"`

- ScalaIDE
  To create an Eclipse project ready to be imported in the IDE, fire `sbt eclipse`

- IntelliJ
  Use the IDEA sbt plug-in to load the project

## Presentation

There's also a `.pdf` located in the root directory covering a presentation about the topic.

## Exercises for the reader

- TODO tasks
  `grep -nr TODO src`

- Remove the WithDistance trait and provide a simpler implementation
  See how many implicits you can prune!

- ScalaCheck reports that the transitive property holds. Try to tweak it, so it can find a counter-example
  In the example apps (`GExample` and `TCExample`) you can find more clues.

- Try to implement a type class of your own.

## Final words

- We love FP!

- We love Haskell!

- We love Scalaz!

# ActiveSlick

[![Build Status](https://travis-ci.org/strongtyped/active-slick.svg?branch=develop)](https://travis-ci.org/strongtyped/active-slick)

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/strongtyped/active-slick?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


ActiveSlick is a library that offers CRUD operations for Slick 3.x projects. The main goal is to provide some basic operations to manage the lifecycle of persisted objects (new/persisted/deleted/stale) and enable the implementation of the Active Record Pattern on Slick mapped case classes.

### Documentation (WIP)
http://www.strongtyped.io/active-slick/

### Main features
- Basic CRUD and auxiliary methods - add/update/save, delete, findById, count and fetchAll (backed by Reactive Streams).
- Generic Id type. 
- Optimistic locking my means of incremental version.
- Before insert and update hooks.
- **ActiveRecord** trait to enable the Active Record Pattern on mapped case classes via class extensions (pimp-my-library style)

### Project artifact

The artifacts are published to Sonatype Repository. Simply add the following to your build.sbt.

As of version 0.3.x we don't support Slick 2.0 anymore. The differences between Slick 2.x and Slick 3.x are so huge that it makes impossible to support two versions. 

```scala
  libraryDependencies += "io.strongtyped" %% "active-slick" % "0.3.3"
```
  
Source code for version 0.3.3 can be found at:
https://github.com/strongtyped/active-slick/tree/v0.3.3


The version supporting Slick 2.0 is still available on Sonatype Repo. However, this documentation only covers the current version (i.e.: 0.3.1).

```scala
  libraryDependencies += "io.strongtyped" %% "active-slick" % "0.2.2"
```

Source code for version 0.2.2 can be found at:
https://github.com/strongtyped/active-slick/tree/v0.2.2

### Contribution policy

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

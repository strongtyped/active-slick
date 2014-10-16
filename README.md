# ActiveSlick

[![Build Status](https://travis-ci.org/strongtyped/active-slick.svg?branch=develop)](https://travis-ci.org/strongtyped/active-slick)

ActiveSlick is a library that offers CRUD operations for Slick 2.1 projects. The main goal is to provide some basic operations to manage the lifecycle of persisted objects (new/persisted/deleted/stale) and enable the implementation of the Active Record pattern on Slick mapped case classes.

All operations are provided by `TableQueries` sub-classes. 

### Main features
- Basic CRUD and auxiliary methods - add/update/save, delete, list, pagedList, (paged result), findById and count.
- Model classes don't need to implement any specific class or trait,
  although for convenience you can extend `Identifiable` or `Versionable` traits.  
- Generic Id type. 
- `Identifiable` trait and respective `EntityTable` to manage Entities.
- `Versionable` trait and respective `VersionableEntityTable` for optimistic locking.
- `ActiveRecord` trait to enable the Active Record pattern on mapped case classe via class extensions (pimp-my-library style)


## Project artifact

The latest release (0.2.1) is available on Sonatype's repository. 
This is a crossbuild for Scala 2.10 and 2.11 and and Slick 2.1. 

    libraryDependencies += "io.strongtyped" % "active-slick_2.10" % "0.2.1"
or
    libraryDependencies += "io.strongtyped" % "active-slick_2.11" % "0.2.1"
or 
    libraryDependencies += "io.strongtyped" %% "active-slick" % "0.2.1"


## Usage 

Usage information can be found on the [wiki pages](https://github.com/strongtyped/active-slick/wiki/Introduction).

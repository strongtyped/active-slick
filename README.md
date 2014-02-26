# slick-dao

Generic DAO for Slick is a tiny library that offers CRUD operations for Slick 2.0 mapped classes.

### Main features
- basic CRUD and auxiliary methods - add/update/save, delete, pages (paged result), findById and count
- Model classes don't need to implement any specific class or trait. No base Entity trait whatsoever. 
- Generic Id type
- ActiveRecord implicit conversion

### TODO

- testing, some examples and docs
- a Versionable trait to reinforce versioning
- investigate how we can have DAO for rows with compound keys instead of PKs. Should be possible by defining key as a tuple and mapping it as such
- error handling strategy - what if entity or record doesn’t exist. Exception? Either? ScalaUtils.Or? 
- macro to generate some helper methods like: findByName, findByBirthday, etc. 
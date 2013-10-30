# CingleVue Coding Challenge #

Introduction
------------
This project has been developed to meet the coding challenge from CingleVue.

Technologies Used
-----------------
The technologies used to develop this project are:
  1. [Scala][]
  2. [Reactive Mongo (Asynchronous access to MongoDB)][ReactiveMongo]
  3. [Scalatra framework (For developing the REST API)][Scalatra]
  4. [Angular (For the UI layer)][Angular]


Build & Run
-----------
To build & run this project, you will need to have [SBT][] preinstalled. Once you have [SBT][] installed you need to setup an `application.conf` file in the `src/main/resources` directory. A sample has been provided. Just populate your own values. A key thing to note, you can define the database in the `application.conf` file, but the Mongo collection used **HAS** to be named **Schools**

```sh
$ cd CingleVue_Coding_Challence
$ sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
    
Loading Sample Data
-------------------
There is a script in the `tests` which uses `application.conf` to load data into the relevant collection. Just execute
```sbt
>test:run-main com.cinglevue.challenge.FixtureLoader
```

[SBT]: http://www.scala-sbt.org/ "SBT"
[Scala]: http://www.scala-lang.org/ "Scala Language"
[ReactiveMongo]: http://reactivemongo.org/ "Reactive Mongo"
[Scalatra]: http://www.scalatra.org/ "Scalatra Framework"
[Angular]: http://angularjs.org/ "Angular JS"

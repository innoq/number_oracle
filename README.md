# Number guessing game sparring partner

This repository provides a sparring partner for a number determination game.

The intended use-case is, you write an automated game player in a programming language of your choice and have some fun.

## The game's rules.

A number is hidden from you. Your objective in the game is to determine that number, and to do so with as few steps as you can.  You are provided with an oracle to do so.

The space of numbers from which the hidden number can be taken is limited in each individual game, in two ways:

* The number has precisely `D` digits.
* Each individual digit is in the range `0 .. (B-1)`, where `B` is the _base_ of the game.

Those parameters `B` and `D` are known before you start the game.

In each individual step, you present the oracle with one number from the guessing space.  If that number matches (is the same as) the hidden number, you've completed that game successfully.

If not, you are given the oracle's verdict, in the form of two counts, the _full matches_ count and the _partial matches_ (or wrong position match) count.

* For the _full match count_, the oracle iterates through the digit positions. For each digit index, the oracle checks whether the digit in your number at that index is the same as that from the hidden number at the same index. If so, one is added to the full match count, and the pertinent digit is replaced by a unique symbol (that will never match anything) in both the hidden and your number.
* For the _partial match count_, the oracle searches for digit values that occure in what's left of your and the hidden number, after symbol replacement.  So if your and the hidden number contain the same digit value (never mind the digit position), one is added to the partial match count and the two digits thus matched are again replaced with a unique symbol each (that will never match anything). The search continues until there is no common digit value in your and the hidden number.

## The game and oracle REST JSON API

You do stuff against this API by issuing a POST against an URL.  You provide a JSON payload. You are supposed to set your `Accept:` header to prefer `application/json`, then you'll receive a JSON payload as described below.

As is customary, this API will and your software should ignore any additional JSON data not documented here.

### One divination step

To submit a number to game's oracle as the next step you make, you obviously need to know that game's oracle URI and the parameter's `B` and `D`.

An example submission for `B = 6` and `D = 4` might be

    { "submission": [0, 5, 1, 1] }

If the hidden code happens to be

    [1, 0, 1, 3]

the oracle will answer with JSON

    { "full_match_count": 1, "partial_match_count": 2 }

You're supposed to discern yourself whether `full_match_counts` equals `D`, in which case you've completed the game and should discontinue use of this game's URI.

### Starting a new game

For this, you post JSON to the game-making URI like so:

    { "base": 6, "length": 4, "oracle_type": "fair" }

Of course, `base` is synonymous for `B` and `length` synonymous for `D`.

The parameter `base` needs to be an integer at least 2, but no more than, say, 100, and the parameter `length` an integer at least 1, but reasonably small (up to 40 or so should not pose a big problem).  Try the above values for a start.

In reply, you'll receive a redirect to the oracle URI, discussed above.  If you approach the oracle URI with a normal GET request, it will answer with JSON that contains that same oracle URI in a "self" field, and also the game's parameters:

    { "self": "http....", "base": 6, "length": 4, "oracle_type": "fair" }

#### Oracle types

There are three types of oracles:

* "fair" (the normal case) will dice out a random hidden number and answer your questions based on that number, as explained above.
* "nice" (a somewhat boring case) will change the hidden number in your favor upon your first request - so you always immediately receive a `full_match_count` equals to `length`.
* "evil" (this may or may not get implemented any time soon) will change the hidden number from under you, so as to give you the answer it considers least informative for you. But it will be bound by whatever answers it has already given. So you cannot easily tell the difference between the evil and the fair oracle (other than through statistics or run-time observation).

### Starting a contest

Post, to the contest creation URI, JSON like so:

    { "base": 6, "length": 4, "oracle_type": "fair", "games": 100}

You can also split a contest to work with several oracles.  Each oracle needs to provide a game-maing URI as above.  This could be particularily interesting to make several evil oracles compete:

    {
      "base": 6, "length": 4, "oracle_type": "evil", 
      "games_per_orclefactory": 10,
      "oraclefactories": [
         "(game-making-URI 1)",
         "(game-making-URI 2)",
         ...
         "(game-making-URI n)"
      ]
    }

In response to a contest creation request, you'll be redirected to a contest watch URI.  A GET to that URI returns JSON like this:

    {
       "self": "http...(content watch URI)",
       "entry": "http...(content entry URI)",
       "base": 6,
       "length": 4,
       "oracle_type": "fair",
       "games": 100
    }

A team or individual software developer may enter the content by posting JSON to the content entry URI like so:

    {
        "team_name": "(your team name)",
        "software_version": "(a version string)"
    }

Both team name and software version string should be 20 characters or less.

The response will be JSON containing individual oracle URIs, each as described above.

    { "oracle_URIs": [ "http...", "http...", ... ] }

The individual URIs given will differ from team to team, but the set of hidden codes will remain the same (not necessarily in the same order, though).

The same team may enter the same competition again, with a new software version. Doing so demotes that team to "training mode". This is so people don't start playing contests twice, using the set of numbers they saw the first time for short-cuts the second time.

To be future-proof, your client should not assume that all games in a certain contest share common `base` and `length` values.  Instead, it should fire a HTTP GET against each individual oracle URI, which will provide those values (as described above).

## How to run

* Install Java 8
* Install Maven
* Have a clone of this repository on your local hard drive
* run `mvn clean install`

### JVM access to basic oracle

This is a side line.  If you want the real thing, just skip to the next paragraph and set up the database.

Basic oracle functionality can be accessed locally. Your main entrypoint is the static method `makeRandomFairOracle(int length, int base)` in class `com.innoq.numbergame.base.OracleFactory`.

There is sample code in the JUnit test `base_used_by_java/src/test/java/com/innoq/numbergame/base/RandomOracleTest.java` which is run as part of `mvn clean install`. To run it manually at your `bash` prompt, use

    r="$HOME/.m2/repository"
    CLASSPATH="$r/org/scala-lang/scala-library/2.11.5/scala-library-2.11.5.jar"
    CLASSPATH="$CLASSPATH:base/target/classes"
    CLASSPATH="$CLASSPATH:$r/junit/junit/4.12/junit-4.12.jar"
    CLASSPATH="$CLASSPATH:$r/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
    CLASSPATH="$CLASSPATH:base_used_by_java/target/test-classes"
    export CLASSPATH
    java org.junit.runner.JUnitCore com.innoq.numbergame.base.RandomOracleTest

### Create database

Install PostgreSQL on your machine.

Initialize a database and a database user by running the `psql` command, as the `postgres` admin user (or some other admin user), and type at the SQL prompt:

    CREATE ROLE number_oracle_db_user LOGIN PASSWORD 'xxx';
    CREATE DATABASE number_oracle_db;
    \q

Next, stil as the DB admin user, run

    psql -d number_oracle_db -a --variable=ON_ERROR_STOP=true -f create_db_postgres.sql

Back as the user that will be running the application, set that password as
an environment value:

    NUMBER_ORACLE_DB_PASSWORD=xxx
    export NUMBER_ORACLE_DB_PASSWORD

(Hopefully, you changed the `xxx` password to something more complicated.)

### Running the server

A server that aims to implement the above protocol (not yet fully functional)
can be run as follows:

* Do the stuff in "How to run" above.
* Do the stuff in "Create the database" above.
* Install Play, or, more precisely, unzip `typesafe-activator-1.2.12.zip` .
* Copy `activator-launch-1.2.12.jar` to the directory `oracle-rest-api` here.
* Create a directory `oracle-rest-api/lib` if it's not already there,
  and copy `base/target/base-0.3-SNAPSHOT.jar` to that directory.
* In the directory `oracle-rest-api` (having the `NUMBER_ORACLE_DB_PASSWORD` environment varialbe set as above), run `./activator start` to fire up the server.

TODO: This need to be somewhat more automated.

### Trying it out

Create a new nice oracle with

    curl -v --data-binary '{"base": 6, "length": 4, "oracle_type": "nice"}' \
    -H 'Content-Type: application/json; charset=utf-8' \
    -H 'Accept: application/json' http://localhost:9000/neworacle

This should return a `303 See Other` with a `Location: ` header giving the URI of the newly created nice oracle.

If this is the first thing you ever did,
`/oracle/1` would be that URI.  Do a `GET` to that
location as often as you like:

    curl -v -H 'Accept: application/*' http://localhost:9000/oracle/1

This should give you `200 OK` with a JSON body containing at least:

    {"base":6,"length":4,"self":"/oracle/1","type":"nice"}

You can guess (as it's a _nice_ oracle, it'll always let you succeed on your first try):

    curl -v --data-binary '{"submission": [0,2,4,5]}' \
    -H 'Content-Type: application/json; charset=utf-8' \
    -H 'Accept: application/json' http://localhost:9000/oracle/1

This should give you a `200 OK` and the answer

    {"full_match_count":4,"partial_match_count":0}

Any oracle should be left alone after solved. If you rerun the same POST, you'd deserve, and get, a `400 Bad Request`.

## An example session against a fair oracle

The precise values you get might change.

    curl -v --data-binary '{"base": 6, "length": 4, "oracle_type": "fair"}' \
    -H 'Content-Type: application/json; charset=utf-8' \
    -H 'Accept: application/json' http://localhost:9000/neworacle

    ...
    HTTP/1.1 303 See Other
    Location: /oracle/2
    Content-Length: 0

    curl -v -H 'Accept: application/json' http://localhost:9000/oracle/2

    {"self":"/oracle/2","base":6,"length":4,"type":"fair"}

    curl -v --data-binary '{"submission": [0,0,1,2]}' \
    -H 'Content-Type: application/json; charset=utf-8' \
    -H 'Accept: application/json' http://localhost:9000/oracle/2

(And then it doesn't presently work, as it doesn't read out BLOBs.)

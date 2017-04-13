# Introduction

This simple application goal is to parse entries from bash.org.pl
In order to reach its goal it uses:
- Typesafe Config
- Akka Streams

# Running
From project directory run:
    sbt "run x"

where x is number of pages you whish to parse grater then 0
In case of invalid input application should return proper information
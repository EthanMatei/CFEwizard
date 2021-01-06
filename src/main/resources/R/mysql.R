#!/usr/bin/env Rscript

library(RMySQL)
library(properties)

properties <- read.properties("~/.cfe.properties")
print(properties)

dbUsername <- toString(properties['db.username'])
dbPassword <- toString(properties['db.password'])

mydb <- dbConnect(MySQL(), user = dbUsername, password = dbPassword, dbname='cfe',host='127.0.0.1')

queryString <- "SELECT version FROM flyway_schema_history"
query <- dbSendQuery(mydb, queryString)
data <- fetch(query, n=-1) # n=-1

print(data)

print(dbHost)


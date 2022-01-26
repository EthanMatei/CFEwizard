# Packages that are needed
wants <- c("plyr", "gtools", "dplyr", 'properties', 'RMySQL')

print(wants)
has   <- wants %in% rownames(installed.packages())
print(has)

if(any(!has)) install.packages(wants[!has])

library(gtools)
library(plyr)
library(dplyr)
library(properties)
library(RMySQL)

myProps <- read.properties("~/.cfe.properties")

print(myProps)

dbHost     <- myProps['db.host']
dbUsername <- myProps['db.username']
dbPassword <- myProps['db.password']

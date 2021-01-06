#!/usr/bin/env Rscript
library('RJDBC')

args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
    stop("No database file specified")
}

print(tempdir())

dbFile <- args[1]
print(dbFile)

scriptDir <- args[2]
print(paste("Script dir:", scriptDir))

cat("Before JDBC call")
cat(paste("Current directory:", getwd()))

# Using Ucanacess Driver
# Need to download this, unzip, and put all jars in directory with script
jars = paste(
    paste(scriptDir, "commons-lang3-3.8.1.jar", sep="/"),
    paste(scriptDir, "commons-logging-1.2.jar", sep="/"),
    paste(scriptDir, "hsqldb-2.5.0.jar", sep="/"),
    paste(scriptDir, "jackcess-3.0.1.jar", sep="/"),
    paste(scriptDir, "ucanaccess-5.0.0.jar", sep="/"),    
    sep=":"
)

print(jars)

driver <- JDBC(
    "net.ucanaccess.jdbc.UcanaccessDriver",
    jars,
    identifier.quote="`"
)

print("After JDBC call")


dbUrl <- paste("jdbc:ucanaccess://", dbFile, sep="")
print(dbUrl)
conn <- dbConnect(driver, dbUrl)

# List the tables in the database
dbListTables(conn)

data <- dbGetQuery(conn, "SELECT PheneVisit FROM Stress")

print(data)

queryString <- "SELECT `PTSD Scale` FROM `PTSD Scale`"
query <- dbSendQuery(conn, queryString)
data <- fetch(query, n=-1) # n=-1

print(data)

result <- 123

result

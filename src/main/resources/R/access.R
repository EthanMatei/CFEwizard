#!/usr/bin/env Rscript
library('RJDBC')

args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
    stop("No database file specified")
}

print(tempdir())
dbFile <- args[1]

# Using Ucanacess Driver
# Need to download this, unzip, and put all jars in directory with script
driver <- JDBC(
    "net.ucanaccess.jdbc.UcanaccessDriver",
    "commons-lang3-3.8.1.jar:commons-logging-1.2.jar:hsqldb-2.5.0.jar:jackcess-3.0.1.jar:ucanaccess-5.0.0.jar",
    identifier.quote="`"
)



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

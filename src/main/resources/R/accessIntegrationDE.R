#-------------------------------------------------------------------------
# data - MS Access database
# cohort - cohort CSV File
# dxCode - diagnosis code
# phene - phene to process
# pheneTable - name of database table that contains the specified phene
#-------------------------------------------------------------------------
### prepAccessData <- function(data, cohort, dxCode, phene, pheneTable, bigDataCsv) {
prepAccessData <- function(cohort, dxCode, phene, pheneTable, bigDataCsv) {
    
  # ORIG: tblsNeeded <- c("CFI-S","SAS","SMS","HAMD SI","Pain","PANSS", "Demographics","SSS", "Diagnosis", "Subject Identifiers", cohortTbl)
  # tblsNeeded <- c(pheneTable, "Demographics", "Diagnosis", "Subject Identifiers")
  
  #---------------------------------------------------------
  # Set bigData to have the PheneVisit as its first column
  #---------------------------------------------------------
  # OLD CODE:
  #tableName <- paste("`", tblsNeeded[1], "`", sep="")    # Name needs to be escaped in case it has special character, such as hyphen
  #bigData <- dbReadTable(data, tableName)$PheneVisit # Reads tables in database into a data frame
  #bigData <- data.frame(bigData, stringsAsFactors = TRUE)
  #colnames(bigData) <- "PheneVisit"
  #
  #for (i in tblsNeeded){
  #  # collect all the tables you want and merge them together sequentially
  #  
  #  # New code
  #  tableName = paste("`", i, "`", sep="")
  #  tableData = dbReadTable(data, tableName)
  #  
  #  vars <- names(tableData)
  #  tableData[vars] <- lapply(
  #      tableData[vars],
  #      function(x) replace(x,x %in% c("na","NA","", "<NA>", "Na", "n/a", "N/A"), NA)
  #  )
  #  
  #  bigData <- merge(bigData, tableData, by="PheneVisit")
  #}
  
  # NEW CODE:
  bigData = read.csv(bigDataCsv, check.names=FALSE, stringsAsFactors = TRUE, na.strings=c("na","NA","", "<NA>", "Na", "n/a", "N/A"));
  #bigData <- data.frame(bigData, stringsAsFactors = TRUE, check.names=FALSE)
  vars <- names(bigData)
  
  outputFile <- paste(tempDir, "/ai-bigData_", Sys.Date(),".csv", sep="")
  write.csv(bigData, outputFile)
  
  # Merge the Cohort data
  cohortDataFrame = read.csv(cohort);
  bigData <-merge(bigData, cohortDataFrame, by="PheneVisit")
  
  #define function that gets all characters of string x minus the last n characters
  substrLeft <- function(x, n){
    substr(x, 1, nchar(x)-n)
  }
  
  #define a "Subject" column that excludes visit data
  bigData <- cbind(bigData,substrLeft( sapply(bigData["PheneVisit"],as.character),2))
  
  colnames(bigData)[length(bigData)] <- "Subject"
  
  
  #write.csv(bigData, "/tmp/bigData.csv", row.names = FALSE)
  
  
  ##Re-encode gender as a string ("M" or "F")
  bigData["Gender"] <- lapply(bigData["Gender(M/F)"], as.character)
  
  #---------------------------------------
  # Calculation phene
  #---------------------------------------
  bigData[, phene] <- as.numeric(bigData[, phene] >= highCutoff)
  
  #get names of cohorts
  cohortColumns <- colnames(cohortDataFrame)
  
  #drop the subject header column
  cohortColumns <- cohortColumns[-1]
  
  #subset data by cohort
  # OLD: bigData <- bigData[bigData[cohortColumns[cohortPreference]] == 1,]
  bigData <- bigData[bigData["DiscoveryCohort"] == 1,]
  
  #make sure the user entered a choice of diagnosis
  if (dxCode != ""){
    bigData <- bigData[which(bigData["DxCode"]==dxCode),]
  }
  
  
  ##clear up ram
  gc()
  
  # dbDisconnect(data)  # close the database connection
  
  # OLD: output <- list(bigData, dxCode, cohortColumns[cohortPreference])
  output <- list(bigData, dxCode, cohort)  
  
  return(output)
}


#---------------------------------------------------------------------------------------------
# cohort - cohort CSV File
# dxCode - diagnosis code
# phene - phene to process
# pheneTable - name of database table that contains the specified phene
# bigDataCsv - path for CSV file that contains test database data needed by this function
#---------------------------------------------------------------------------------------------
prepAccessData <- function(cohort, dxCode, phene, pheneTable, bigDataCsv, highCutoff) {

  bigData = read.csv(bigDataCsv, check.names=FALSE, stringsAsFactors = TRUE, na.strings=c("na","NA","", "<NA>", "Na", "n/a", "N/A"));
  vars <- names(bigData)
  
  # FOR DEBUGGING:
  #outputFile <- paste(tempDir, "/ai-bigData_", Sys.Date(),".csv", sep="")
  #write.csv(bigData, outputFile)
  
  # Merge the Cohort data
  cohortDataFrame = read.csv(cohort);
  bigData <-merge(bigData, cohortDataFrame, by="PheneVisit")

  # Re-encode gender as a string ("M" or "F")
  bigData["Gender"] <- lapply(bigData["Gender(M/F)"], as.character)
  
  #---------------------------------------
  # Calculation phene
  #---------------------------------------
  highCutoff <- as.numeric(highCutoff)
  bigData[, phene] <- as.numeric(bigData[, phene] >= highCutoff)
  
  #get names of cohorts
  cohortColumns <- colnames(cohortDataFrame)
  
  #drop the subject header column
  cohortColumns <- cohortColumns[-1]
  
  #subset data by cohort
  bigData <- bigData[bigData["DiscoveryCohort"] == 1,]
  
  #make sure the user entered a choice of diagnosis
  if (dxCode != ""){
    bigData <- bigData[which(bigData["DxCode"]==dxCode),]
  }
  
  # clear up ram
  gc()
  
  output <- list(bigData, dxCode, cohort)  
  
  return(output)
}


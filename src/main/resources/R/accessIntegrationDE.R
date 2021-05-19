
# data - MS Access database
# cohort - cohort CSV File
# dxCode - diagnosis code
prepAccessData <- function(data, cohort, dxCode, phene, pheneTable) {

  cohortDataFrame = read.csv(cohort);
  
  #specify all the names of the tables you want to pull from access
  ### cohortTbl <- "Cohorts"
  
  # ORIG: tblsNeeded <- c("CFI-S","SAS","SMS","HAMD SI","Pain","PANSS", "Demographics","SSS", "Diagnosis", "Subject Identifiers", cohortTbl)
  # OLD: tblsNeeded <- c("CFI-S","SAS","SMS","HAMD SI","Pain","PANSS", "Demographics","SSS", "Diagnosis", "Subject Identifiers")
  tblsNeeded <- c(pheneTable,"PANSS", "Demographics","Diagnosis", "Subject Identifiers")
  
  #if (!(pheneTable %in% tblsNeeded)) {
  #  tblsNeeded <- c(tblsNeeded, pheneTable)
  #}
  
  
  varName <- NULL #instantiate varNames holder
  
  #pull all the sql keys to use as a base for merging
  ###bigData <- sqlFetch(data,tblsNeeded[1])$PheneVisit # Reads tables in database into a data frame
  
  # It appears that the table name needs to be escaped if it has a hyphen
  tableName <- paste("`", tblsNeeded[1], "`", sep="")
  
  bigData <- dbReadTable(data, tableName)$PheneVisit # Reads tables in database into a data frame

  bigData <- data.frame(bigData)

  colnames(bigData) <- "PheneVisit"
  
  for (i in tblsNeeded){
    # collect all the tables you want and merge them together sequentially
    
	# New code
	tableName = paste("`", i, "`", sep="")
	tableData = dbReadTable(data, tableName)
	
	vars <- names(tableData)
	tableData[vars] <- lapply(
			tableData[vars],
			function(x) replace(x,x %in% c("na","NA","", "<NA>", "Na", "n/a", "N/A"), NA)
	)

    bigData <- merge(bigData, tableData, by="PheneVisit")
    
	# OLD CODE:
	# bigData <- merge(bigData,sqlFetch(data, i, na.strings=c("na","NA","", "<NA>", "Na", "N/A")), by="PheneVisit")
  }
  
  # Merge the Cohort data
  bigData <-merge(bigData, cohortDataFrame, by="PheneVisit")

  # Reset phene to phene dataframe variable name, which may be different from the 
  # phene database column name, because R changes some special characters (e.g., "-" and " ") to "."
  #pheneColumnIndex =  which(colnames(bigData) == phene)
  #print(colnames(bigData))
  #phene <- colnames(bigData)[pheneColumnIndex]
  #print(pheneColumnIndex)
  #print(c("PHENE:", phene))
  #print("-----------------------------------------------------------------------\n")
  
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
  # OLD: cohortColumns <- sqlColumns(data,cohortTbl)$COLUMN_NAME
  cohortColumns <- colnames(cohortDataFrame)
  
  #drop the subject header column
  cohortColumns <- cohortColumns[-1]
  
  
  #Convert string to integer
  #cohortPreference <- strtoi(cohortPreference)
  
  #subset data by cohort
  # OLD: bigData <- bigData[bigData[cohortColumns[cohortPreference]] == 1,]
  bigData <- bigData[bigData["DiscoveryCohort"] == 1,]
  
  # OLD CODE (get this from web app now):
  #dxCodeString <- ""
  #dxCode <- ginput(message = paste("Type the diagnosis code you want to use.\nIf you want to keep everyone, just press <enter>",dxCodeString), 
  #                 icon="question",
  #                 title="Choose a diagnosis")
  
  #make sure the user entered a choice of diagnosis
  if (dxCode != ""){
    bigData <- bigData[which(bigData["DxCode"]==dxCode),]
  }
  
  
  ##clear up ram
  gc()
  
  #close database
  #close(data)
  dbDisconnect(data)  # RJDBC (equivalent call for close)
  
  # OLD: output <- list(bigData, dxCode, cohortColumns[cohortPreference])
  output <- list(bigData, dxCode, cohort)  

  return(output)
}
  

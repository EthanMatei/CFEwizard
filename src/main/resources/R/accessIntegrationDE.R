




prepAccessData <- function(data){
	
    print("*** prepAccessData")
  
  #specify all the names of the tables you want to pull from access
  cohortTbl <- "Cohorts"
  tblsNeeded <- c("CFI-S","SAS","SMS","HAMD SI","Pain","PANSS", "Demographics","SSS", "Diagnosis", "Subject Identifiers", cohortTbl)
  
  varName <- NULL #instantiate varNames holder
  
  #pull all the sql keys to use as a base for merging
  ###bigData <- sqlFetch(data,tblsNeeded[1])$PheneVisit # Reads tables in database into a data frame
  
  print(tblsNeeded[1])
  
  print("DATA:")
  print(data)
  
  # It appears that the table name needs to be escaped if it has a hyphen
  tableName <- paste("`", tblsNeeded[1], "`", sep="")
  
  bigData <- dbReadTable(data, tableName)$PheneVisit # Reads tables in database into a data frame


  print("BIG DATA:\n\n")
  bigData <- data.frame(bigData)
  
  print("Frame created.")

  colnames(bigData) <- "PheneVisit"

  print(bigData)
  
  for (i in tblsNeeded){
    # collect all the tables you want and merge them together sequentially
    
	# New code
	tableName = paste("`", i, "`", sep="")
	print(paste("TABLE", tableName, ":"))
	tableData = dbReadTable(data, tableName)
	
	vars <- names(tableData)
	tableData[vars] <- lapply(
			tableData[vars],
			function(x) replace(x,x %in% c("na","NA","", "<NA>", "Na", "n/a", "N/A"), NA)
	)
	
	#print("VARS---------------")
	#print(vars)
	#print(tableData)

    bigData <- merge(bigData, tableData, by="PheneVisit")
    
	# OLD CODE:
	# bigData <- merge(bigData,sqlFetch(data, i, na.strings=c("na","NA","", "<NA>", "Na", "N/A")), by="PheneVisit")
  }

  
  #define function that gets all characters of string x minus the last n characters
  substrLeft <- function(x, n){
    substr(x, 1, nchar(x)-n)
  }
  
  #define a "Subject" column that excludes visit data
  bigData <- cbind(bigData,substrLeft( sapply(bigData["PheneVisit"],as.character),2))
  colnames(bigData)[length(bigData)] <- "Subject"
  
  # APPEARS TO BE WORKING UP TO HERE =========================================================================
  write.csv(bigData, "/home/jim/discovery-debug/bigData.csv", row.names = FALSE)

  sasColumns = c("SAS Anxiety (0-100); ~ 4/14/11 don't reverse scores; 1st switch ",
		  "SAS Uncertainty (0-100)",
		  "SAS Fear (0-100)",
		  "SAS Anger (0-100)")

  # jim debug:
  #bigDataC <- bigData[sasColumns]
  #write.csv(bigDataC, "/home/jim/discovery-debug/bigDataC.csv")
  #print("")
  #for (j in 1:ncol(bigDataC)) {
  #    for (i in 1:nrow(bigDataC)) {
  #		  cols = colnames(bigDataC)
  # 	  print(paste(cols[j], "type[", i, j, "]:", typeof(bigDataC[i,j])))
  #	  }
  #    
  #}
  # end jim debug

  bigData[sasColumns] <- lapply(sasColumns, as.numeric)
  ##calculate raw SAS score
  bigData["sasScore"] <- rowMeans(bigData[sasColumns])
					
  ##calculate raw SMS score
  smsColumns = c(
    "SMS Mood (0-100)",
    "SMS Motivation (0-100)",
	"SMS Movement (0-100)",
	"SMS Thinking (0-100)",
	"SMS Self-esteem (0-100)",
	"SMS Interest (0-100)",
	"SMS Appetite (0-100)"
  )
  bigData[smsColumns] <- lapply(smsColumns, as.numeric)
  
  bigData["smsScore"] <- rowMeans(bigData[smsColumns])
  
  ##calculate raw SMSmood score
  bigData["SMSmood"] <- rowMeans(bigData[c("SMS Mood (0-100)")])
                                            
  
  ##calculate SASS composite
  bigData["SASS"] <- bigData["sasScore"] - bigData["smsScore"]
  
  
  ##calculate CFI score
  bigData["cfiscore"] <- rowMeans(sapply(bigData[c("1 Psychiatric",
                                            "2 Compliance",
                                            "3 Familial",
                                            "4 Example",
                                            "5 Abuse",
                                            "6 Medical",
                                            "7 Losses",
                                            "8 Useless",
                                            "9 Introverted",
                                            "10 Dissatisfied",
                                            "11 Hopeless",
                                            "12 Addiction",
                                            "13 History",
                                            "14 Non-religious",
                                            "15 Rejection",
                                            "16 Isolation",
                                            "17 Impulsive",
                                            "18 Non-coping",
                                            "19 Childless",
                                            "20 Hallucinations",
                                            "21 Age",
                                            "22  Gender")], as.numeric), na.rm=TRUE)
  


  ##define Phene as people who scored a 2 or greater on the ideation item of HAMD
  bigData["SI"] <- as.numeric(bigData["HAMD SI"] >= 2)
  
  bigData["STRESS"] <- as.numeric(bigData["SSS- Life Stress (0-100)"] >= 67)
  
  bigData["Anxiety"] <- as.numeric(bigData["SAS Anxiety (0-100); ~ 4/14/11 don't reverse scores; 1st switch "] >= 60)
  
  bigData["MOOD"] <- as.numeric(bigData["SMS Mood (0-100)"] >= 67)
  
  bigData["Appetite"] <- as.numeric(bigData["SMS Appetite (0-100)"] >= 60)
  
  ##Re-encode gender as a string ("M" or "F")
  bigData["Gender"] <- lapply(bigData["Gender(M/F)"], as.character)
  
  bigData["PAIN"] <- as.numeric(bigData["Pain Scale"] >= 6)
  
  bigData["Delusions"] <- as.numeric(bigData["P1 Delusions (1-7)"] >= 4)
  
  bigData["Hallucinations"] <- as.numeric(bigData["P3 Hallucinations (1-7)"] >= 4)
  
  #get names of cohorts
  # OLD: cohortColumns <- sqlColumns(data,cohortTbl)$COLUMN_NAME
  cohortColumns <- dbGetFields(data, cohortTbl)$COLUMN_NAME
  
  #drop the subject header column
  cohortColumns <- cohortColumns[-1]
  
  cohortString <- "" #initiate variable for prompt
  for (i in 1:length(cohortColumns)) {
    entry <- paste0("(",i,") ",cohortColumns[i])
    cohortString <- paste(cohortString,entry, sep="\n")
  }
  
  print("=================================================================================")
  print("COHORT STRING")
  print(cohortString)

  cohortPreference <- ginput(message = paste("Enter the number corresponding with the cohort that you want to use.",cohortString), 
                           icon="question",
                           title="Choose a cohort")
  
  #make sure the user entered a choice of cohort
  stopifnot(!(cohortPreference == ""))
  
  #Convert string to integer
  cohortPreference <- strtoi(cohortPreference)
  
  #subset data by cohort
  bigData <- bigData[bigData[cohortColumns[cohortPreference]] == 1,]
  
  dxCodeString <- ""
  dxCode <- ginput(message = paste("Type the diagnosis code you want to use.\nIf you want to keep everyone, just press <enter>",dxCodeString), 
                   icon="question",
                   title="Choose a diagnosis")
  
  #make sure the user entered a choice of diagnosis
  if (dxCode != ""){
    bigData <- bigData[which(bigData["DxCode"]==dxCode),]
  }
  
  
  ##clear up ram
  gc()
  
  #close database
  close(data)
  
  output <- list(bigData, dxCode, cohortColumns[cohortPreference])
  
  
  return(output)
  
  }
  

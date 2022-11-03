

library(dplyr)
library(coin)

#----------------------------------------------------------------
# The mastersheet CSV file should contain Phene and Gene data
# along with subject and chip numbers
#----------------------------------------------------------------

#-------------------------------------------------
# Process command line arguments
#-------------------------------------------------
args = commandArgs(trailingOnly=TRUE)
if (length(args) != 9) {
  print(paste("Incorrect number of arguments: ", length(args)))
  stop("Incorrect number of arguments to Prediction script")
}

specialPredictorList = NULL;

scriptDir                   <- args[1]
testType                    <- args[2]    # "state", "first-year", or "future"
studyType                   <- args[3]    # "cross-sectional" or "longitudinal"
phene                       <- args[4]
pheneHighCutoff             <- args[5]    # Ignored for "FirstYearScore" and "HospFreq" and 

# diagnoses                   <- args[6]    # Comma-separated string of diagnoses
# genderDiagnoses             <- args[7]    # Comma-separated string of gender diagnoses, for example: "F-BP,F-SZ,M-BP,M-MDD"  

masterSheetCsvFile          <- args[6]
predictorListCsvFile        <- args[7]
specialPredictorListCsvFile <- args[8]
outputDir                   <- args[9]

pheneHighCutoff <- as.numeric(pheneHighCutoff)     # Make sure that the phene high cutoff is numeric

# diagnoses <- unlist(strsplit(diagnoses, ","))
# genderDiagnoses <- unlist(strsplit(genderDiagnoses, ","))


# d <- read.csv("Z:\\Delusions+Hallucinations Folder\\Delusions2021\\Mariah Project Folder\\Mastersheets\\All Future\\Mastersheet for All Future Predictions Delusions (MDH 4-16-2021).csv")
d <- read.csv(masterSheetCsvFile, check.names=FALSE)
names(d)[names(d) == "Visit Date"] <- "Visit.Date"
# d <- read.csv(masterSheetCsvFile)

d <- as.data.frame((d))

# Set VisitNumber column on master sheet based on the test type
if (testType == "state") {
  names(d)[names(d) == "Test.VisitNumber"] <- "VisitNumber"
  # doesn't need time, but rename this column so there is a "time" column:
  # names(d)[names(d) == "Time to 1st Hosp"] <- "time"
} else if (testType == "first-year") {
  names(d)[names(d) == "FirstYear.VisitNumber"] <- "VisitNumber"
  
  # Change time column name: "Time to 1st Hosp" => "time"
  names(d)[names(d) == "Time to 1st Hosp"] <- "time"
}  else if (testType == "future") {
  names(d)[names(d) == "Hospitalization.VisitNumber"] <- "VisitNumber"
  
  # Change time column name: "Time Future" => "time"
  names(d)[names(d) == "Time Future"] <- "time"
}

d[] <- lapply(d, function(x) type.convert(as.vector(x)))



########################################################
## Enter the path for your .csv predictor list here.  ##
## This file should contain genes and phene you want  ##
## to use along with direction of change and panels   ##
########################################################

###predictorFilePath <- "Z:\\Delusions+Hallucinations Folder\\Delusions2021\\Mariah Project Folder\\Mastersheets\\Delusions Predictor List for P1 (MDH 5-31-2021).csv"
predictorFilePath <- predictorListCsvFile

# load list of predictors with the silos you want to test
predictors <- read.csv(predictorFilePath)
predictorz <- predictors

if (!is.null(specialPredictorListCsvFile) && specialPredictorListCsvFile != "") {
  predictorFilePath <- specialPredictorListCsvFile
  predictors <-read.csv(specialPredictorListCsvFile)
}

predictorColumnNames <- colnames(predictors)
diagnoses <- predictorColumnNames[predictorColumnNames %in% c("Predictor", "Direction", "Male", "Female", "All") == FALSE]
diagnoses <- unlist(diagnoses)    # convert to vector
genderDiagnoses <- c(paste("F", diagnoses, sep="-"), paste("M", diagnoses, sep="-"))
genderDiagnoses <- unlist(genderDiagnoses)

cat("\nDIAGNOSES: ")
print(diagnoses)
cat("\nGENDER DIAGNOSES: ")
print(genderDiagnoses)


predictors_increase <- predictorz[predictorz$Direction == "I",]
predictors_decrease <- predictorz[predictorz$Direction == "D",]
biomarkersstring<- as.character(predictorz$Predictor)

increasedString <- as.character(predictors_increase$Predictor)
decreasedString <- as.character(predictors_decrease$Predictor)



#####################################################
##                                                 ##
## Insert your genes into the appropriate panels   ##
##                                                 ##
#####################################################

increasedPanel <- increasedString
decreasedPanel <- decreasedString


########################################################
##           Choose prediction test type              ##
########################################################

correctTies <- TRUE      ## Adjust p-values for AUCs for possible ties

FIRSTYEARtest <- F      ## First year hospitalization with highwater marks
FUTUREtest    <- F       ## All future hospitalization with highwater marks

# SETTABLE: -----------------------------------------------------
stateFirstYearHosp <- FALSE  ## First year hospitalization with NO highwater marks
stateFutureHosp <- FALSE     ## All future hospitalization with NO highwater marks

STATEtest  <- FALSE    ## For any STATE predictions (no highwater marks) 

if (testType == "state") {
  STATEtest <- TRUE
} else if (testType == "first-year") {
  stateFirstYearHosp <- TRUE
}  else if (testType == "future") {
  stateFutureHosp <- TRUE
}

#--------------------------------------------------------------------------------

DEATHtest <- F           ## Set to TRUE if you want to use DEATH as an outcome


############################   CORNERSTONES   ############################

maxSlopes <- FALSE     ## maxSlopes: Calculate the maximum slope value
slopes <- FALSE      ## slopes: Take absolute value of highest minus lowest visits
##         When you ask for slopes or max slopes people 
##         with fewer than two visits are dropped)
MAX <- FALSE           ## MAX: Highwater mark
LEVELS <- TRUE        ## LEVELS: raw value of biomarker; cross-sectional only

if (studyType == "longitudinal") {
  maxSlopes <- TRUE
  slopes    <- TRUE
  MAX       <- TRUE
}

#############################################################
## Insert the PHENE to predict below within quotes.        ##
## This must correspond to a header in your .csv database  ##
#############################################################

# Set PHENE
if (testType == "state") {
  PHENE <- phene;
  cutoff <- pheneHighCutoff
} else if (testType == "first-year") {
  PHENE <- "FirstYearScore"
}  else if (testType == "future") {
  PHENE <- "HospFreq"
}


#PHENE <- "P1Delusions"

#cutoff <- 2 #greater than or equal to cutoff for PHENE


########################################################
##     Location of folder for output data             ##
########################################################

# csvOutputFolder <- "Z:\\Delusions+Hallucinations Folder\\Delusions2021\\Mariah Project Folder\\R Outputs\\All Future"
csvOutputFolder <- outputDir



# install all the packages you need but don't have
# wants <- c("coin","survival", "pROC", "xlsx", "verification", "gWidgetstcltk", "gWidgets", "tcltk", "ROCR", "ggplot2", "GGally", "VGAM", "exactRankTests")
wants <- c("ROCR", "verification")
has   <- wants %in% rownames(installed.packages())
if(any(!has)) install.packages(wants[!has])
library(dplyr)
library(data.table)
library(readr)


#load packages#
sapply(wants, require, character.only = TRUE)
options(guiToolkit="tcltk") 


# load data needed 
#data <- read.csv(file=d, header = TRUE, na.strings=c("NA", "na","Na" , ""))
data <- d
header = TRUE 
na.strings=c("NA", "na","Na" , "")
# note: you can open up and inspect the data file by uncommenting and running the 
# following code:
# file.show(data)


#get the subject ID and visitnumber for all incomplete cases
incompletes <- data[!complete.cases(data[PHENE]),][,c("Subject","VisitNumber")]

if ( nrow(incompletes) > 0 ){
  for ( subjNum in nrow(incompletes):1 ){
    
    subj <- incompletes[subjNum,1]
    
    
    
    visitNumsGreaterThan <- data[data$Subject == subj & data$VisitNumber > incompletes[[subjNum,"VisitNumber"]], ]$VisitNumber
    
#increment down the visit numbers above the one you dropped to maintain the sequence
    if ( any(  visitNumsGreaterThan > incompletes[subjNum, 2] )  ){ #only do this if there are any visit numbers greater than the one you dropped
      data[data$Subject == subj & data$VisitNumber > incompletes[[subjNum,"VisitNumber"]], ]$VisitNumber <- (visitNumsGreaterThan - 1)
    }
#get rid of missing data
    data <- data[complete.cases(data[PHENE]),]
  }
}




# throw an error if the user asks for a state and a hospitalization test
# as they're incompatible
if ( (FIRSTYEARtest | FUTUREtest) & STATEtest ){
  error("You can't test both hospitalization and STATE at the same time. Set either FIRSTYEARtest | FUTUREtest or STATEtest to FALSE")
}




if (FIRSTYEARtest | FUTUREtest | stateFirstYearHosp | stateFutureHosp){ 
  
  # if user asks for state first year hosp, use
  # the variable "FirstYearScore" as the outcome 
  if (stateFirstYearHosp){
 
    #calculate ROC grouping variable#
    data$ROC <- as.numeric(data["FirstYearScore"] > 0)
    
    testName <- "firstYearHosp"
  }
  
  # if the user asks for state future hosp use "HospFreq"
  # as the outcome variable
  if (stateFutureHosp){
    
    data$ROC <- as.numeric(data["HospFreq"] > 0)
    testName <- "futureHosp"
  }
  
  
  #Split dataset by cohort according to what test the user specifies##
  if (FIRSTYEARtest | stateFirstYearHosp) {
    data <- data[data$FirstYearCohort == 1,]
  }
  
  if (FUTUREtest | stateFutureHosp ) {
    data <- data[data$HospitalizationCohort == 1,]
  }
  
  
}

if ( STATEtest ){
  # Make sure that columns used for numeric comparisons are numeric
  # data <- transform(data, PHENE = as.numeric(PHENE), check.names = false)
  
  #calculate ROC grouping variable
  #specify the PHENE variable above
  data$ROC <- as.numeric(data[PHENE] >= cutoff)
  #Split dataset by cohort##
  data <- data[data$TestCohort == 1,]
  testName <- "STATE"
  
}

if ( DEATHtest ){
  # Make sure that columns used for numeric comparisons are numeric
  # data <- transform(data, DeathCohort = as.numeric(DeathCohort), check.names = FALSE)
  
  #calculate ROC grouping variable#
  data$ROC <- as.numeric(data$Deathcohort > 0)
  #Split dataset by cohort##
  data <- data[data$Death == 1,]
  testName <- "Death"
}


#set column names for the output
tableColNames <- c("Gender",
    "Dx",
    "Predictor",
    "Change",
    "Events",
    "Non-Events",
    "Total Events",
    "AUC",
    "AUC p-value",
    "R",
    "R p-value",
    "t-test p-value")

#for HOSP runs we additionally do some cox regressions so we need column names for those too
if (stateFirstYearHosp | stateFutureHosp | FIRSTYEARtest | FUTUREtest | DEATHtest){  
  tableColNames <- c(tableColNames, 
      "Odds ratio predicting suicide",
      "p-value for odds ratio")
  
}

#initialize a table that we'll populate as we calculate values for the above columns
table <- as.data.frame(setNames(replicate(length(tableColNames),0, simplify = F), tableColNames))



# supply the source() function with the filepath of the version of the predictor variable you want to use 
# source("Z:\\Suman\\Suicide project with new guidelines using 2 probesets\\R\\CurrentBatchFunctionFourCornerstones (SG 10-27-2020).R")
fourCornerstonesScript <- paste(scriptDir, "CurrentBatchFunctionFourCornerstones.R", sep="/")
source(fourCornerstonesScript)

# for each predictor in your predictorFilePath....
for (i in 1:(nrow(predictors))) {
  
  #get the name of the first predictor as a string
  predictor <- as.character(predictors[i,1])
  #get the direction of the hypothesis ("I", "D", or "Panel")
  direction <- as.character(predictors[i,2]) 
  
  
  ##Calculate PREDICTOR variable with user inputs
  
 
  # for each marker, check if the user has requested any analyses
  any.requested <- rowSums(predictors[,! (names(predictors) %in% c("Predictor","Direction"))])[[i]]
  #if not, skip to the next one
  if (any.requested > 0) {
    
    # by now, all the variables needed for the calculatePREDICTOR() function have been set. no need to adjust anything
    # note: we add 1 to markers with any zero values... this is to avoid dividing by zero
    output <- calculatePREDICTOR(data, predictor, genderDiagnoses, direction, increasedPanel, decreasedPanel, LEVELS, slopes, MAX, maxSlopes)
    
    
    #get processed data
    data.out <- output[[1]]
    #if the user inputs no increased predictor and just one decreased predictor
    #this will automatically be set to TRUE, otherwise FALSE
    singleNegativePredictor <- output[[2]]
    #get list of what increased predictors are in the PREDICTOR variable
    #and what decreased predictors are in the predictor variable, just for reference
    increasedPredictors <- output[[3]]
    decreasedPredictors <- output[[4]]
    
    
    
    #Split dataset by diagnosis and gender as requested #
    genderList <- NULL
    if (predictors[i,"Female"]){
      genderList <- "F"
    }
    if (predictors[i,"Male"]){
      genderList <- c(genderList,"M")
    }
    
    dxList <- NULL 

    for (dx in diagnoses) {
        if (predictors[i, dx]) {
            dxList <- c(dxList, dx)
        }
    }

# OLD CODE:
#    if (predictors[i,"BP"]){
#      dxList <- "BP"
#    }
#    if (predictors[i,"MDD"]){
#      dxList <- c(dxList,"MDD") 
#    }
#    if (predictors[i,"SZ"]){
#      dxList <- c(dxList,"SZ")
#    }
#    if (predictors[i,"SZA"]){
#      dxList <- c(dxList,"SZA")
#    }
#    if (predictors[i,"PTSD"]){
#      dxList <- c(dxList,"PTSD")
#    }
#    if (predictors[i,"MOOD"]){
#      dxList <- c(dxList,"MOOD")
#    }
#    if (predictors[i,"PSYCH"]){
#      dxList <- c(dxList,"PSYCH")
#    }
#    if (predictors[i,"PSYCHOSIS"]){
#      dxList <- c(dxList,"PSYCHOSIS")
#    }
#    if (predictors[i,"GENDER"]){
#      dxList <- c(dxList,"GENDER")
#    }
    #if (predictors[i,"CONTROL"]){
    #dxList <- c(dxList,"CONTROL")
    #}
    #if (predictors[i,"AX"]){
    # dxList <- c(dxList,"AX")
    #}

    all <- FALSE
    if (predictors[i,"All"]){
      all <- TRUE
    }
    
    ####################################################################################################
    
    ######################## (all the stats) #################################################
    
    
    
    # for each gender requested
    for ( sex in genderList ){ 
      
      #subset to that gender
      data.subset.Gender <- data.out[data.out$Gender == sex,]
      
      #subset that gender further by requested diagnosis
      for (dx in dxList) {
        
        
        if (dx == "PSYCHOSIS") {
          data.subset.Gender.Dx <- subset(data.subset.Gender, DxCode %in% c("SZ","SZA"))
        } 

        if (dx == "GENDER") {
          # OLD CODE: data.subset.Gender.Dx <- subset(data.subset.Gender, DxCode %in% c("SZ","SZA","BP","MDD","PTSD","MOOD","PSYCH","OCD","AX"))
          data.subset.Gender.Dx <- subset(data.subset.Gender, DxCode %in% diagnoses)
        } 

        if (!(dx %in% c("PSYCHOSIS", "GENDER", "PLACEHOLDERDX") ) ) {
          data.subset.Gender.Dx <- data.subset.Gender[data.subset.Gender$DxCode == dx,]
        }
        
        # as demonstrated above, you can specify subsets that don't exist in the
        # DxCode for the data set, as with "PSYCHOSIS" above. if you like, you can do this yourself.
        
        
        #if the user wants one of the following tests, take highwater marks
        if (FIRSTYEARtest | FUTUREtest | DEATHtest){
          #select cases with the max PREDICTOR value for each client and restrict 
          #dataset to that. Break ties by visit number (earlier visits win)#
          ag <- aggregate(PREDICTOR~Subject, data=data.subset.Gender.Dx, max)
          data.subset.Gender.Dx <- merge(data.subset.Gender.Dx, ag[,c("Subject", "PREDICTOR")], by.x=c("Subject", "PREDICTOR"), by.y=c("Subject", "PREDICTOR"))
          ag2 <- aggregate(VisitNumber~Subject, data=data.subset.Gender.Dx, min)
          data.subset.Gender.Dx <- merge(data.subset.Gender.Dx, ag2[,c("Subject", "VisitNumber")], by.x=c("Subject", "VisitNumber"), by.y=c("Subject", "VisitNumber")) 
          rm(ag)
          rm(ag2)
        }
        
        
        #populate table row for with descriptive information 
        tableRow <- c(sex,dx,predictor,  direction)
        
        
        #calculate AUC, but only if there's more than one event in the silo
        if ( sum(data.subset.Gender.Dx$ROC) > 0 & 0 %in% data.subset.Gender.Dx$ROC & length(data.subset.Gender.Dx$ROC) > 2){
          
          #fit ROC and save to rocFit variable
          rocFit <- roc.area(data.subset.Gender.Dx$ROC,data.subset.Gender.Dx$PREDICTOR)
          
          
          #if the user wants to correct ties then we use the wilcoxon test for 
          # the p.value of our AUC
          if (correctTies == FALSE){
            p.value <- rocFit$p.value
          }
          if (correctTies == TRUE){
            p.value <- pvalue(wilcox_test(formula = PREDICTOR ~ as.factor(ROC), data=data.subset.Gender.Dx, alternative="less" ))
          }
          
          #continue to populate the table row with data from the AUC
          tableRow <- c(tableRow, 
              rocFit$n.events, # num. of events observed
              rocFit$n.noevents, # num. of non-events
              rocFit$n.total, # total num. of events and non-events
              rocFit$A, # AUC value
              p.value) # p value
          
          
          # for suicide completer tests, only include completers in the correlation tests
          if (DEATHtest){
            corData <- subset(data.subset.Gender.Dx,ROC=="1")
          }else{
            corData <- data.subset.Gender.Dx
          }
          
          # if there's a single negative predictor, flip the sign of the R in the pearson correlation
          if ( singleNegativePredictor) {
            corFit <- cor.test( ~ corData[,PHENE] + corData$PREDICTOR, alternative = "greater")
            corFit$estimate <- -corFit$estimate
          } else {
            corFit <- cor.test(~ corData[,PHENE] + corData$PREDICTOR, alternative = "greater")
          }
          tableRow <- c(tableRow, corFit$estimate, corFit$p.value)
        } else{
          
          #if you couldn't calculate all these values, fill the table with "NA" values instead of crashing
          tableRow <- c(tableRow, "NA", "NA", "NA", "NA", "NA", "NA", "NA")
          
        }
        
        
        
        if ( sum(data.subset.Gender.Dx$ROC) > 1 & 0 %in% data.subset.Gender.Dx$ROC){
                  
          lengthX = length(data.subset.Gender.Dx$PREDICTOR[data.subset.Gender.Dx$ROC == 1])
          lengthY = length(data.subset.Gender.Dx$PREDICTOR[data.subset.Gender.Dx$ROC == 0])
          
          if (lengthY < lengthX) {
            # If there aren't enough y values
            tableRow <- c(tableRow, "NA")
          } else {      
            #All t-test between presence of SI and no SI
            tFit <- t.test(data.subset.Gender.Dx$PREDICTOR[data.subset.Gender.Dx$ROC == 1],
                data.subset.Gender.Dx$PREDICTOR[data.subset.Gender.Dx$ROC == 0],       
                alternative = "greater" )
          
            tableRow <- c(tableRow, tFit$p.value)
          }
        } else{
          tableRow <- c(tableRow, "NA")
          
        }
        
        
        
        if (stateFirstYearHosp | stateFutureHosp | FIRSTYEARtest | FUTUREtest | DEATHtest) {
          
          if( !(sum(data.subset.Gender.Dx$ROC)) > 1 |  !(0 %in% data.subset.Gender.Dx$ROC) ){
            oddsRatio <- "NA"
            oddsPvalue <- "NA"
          }else{
            coxFit <- coxph(formula = Surv(data.subset.Gender.Dx$time, data.subset.Gender.Dx$ROC) ~ data.subset.Gender.Dx$PREDICTOR)
            
            if (!(summary(coxFit)$coefficients[[4]] == "NaN")){
              
              oddsRatio <- summary(coxFit)$coefficients[[2]]
              oddsPvalue <- summary(coxFit)$coefficients[[5]]
              rm(coxFit)
            }else{
              oddsRatio  <- "Did not converge"
              oddsPvalue <- "Did not converge"
            }
          }
          
          
          
          tableRow <- c(tableRow, oddsRatio, oddsPvalue)
        }
        table <- rbind(table, tableRow)
        
        
        
      }
      cat(".")
      
    }
    
    if (all == TRUE){
      dataAll <- data.out
      
      if (FIRSTYEARtest | FUTUREtest | DEATHtest){
        highestZscore <- aggregate(PREDICTOR~Subject, data=data, max)
        dataAll <- merge(data, highestZscore[ ,c("Subject", "PREDICTOR")], by.x=c("Subject", "PREDICTOR"), by.y=c("Subject", "PREDICTOR"))
        highwaterVisit <- aggregate(VisitNumber~Subject, data=dataAll, min)
        dataAll <- merge(data, highwaterVisit[,c("Subject", "VisitNumber")], by.x=c("Subject", "VisitNumber"), by.y=c("Subject", "VisitNumber")) 
        rm(highestZscore)
        rm(highwaterVisit)
      }
      
      
      tableRow <- c("All","All", predictor, direction)
      rocFit <- roc.area(dataAll$ROC,dataAll$PREDICTOR)
      
      if (correctTies == FALSE){
        p.value <- rocFit$p.value
      }
      if (correctTies == TRUE){
        
        p.value <- pvalue(wilcox_test(formula = PREDICTOR ~ as.factor(ROC), data=dataAll, alternative="less" ))
        
      }
      
      tableRow <- c(tableRow, rocFit$n.events, rocFit$n.noevents, rocFit$n.total, rocFit$A, p.value)
      
      if (DEATHtest){
        corData <- subset(data,ROC=="1")
      }else{
        corData <- dataAll
      }
      
      #Whole dataset ("all") correlation
      if ( singleNegativePredictor) {
        corFit <- cor.test( ~ corData[,PHENE] + corData$PREDICTOR, alternative = "greater")
        corFit$estimate <- -corFit$estimate
      } else {
        corFit <- cor.test(~ corData[,PHENE] + corData$PREDICTOR, alternative = "greater")
      }
      tableRow <- c(tableRow, corFit$estimate, corFit$p.value)
      
      if( sum(dataAll$ROC) > 1 ){
        
        #All t-test between presence of SI and no SI
        tFit <- t.test(dataAll$PREDICTOR[dataAll$ROC == 1],
            dataAll$PREDICTOR[dataAll$ROC ==0],       
            alternative = "greater" )
        
        tableRow <- c(tableRow, tFit$p.value)
      }else{tableRow <= c(tableRow, "NA")}
      
      
      
      if ( stateFirstYearHosp | stateFutureHosp | FIRSTYEARtest | FUTUREtest ) {
        
        
        if( !(sum(dataAll$ROC) > 1) ){
          oddsRatio <- "NA"
          oddsPvalue <- "NA"
        }else{
          coxFit <- coxph(formula = Surv(dataAll$time, dataAll$ROC) ~ dataAll$PREDICTOR)
          
          if (!(summary(coxFit)$coefficients[[4]] == "NaN")){
            
            oddsRatio <- summary(coxFit)$coefficients[[2]]
            oddsPvalue <- summary(coxFit)$coefficients[[5]]
          }else{
            oddsRatio <- "Did not converge"
            oddsPvalue <- "Did not converge"
            
          }
        }
        
        tableRow <- c(tableRow, oddsRatio, oddsPvalue)
        rm(coxFit)
        
      }
      table <- rbind(table, tableRow)
      
      rm(dataAll)
      
    }
    
    cat(".")
  }
  
}





table <- table[-1,] #remove first placeholder row

#------------------------------------
# Create the output file path
#------------------------------------
if (correctTies ) {
  csvOutput <- paste0(csvOutputFolder, "/predictions-output-CORRECT-TIES ", testName, Sys.Date())
}else {
  csvOutput <- paste0(csvOutputFolder, "/predictions-output", testName, Sys.Date())
}

if (LEVELS){ 
  csvOutput <- paste0(csvOutput, "-levels")
}

if (slopes) {
  csvOutput <- paste0(csvOutput, "-slopes")
}

if (MAX) {
  csvOutput <- paste0(csvOutput, "-MAX")
}

if (maxSlopes) {
  csvOutput <- paste0(csvOutput, "-maxSlopes")
}

csvOutput <- paste0(csvOutput, ".csv")

#--------------------------------------------------------------------------------------------
# Write the table to the output file and print a statement with a label and the file name
# so that the calling code can find the file
#--------------------------------------------------------------------------------------------
write.csv(table, file=csvOutput)
cat("\nPrediction output file created: ", csvOutput, "\n")



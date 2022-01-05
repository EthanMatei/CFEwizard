#-------------------------------------------------
# Process command line arguments
#-------------------------------------------------
args = commandArgs(trailingOnly=TRUE)
if (length(args) != 5) {
  print(paste("Incorrect number of arguments: ", length(args)))
  stop("Incorrect number of arguments to Validation script")
}

scriptDir            <- args[1]
phene                <- args[2]
masterSheetCsvFile   <- args[3]
predictorListCsvFile <- args[4]
outputDir            <- args[5]

predictorFilePath <- predictorListCsvFile

#####################################################
##                                                 ##
## Insert your genes into the appropriate panels   ##
##                                                 ##
#####################################################




### BioM41 
#increasedPanel <- c("SEPT7P2Pain1569973_at","HRASPain212983_at","DNAJC18Pain227166_at","DENND1BPain1557309_at","MCRS1Pain202556_s_at","RAB33APain206039_at","LY9Pain231124_x_at","ASTN2Pain1554816_at","SERPINF1Pain202283_at","HLADQB1Pain211656_x_at","HLADQB1Pain212998_x_at","FAM134BPain218510_x_at","AF090920Pain234739_at","ZNF441Pain1553193_at","CLSPNPain242150_at","AF087971Pain1561067_at","ZNF91Pain244259_s_at","Hs554262Pain210703_at")
#decreasedPanel <- c("Hs696420Pain243125_x_at","Hs609761Pain244331_at","RALGAPA2Pain231826_at","OSBP2Pain1569617_at","YBX3Pain201160_s_at","TCF15Pain207306_at","GSPT1Pain215438_x_at","CCDC85CPain219018_s_at","WNK1Pain1555068_at","PTNPain211737_x_at","MBNL3Pain219814_at","Hs659426Pain240599_x_at","Hs596713Pain226138_s_at","DCAF12Pain224789_at","PIK3CDPain211230_s_at","Hs666804Pain240949_x_at","Hs677263Pain216444_at","CALCAPain210727_at","COMTPain213981_at","COMTPain216204_at","CNTN1Pain1554784_at","HTR2APain211616_s_at","H05785Pain236913_at")

### BioM2
#increasedPanel <- c()
#decreasedPanel <- c("Hs666804Pain240949_x_at","PIK3CDPain211230_s_at")

### BioM test 2 not real, for checking manual against manual calculations
#increasedPanel <- c("ZNF91Pain244259_s_at")
#decreasedPanel <- c("PIK3CDPain211230_s_at")

########################################################
##           Choose prediction test type              ##
########################################################

correctTies <- TRUE      ## Adjust p-values for AUCs for possible ties

FIRSTYEARtest <- F       ## First year hospitalization with highwater marks
FUTUREtest    <- F       ## All future hospitalization with highwater marks

stateFirstYearHosp <- F  ## First year hospitalization with NO highwater marks
stateFutureHosp <- F     ## All future hospitalization with NO highwater marks

STATEtest  <- TRUE       ## For any STATE predictions (no highwater marks) 
DEATHtest <- F           ## Set to TRUE if you want to use DEATH as an outcome


############################   CORNERSTONES   ############################

maxSlopes <- F     ## maxSlopes: Calculate the maximum slope value
slopes <- F        ## slopes: Take absolute value of highest minus lowest visits
##         When you ask for slopes or max slopes people 
##         with fewer than two visits are dropped)
MAX <- F           ## MAX: Highwater mark
LEVELS <- TRUE        ## LEVELS: raw value of biomarker

#############################################################
## Insert the PHENE to predict below within quotes.        ##
## This must correspond to aheader in your .csv database   ##
#############################################################

PHENE <- phene


########################################################
##     Location of folder for output data             ##
########################################################

csvOutputFolder <- outputDir


###############################################
###############################################
##########                           ##########
##########    END USER SETTINGS      ##########
###############################################
###############################################



#################################################################################################################################


###############################################
###############################################
#########                           ##########
######### DO NOT EDIT ANYTHING BELOW #########
##############################################
##############################################





# install all the packages you need but don't have


require(devtools)

gwidgetspackage <- c("gWidgets") %in% rownames(installed.packages())
gwidgetstcltkpackage <- c("gWidgetstcltk") %in% rownames(installed.packages())
verificationpackage <- c("verification") %in% rownames(installed.packages())

if(!gwidgetspackage) install_version("gWidgets", version = '0.0-54.1')
if(!gwidgetspackage) install_version("gWidgetstcltk", version = '0.0-55')
if(!gwidgetspackage) install_version("verification", version = '1.42')

wants <- c("coin","survival", "pROC", "xlsx", "verification", "gWidgetstcltk", "gWidgets", "tcltk", "ROCR", "ggplot2", "GGally", "VGAM", "exactRankTests")
has   <- wants %in% rownames(installed.packages())
if(any(!has)) install.packages(wants[!has])


#load packages#
sapply(wants, require, character.only = TRUE)
options(guiToolkit="tcltk")


# load data needed 
data <- read.csv(file=masterSheetCsvFile, header = TRUE, na.strings=c("NA", "na","Na" , ""))


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

  
# load list of predictors with the silos you want to test
predictors <- read.csv(predictorFilePath) 


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
  
  #calculate ROC grouping variable
  #specify the PHENE variable above
  data$ROC <- as.numeric(data[PHENE] >= 4)
  #Split dataset by cohort##
  data <- data[data$ValidationCohort == 1,]
  #data <- data[data$TestCohort == 1,]
  testName <- "STATE"
  
}

if ( DEATHtest ){
  #calculate ROC grouping variable#
  data$ROC <- as.numeric(data$Deathcohort > 0)
  #Split dataset by cohort##
  data <- data[data$Death == 1,]
  testName <- "Death"
}



#set column names for the output
tableColNames <- c("Analysis",
                   "Gene",
                   "Change",
                   "n Validated",
                   "ANOVA p-value",
                   "Stepwise Test")

#for HOSP runs we additionally do some cox regressions so we need column names for those too
if (stateFirstYearHosp | stateFutureHosp | FIRSTYEARtest | FUTUREtest | DEATHtest){  
  tableColNames <- c(tableColNames, 
                     "Odds ratio predicting suicide",
                     "p-value for odds ratio")
  
}

#initialize a table that we'll populate as we calculate values for the above columns
table <- as.data.frame(setNames(replicate(length(tableColNames),0, simplify = F), tableColNames))


# for each predictor in your predictorFilePath....
for (i in 1:(nrow(predictors))) {
  
  #get the name of the first predictor as a string
  predictor <- as.character(predictors[i,1])
  #get the direction of the hypothesis ("I", "D", or "Panel")
  direction <- as.character(predictors[i,2]) 
  

  ##Calculate PREDICTOR variable with user inputs
  
  # supply the source() function with the filepath of the version of the predictor variable you want to use 
  fourCornerstonesScript <- paste(scriptDir, "CurrentBatchFunctionFourCornerstones.R", sep="/")
  source(fourCornerstonesScript)
  
  
  # for each marker, check if the user has requested any analyses
  any.requested <- rowSums(predictors[,! (names(predictors) %in% c("Predictor","Direction"))])[[i]]
  #if not, skip to the next one
  if (any.requested > 0){
  
  # by now, all the variables needed for the calculatePREDICTOR() function have been set. no need to adjust anything
  output <- calculatePREDICTOR(data, predictor, direction, increasedPanel, decreasedPanel, LEVELS, slopes, MAX, maxSlopes)
  
  
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
  if (predictors[i,"BP"]){
    dxList <- "BP"
  }
  if (predictors[i,"MDD"]){
    dxList <- c(dxList,"MDD")
  }
  if (predictors[i,"SZ"]){
    dxList <- c(dxList,"SZ")
  }
  if (predictors[i,"SZA"]){
    dxList <- c(dxList,"SZA")
  }
  if (predictors[i,"PTSD"]){
    dxList <- c(dxList,"PTSD")
  }
  if (predictors[i,"PSYCHOSIS"]){
    dxList <- c(dxList,"PSYCHOSIS")
  }
  all <- FALSE
  if (predictors[i,"All"]){
    all <- TRUE
  }
  
  ####################################################################################################
  
  ######################## (all the stats) #################################################
  


  # for each gender requested

  if (all == TRUE){
    dataAll <- data.out
    
    
    
    tableRow <- c("Validation", predictor, direction)
    rocFit <- roc.area(dataAll$ROC,dataAll$PREDICTOR)
    
   
    tableRow <- c(tableRow, rocFit$n.total)
    
   
      corData <- dataAll
    
    

    
    #All t-test between presence of SI and no SI
    #tFit <- t.test(dataAll$PREDICTOR[dataAll$ROC == 1],
    #              dataAll$PREDICTOR[dataAll$ROC ==0],       
    #             alternative = "greater" )
     #    tableRow <- c(tableRow, tFit$p.value)
    ANOVA<- oneway.test(corData$PREDICTOR ~ corData$ValCategory, var.equal = TRUE)
    #tFit<- aov(dataAll$PREDICTOR ~ dataAll$ValCategory)
    #oneway.test(corData$PREDICTOR ~ corData$ValCategory, var.equal = TRUE)
    Stepwise <- aov(corData$PREDICTOR ~ corData$ValCategory)
    AOVStepwise<- TukeyHSD(Stepwise)
    tableRow <- c(tableRow, ANOVA$p.value)
    
    
    if(AOVStepwise$'corData$ValCategory'[,1]["High-Clinical"] < 0
       && AOVStepwise$'corData$ValCategory'[,1]["Low-Clinical"] < 0
       && AOVStepwise$'corData$ValCategory'[,1]["Low-High"] < 0   ) 
    {  Stepwise <- "Stepwise"
     } else Stepwise <- "Not Stepwise"
     tableRow <- c(tableRow, Stepwise)
       

    table <- rbind(table, tableRow)
    
    rm(dataAll)
    
  }
  
  cat(".")
  }
  
}





table <- table[-1,] #remove first placeholder row
if (correctTies ){
csvOutput <- paste0(csvOutputFolder, "/predictions output CORRECT TIES ", testName, Sys.Date())
}else{
csvOutput <- paste0(csvOutputFolder, "/predictions output", testName, Sys.Date())
}
if (LEVELS){
  csvOutput <- paste0(csvOutput, " levels")
}
if (slopes){
csvOutput <- paste0(csvOutput, " slopes")
}
if (MAX){
csvOutput <- paste0(csvOutput, " MAX")
}
if (maxSlopes){
csvOutput <- paste0(csvOutput, " maxSlopes")
}

csvOutput <- paste0(csvOutput, ".csv")

#--------------------------------------------------------------------------------------------------------
# Create the output file and print the label and the path for this file so the calling code can find it
#--------------------------------------------------------------------------------------------------------
write.csv(table, file=csvOutput)
cat("\nValidation output file created: ", csvOutput, "\n")


#write.csv(corData, file="Z:\\Female Suicide\\12 Female Intra Suicide With Male Evidence\\tempDatasetFORDan1.csv")
#file.show("Z:\\Female Suicide\\12 Female Intra Suicide With Male Evidence\\tempDatasetFORDan1.csv")


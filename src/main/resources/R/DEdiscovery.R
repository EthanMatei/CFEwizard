


### wants <- c("RODBC","RODBCext","gWidgets", "tcltk", "plyr", "gtools", "dplyr")

# Packages
# RJDBC - for accessing MS Access
# plyr - "Tools for Splitting, Applying and Combining Data"
# dplyr - tool for working with data frame like objects
# gtools - various general R programming tools

wants <- c("RJDBC", "plyr", "gtools", "dplyr")
has   <- wants %in% rownames(installed.packages())
if(any(!has)) install.packages(wants[!has])

### library(RODBC)
### require(gWidgets)
### require(tcltk)
library(RJDBC)
library(gtools)
library(plyr)
library(dplyr)
### options(guiToolkit="tcltk") 

#-------------------------------------------------
# Process command line arguments
#-------------------------------------------------
args = commandArgs(trailingOnly=TRUE)
if (length(args) != 10) {
	print(paste("Incorrect number of arguments: ", length(args)))
	stop("Incorrect number of arguments to DEdiscovery script")
}

scriptDir          <- args[1]
cohortCsvFile      <- args[2]
diagnosisCodeParam <- args[3]
dbFile             <- args[4]
csvFile            <- args[5]
pheneSelection     <- args[6]
pheneTable         <- args[7]
lowCutoff          <- args[8]
highCutoff         <- args[9]
tempDir            <- args[10]

print(pheneTable)

if (diagnosisCodeParam == "All") {
	diagnosisCodeParam <- ""
}

addAsymmetry <- TRUE


#get access database location
accessDb <- dbFile

##open connection with access database

#-------------------------------------------------------
# Get MS Access database connection
#-------------------------------------------------------
###PheneData <- odbcConnectAccess2007(accessDb)

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

driver <- JDBC(
		"net.ucanaccess.jdbc.UcanaccessDriver",
		jars,
		identifier.quote="`"
)
dbUrl <- paste("jdbc:ucanaccess://", dbFile, sep="")
PheneData <- dbConnect(driver, dbUrl)

# List the tables in the database
dbListTables(PheneData)

# Get the access integration script
accessIntegrationScript <- paste(scriptDir, "accessIntegrationDE.R", sep="/")
source(accessIntegrationScript)

##get processed access data
accessIntegrationOutput <- prepAccessData(PheneData, cohortCsvFile, diagnosisCodeParam, pheneSelection, pheneTable)

PheneData <- accessIntegrationOutput[[1]]
dxChoice <- accessIntegrationOutput[[2]]
cohortChoice <- accessIntegrationOutput[[3]]
rm(accessIntegrationOutput)

# PHENE <- "SI"   # This is your dependent variable
PHENE <- pheneSelection

print(paste("PHENE", PHENE))

DEdata <- read.csv(csvFile) #location of RMA data #location of RMA data

rownames(DEdata) <- DEdata[,1] #make the first row into headers
DEdata <- DEdata[, -1] #delete the now-redundant first row

#transpose data to set the subject visits as rows
DEdata <- as.data.frame(t(DEdata))

#define function that gets the last n characters of a string x
substrRight <- function(x, n){
  substr(x, nchar(x)-n+1, nchar(x))
}


DEdata <- cbind(DEdata,substrRight(rownames(DEdata),2)) #get visit number
colnames(DEdata)[length(DEdata)] <- "VisitNumber" #name the column

DEdata["PheneVisit"] <- rownames(DEdata) #make "PheneVisit" column from row IDs

gc()


stringColumns <- c("PheneVisit","Subject","date", PHENE)


data <- join(DEdata,PheneData[stringColumns], #merge to get SI row
             by = "PheneVisit", #merge on phene visit
             type="inner") #drop unmatched (i.e., out-of-cohort) cases 


rownames(data) <- as.matrix(data["PheneVisit"]) #make row names





##add empty row to hold AP scores
data <- rbind(data,rep(NA,ncol(data)))

rownames(data)[nrow(data)] <- "DEscores"


data[, ! names(data) %in% stringColumns] <- lapply(data[, ! names(data) %in% stringColumns], as.numeric)
#set initial DEscore values to 0 on full data.frame
data["DEscores", ! names(data) %in% stringColumns] <- 0


#### test data for practice
# i <- 1
# x <- data[which(data$Subject == "phchp304"),]
# x <- cbind(x[1070:1075], x[stringColumns])




subjectDEscore <- data["DEscores",-((ncol(data)-3):ncol(data))] #this holds an subject-level APscore, drops identifier columns
subjectLevelOutpout <- subjectDEscore

loopNumber <- 1
#put some calming messages here
calmingMessage <- c("This will take awhile. Now is a good time to grab a coffee or a bite to eat",
                    "You've got a great outfit on!",
                    "You look very professional today",
                    "You have kind eyes.",
                    "Give yourself a meaningful compliment.",
                    "Keep your head up!",
                    "You're hard at work")

list.of.unique.subjects <- as.character(unique ( data[-nrow(data) & order("PheneVisit"),"Subject"] ))
list.of.unique.subjects <- list.of.unique.subjects[!is.na(list.of.unique.subjects)]



wormShape <- subjectDEscore


cat("You selected cohort",cohortChoice,"and diagnosis:",ifelse(dxChoice != "", dxChoice, "everyone"))

numberOfSubjects <- length(unique ( data[-nrow(data),"Subject"] ))



summaryResultsTable <- c("Subject",
                   "Number of visits")



for ( uniqueSubject in list.of.unique.subjects  ) { #loop through individual subject IDs
  
  
  calmingMessageSelected <- sample(calmingMessage,1, replace=TRUE)
  cat("
      
      Now processing DE scores for",uniqueSubject,"(", loopNumber,"of", numberOfSubjects,"total subjects )
      
      ",calmingMessageSelected,
      "                             
      
      ")
  loopNumber <- loopNumber + 1
  
  
  x <- data[which(data["Subject"] == uniqueSubject),]
  x  <-  x [ order( as.Date(x$date) ), ] # order by visit
  
  cat("\n...")
  totalVisits <- nrow(x) #get number of visits for this subject
  cat("\n...")
  subjectDEscore["DEscores",] <- 0 #reset AP score to 0 for the new subject
  cat("\n...")
  
  subjectDEscore["perfectionCounter",] <- 0 #reset perfectionCounter to 0 for the new subject
  

  
  for (visit in 1:(totalVisits-1)){ ##stop calculating when there are no comparisons left
    
    
    #status update  
    cat("
        
        #######################################################
        ##                                                   ## 
        ## Processing subject", uniqueSubject, "visits", visit,"-",visit + 1, "( of",totalVisits,") ## 
        ##                                                   ## 
        #######################################################
        
        
        
        
        ")
  
    print(c("PHENE = ", PHENE, "   ", " visit = ", visit, " class: ", class(visit)))
	print(c("x[visit + 1,PHENE]: ", x[visit + 1,PHENE]))
	theResult = x[visit,PHENE]
	print(c("x[visit,PHENE]", x[visit,PHENE], class(theResult)))
	
    pheneChange <- strtoi(x[visit + 1,PHENE]) - strtoi(x[visit,PHENE]) #change in phene state
    
    

    
    #changes in gene states (don't do this to factor columns)
    cat("\nCalculating gene changes for",length(x) - length(stringColumns),"probe sets
        ...")
    geneChange <- x[visit + 1,! names(x) %in% stringColumns] / x[visit,! names(x) %in% stringColumns] 
    
    ## set fold changes. ORDER MATTERS here.
    cat("\n\nConverting continuous fold changes to categorical values:
Max changes downward")
    geneChange[1,which((geneChange<=0.8333333333333333) & (1==1)) ] <- -1
    cat("\nMarginal changes downward")
    geneChange[1,which((geneChange<=0.90909090909090) & (geneChange>0.8333333333333333))] <- -0.5
    cat("\nNo change")
    geneChange[1,which((geneChange>0.90909090909090) & (geneChange<1.1))] <- 0
    cat("\nMarginal changes upward")
    
    geneChange[1,which((geneChange>=1.1) & (geneChange<1.2))] <- 0.5
    cat("\nMax changes upward\n")
    
    geneChange[1,which((geneChange>=1.2) & (1==1))] <- 1
    cat("\nGet latest gene value and phene value
        ...")
    cat("\n...")
    pheneValue <- x[visit + 1,PHENE]
    
 
    
    
    
    
    
    if (visit == 1){
      
      lookAround <- data.frame(pheneChange)
      

      wormShape <- subjectDEscore[-(1:2), ]
      
    }else{
      
      lookAround <- rbind(lookAround, pheneChange)
      
    }
    wormShape <- rbind(wormShape, geneChange)
    
    
    
    
    if (totalVisits > 2){
      cat("\nCalculating perfection
          ...")
      
      
      if ( pheneChange != 0 ){ #if there is a phene change, assign points based off of gene change direction
        ## if the perfectionCounter value ends the loop equalling the number of comparisons, you have earned a perfection bonus
        subjectDEscore["perfectionCounter",]  <-  subjectDEscore["perfectionCounter",]  + sign(geneChange[] * pheneChange)
        cat("\n...")
      }
      
      if ( pheneChange == 0 ) { #if there is no phene change, we need special logic to account for genes that didn't change
        cat("\nThere was no phene change between these two visits, which necessitates some extra calculations")
        
  
        
      }
      
      
    }else{
      cat("\nNo need to calculate perfection score because this subject only has two visits")
    }
    
    ##note: there are only two change conditions where APscore has to be adjusted
    
    cat("\nPhene and gene changes calculated\nNow calculating DE points for visits",visit,"-",visit+1)  
    
    if ( pheneChange == 1 ) { #if phene changes, add gene change value to APscore
      
      subjectDEscore["DEscores",] <- subjectDEscore["DEscores",] + geneChange[] 
      cat("\n...")
      
    }
    
    if ( pheneChange == -1 ) { #if phene changes, add gene change value to APscore
      
      subjectDEscore["DEscores",] <- subjectDEscore["DEscores",] + (geneChange[] * -1)
      cat("\n...")
      
    }
    
    if ( pheneChange == 0 ){ #if phene doesn't change, nor does gene...
      cat("\nFor genes that didn't change on this visit, we can't assign points until we get through all the other visits")

    }
    
    
    gc()
    
    
  }
  
    
  if (totalVisits > 2){
  
    
  for ( comparison in 1:nrow(lookAround)) {
    
    if ( lookAround[comparison, "pheneChange"]==0  ){
      
      temp <- subjectDEscore
      temp["DEscores",] <- 0 #reset DE score to 0 for the new subject
      cat("\n...")
      
      temp["perfectionCounter",] <- 0 #reset perfectionCounter to 0 for the new subject
      
      cat("\n\nFor comparison",comparison, "there was no phene change\n\nDaisychain: ENGAGE\n")

       pheneValue <- x[comparison,PHENE] 

      
      cat("\nInitialize temporary points vector for this comparison")
      pointsVector <- wormShape[1,]
      pointsVector[1,] <- 0
      
      
      if (comparison > 1){
           for ( i in (comparison-1):1){
            
            if ( (i > 0) & (i != comparison) ){
              
            cat("\nLooking left to comparison",i)

            if (pheneValue == 1 ){
              tempWorm <- wormShape
              if (addAsymmetry ){
                tempWorm[i, (pointsVector == 0) & ( tempWorm[i,] == -0.5 )] <- -1
                
              }
              
            pointsVector[,pointsVector == 0 ] <-  tempWorm[i, pointsVector == 0]
            }
            
            if (pheneValue == 0 ){
              tempWorm <- wormShape
              if (addAsymmetry ){
         
                tempWorm[i, (pointsVector == 0) & (tempWorm[i,] == -0.5 )] <- -1
                
              }

              pointsVector[,pointsVector == 0 ] <- (tempWorm[i, pointsVector == 0])*-1
            }
            
            
            
              }
            }
          }
      
      for ( i in (comparison+1):nrow(lookAround)){
        
        if ( (i < (nrow(lookAround)+1)) & (comparison != nrow(lookAround)  )){
       
          
          cat("\nLooking right to comparison",i)
          if (pheneValue == 1 ){
            tempWorm <- wormShape
            if (addAsymmetry ){
              tempWorm[i, (pointsVector == 0) & (tempWorm[i,] == 0.5 )] <- 1
            
            }
            
            pointsVector[,pointsVector == 0 ] <- (tempWorm[i, pointsVector == 0])*-1

          }
          
          if (pheneValue == 0 ){
            tempWorm <- wormShape
            if (addAsymmetry ){
              tempWorm[i, (pointsVector == 0) & (tempWorm[i,] == 0.5 )] <- 1
            
            }
            pointsVector[,pointsVector == 0 ] <- tempWorm[i, pointsVector == 0]
          }
        }
      }
      
          comparisonGeneChanges <- wormShape[ comparison , ]
      cat("\nManually assigning zero points wherever genes changed for comparison",comparison)
      
          pointsVector[,  comparisonGeneChanges != 0  ] <- 0   ##zero out the changed comparisons
      
      
          
          cat("\nCalculating exact points received")

          temp["DEscores",] <- temp["DEscores",] + pointsVector
                    
          
          cat("\nCorrecting perfection counters") 
          
      
          temp["perfectionCounter", ] <-   temp["perfectionCounter", ]  +  sign(pointsVector)
      
      
      subjectDEscore <-  subjectDEscore + temp
      
      
  
    }
  
  }
  
  }
  
  
  if (totalVisits > 2){
    
    
    cat("\n\nMarking genes with perfect scores for subject",uniqueSubject)
    perfectionLogical <- (totalVisits-1) == abs( subjectDEscore["perfectionCounter",]  )
    cat("\n...")
    perfectionLogical[is.na(perfectionLogical)] <- FALSE
    cat("\n...")
    cat("\nApplying perfection bonus...")
    
    #perfection bonus
    subjectDEscore[1,perfectionLogical] <- as.matrix(subjectDEscore[1,perfectionLogical])*2 
    
    cat("\n...")
    
  }
  
  
  
  data["DEscores",! names(data) %in% stringColumns] <-   data["DEscores",! names(data) %in% stringColumns] + subjectDEscore[1,]
  
  subjectLevelOutput <- rbind(subjectLevelOutpout, subjectDEscore[1,])
  row.names(subjectLevelOutput)[[nrow(subjectLevelOutput)]] <- uniqueSubject 
  
  summaryResultsTable <- rbind(summaryResultsTable, c(uniqueSubject,totalVisits))
  gc()
  
 
}




    THISISYOUROUTPUT <- data["DEscores",]
    THISISYOUROUTPUT <- t(THISISYOUROUTPUT)


outputFile <- paste(tempDir, "/output_", PHENE, "_", dxChoice, "_", Sys.Date(),".csv", sep="")
write.csv(THISISYOUROUTPUT, outputFile)
cat("\nOutput file created: ", outputFile, "\n")

reportFile <- paste(tempDir, "/output_", PHENE, "_", dxChoice, "_", Sys.Date(),"_REPORT SUMMARY.csv", sep="")
write.csv(summaryResultsTable,reportFile)
cat("Report file created: ", reportFile, "\n")

# Displays the files - can't do that from within a web app like this
### file.show(outputFile)
### file.show(reportFile)

### View(t(subjectLevelOutput))








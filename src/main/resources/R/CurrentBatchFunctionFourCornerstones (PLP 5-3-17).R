


## This function calculates the PREDICTOR variable, including z-scoring all (and only) 
#the needed columns


calculatePREDICTOR <- function(data, predictor, direction, increasedPanel, decreasedPanel, LEVELS = TRUE, slopes = FALSE, MAX = FALSE, maxSlopes = TRUE) {
  
  
  
  
  
  #initialize some variables for later#
  
  increased <- NULL
  decreased <- NULL
  
  ##here is a list of all the possible composite panels##
  compositePanels <- c("increasedPanel",
                       "decreasedPanel",
                       "UNIVERSAL",
                       "BIOM")
  
  ##ask for user input of increased markers#
  
  if (direction == "I"){
    
    increased <- predictor
    decreased <- NULL
    
    ##Stop script and warn user if they request a marker that isn't in the database#
    if ( !(increased %in% c(names(data), compositePanels)) ) {
      
      stop(paste("You asked to test", increased," as an increased marker. There is no column with the name", increased, "in this database. Please rerun the script and try again."))
    }
    
    
  }
  
  if (direction == "D"){
    
    decreased <- as.character(predictor)
    increased <- NULL
    
    ##Stop script and warn user if they request a marker that isn't in the database#
    if ( !(decreased %in%  c(names(data), compositePanels)) ) {
      
      stop(paste("You asked to test", decreased," as a decreased marker. There is no column with the name", decreased, "in this database. Please rerun the script and try again."))
    }
  
    
  }
  
  
  if (direction == "Panel"){
    
    if (predictor == "increasedPanel"){
      increased <- as.character(predictor)
      decreased <- NULL
      

      
    }
    
    if (predictor == "decreasedPanel"){
      decreased <- as.character(predictor)
      increased <- NULL 
      
    }
    
    if (predictor == "BIOM"){
      decreased <- "decreasedPanel"
      increased <- "increasedPanel"
      
    }
    if (predictor == "UNIVERSAL"){
      
      decreased <- c("decreasedPanel","MOOD","YMRS")
      increased <- c("increasedPanel","HAMD")
      
    }
    
    
    
    if (predictor == "SASS"){
      
      decreased <- "Mood"
      increased <- "Anxiety"
      
    }
    
    
    if (predictor == "SASSCFIS"){
      
      decreased <- "Mood"
      increased <- c("CFIS", "Anxiety")
      
    }
    
    if (predictor == "BIOMCFIS"){
      
      decreased <- "decreasedPanel"
      increased <- c("CFIS", "increasedPanel")
      
    }
    

    
  }
  
  
  


  
  
  
  ##Check if the user input was zero increased predictors and only one decreased predictor
  #set singleNegativePredictor to TRUE if this is the case
  if (length(decreased) == 1 && length(increased) == 0)  { singleNegativePredictor <- TRUE } else { singleNegativePredictor <- FALSE }  
  
  #If user inputs no increased or decreased markers, stop script and warn user
  if (length(c(increased, decreased)) == 0) {
    print("NO INCREASED OR DECREASED MARKERS ENTERED. SCRIPT STOPPING.")
    gmessage(message="No increased or decreased markers entered.\nSCRIPT STOPPING.", title="Error")
    stop()
  }
  
  
  
  
  ##the variables that are left over in these Z variables 
  #after the next block of code are those that will
  #be z-scored 
  increasedZ <- increased
  decreasedZ <- decreased
  
  ## If the user lists RNA composite panels in either the increased or decreased vectors
  # this code gives us all of the corresponding composite genes that will need to be 
  # z-scored so that we can calculate the composite panels using them. It also prevents  
  # the program from trying to Z-score the RNA panel directly, which would be unecessary#
  
  
  
  
  if ("decreasedPanel" %in% c(increasedZ, decreasedZ)){
    
    #add the genes that compose the panel to the list of genes we'll have to z-score 
    decreasedZ<- c(decreasedZ, decreasedPanel)
    
    ##But do not try to z-score the composite itself
    increasedZ <- increasedZ[!increasedZ %in% "decreasedPanel"]
    decreasedZ <- decreasedZ[!decreasedZ %in% "decreasedPanel"]
    
  }
  
  
  if ("increasedPanel" %in% c(increasedZ, decreasedZ)){
    increasedZ<- c(increasedZ, increasedPanel)
    
    ##But do not try to z-score the composite itself
    increasedZ <- increasedZ[!increasedZ %in% "increasedPanel"]
    decreasedZ <- decreasedZ[!decreasedZ %in% "increasedPanel"]
    
  }
  
  

  
  
  #for all the markers you want to z-score
  for ( marker in c(increasedZ, decreasedZ) ){
    
    
    #get the subject ID and visitnumber for all incomplete cases
    incompletes <- data[!complete.cases(data[marker]),][,c("Subject","VisitNumber")]
    
    
    #if there are is any incomplete data for this marker
    if ( nrow(incompletes) > 0 ){
      
      #loop through visits, starting with most recent incomplete one
      for ( subjNum in nrow(incompletes):1 ){
        
        #get subject name from current row
        subj <- incompletes[subjNum,1] 
        
        #for that subject, get all their visit numbers greater than the one we're currently processing, if any
        visitNumsGreaterThan <- data[data$Subject == subj & data$VisitNumber > incompletes[[subjNum,"VisitNumber"]], ]$VisitNumber
        
        #increment down the visit numbers above the one you dropped to maintain the sequence
        if ( any(  visitNumsGreaterThan > incompletes[subjNum, 2] )  ){ #only do this if there are any visit numbers greater than the one you dropped
          data[data$Subject == subj & data$VisitNumber > incompletes[[subjNum,"VisitNumber"]], ]$VisitNumber <- (visitNumsGreaterThan - 1)
        }
      }
      
      #get rid of missing data
      data <- data[complete.cases(data[marker]),]
    }
    
    #if the marker has any 0 values, add one to the vector because otherwise we'll end up dividing by zero
    if (sum(as.matrix(data[marker])[,1]==0) > 0 ){ 
      data[marker] <- data[marker] + 1
    }
    
  }
  
  
  
  
  
  ######### BEGIN MAX
  
  if (MAX == TRUE){
    
    
    for ( pred.var in c(increasedZ,decreasedZ)){
      
      colNameMAX <- paste0(pred.var,"_MAX")
      
      if ( !(colNameMAX %in% names(data)) ){
        data[,colNameMAX] <- NA  #create slopes column
      }
      
      
      
      for (current.visit in 1:max(data$VisitNumber)){ #cycle from first visit to the max number of visits
        
        if (current.visit == 1){
          
          #for first visit, max is the same as the level
          data[data$VisitNumber == 1, colNameMAX] <- data[data$VisitNumber == 1,pred.var]
          
        }else{
          
          data.prev.visit <- data[data$VisitNumber == (current.visit-1),]  #get visit just prior
          data.current.visit  <- data[data$VisitNumber == current.visit,]  #get current.visit
          
          #now reduce the "early visit" data.frame to the subjects that exist in the later set
          data.prev.visit <- data.prev.visit[data.prev.visit$Subject %in% unique(data.current.visit$Subject),]
          #data.current.visit <- data.current.visit[data.current.visit$Subject %in% unique(data.early.visit$Subject),]
          
          #pending max calculations, make the current visit max the same as the prev visit max
          data.current.visit[order(data.current.visit$Subject, data.current.visit$VisitNumber),colNameMAX] <- data.prev.visit[order(data.prev.visit$Subject, data.prev.visit$VisitNumber),colNameMAX]
          
          
          
          
          if (  pred.var %in% increasedZ ) {
            
          # create data frame with the current visits value where that value is greater than the max value recorded for the visit before it
          recentVisitGreater <- data.current.visit[data.current.visit[pred.var] > data.current.visit[colNameMAX],pred.var]
          
          # where the current visits value is greater than the max value of the visit prior, make the max value the current value
          data.current.visit[data.current.visit[pred.var] > data.current.visit[colNameMAX], colNameMAX] <-  recentVisitGreater
          
          }
          
          
          if ( pred.var %in% decreasedZ ) {
            
            # create data frame with the current visits value where that value is less than the min value recorded for the visit before it
             recentVisitLesser <- data.current.visit[data.current.visit[pred.var] < data.current.visit[colNameMAX],pred.var]
             
             # where the current visits value is less than the max value of the visit prior, make the min value the current value
            data.current.visit[data.current.visit[pred.var] < data.current.visit[colNameMAX], colNameMAX] <-  recentVisitLesser
            
          }
          
          data[data$VisitNumber == current.visit, colNameMAX] <- data.current.visit[colNameMAX]
          
        
        }
        
      }
      
      if ( pred.var %in% increasedZ ) {
        
        if (!(colNameMAX %in% increasedZ)){
          increasedZ <- c(increasedZ, colNameMAX) 
        }
        
      }
      
      
      if ( pred.var %in% decreasedZ ) {
        
        if (!(colNameMAX %in% decreasedZ)){
          decreasedZ <- c(decreasedZ, colNameMAX) 
        }
        
      }
      
      
    }
  }
  ####### END MAX
  
  
  
  
  
  
  ######### BEGIN FOLD CHANGE
  if (slopes == TRUE | maxSlopes == TRUE){
    

    
    #drop people with single visits
    moreVisits <- aggregate(VisitNumber~Subject, data=data, sum)
    moreVisits["oneVisit"] <- as.numeric(moreVisits$VisitNumber == 1)
    moreVisits <- moreVisits[moreVisits$oneVisit == 0,]
    data <- merge(data, moreVisits["Subject"])
    data$Visit.Date <- as.POSIXct(data$Visit.Date,  format="%m/%d/%Y")
    data <- subset(data, complete.cases(data$Visit.Date))
    
    
    increased.pending <- NULL
    decreased.pending <- NULL
    increased.pending.maxfc <- NULL
    decreased.pending.maxfc <- NULL
    
    for ( pred.var in c(increasedZ,decreasedZ)){
      
    for (i in 2:max(data$VisitNumber)){ #cycle from second visit to the max number of visits
      
      
      colNameFC <- paste0(pred.var,"_FC")
      
      if ( !(colNameFC %in% names(data)) ){
        data[,colNameFC] <- NA  #create slopes column
      }
      
      data.early.visit <- data[data$VisitNumber == (i-1),]  #get visit just prior
      data.late.visit  <- data[data$VisitNumber == i,]  #get visit just after
      
      #now reduce the "early visit" data.frame to the subjects that exist in the later set
      data.early.visit <- data.early.visit[data.early.visit$Subject %in% unique(data.late.visit$Subject),]
      #data.late.visit <- data.late.visit[data.late.visit$Subject %in% unique(data.early.visit$Subject),]
      
      data.late.visit[, colNameFC] <- data.late.visit[,pred.var] / data.early.visit[, pred.var] #calculate fold change
      data.late.visit[, colNameFC] <-  data.late.visit[, colNameFC] - 1 # symmetry
      data.late.visit[, colNameFC] <-  data.late.visit[, colNameFC] * 100 # times 100
      
      data.late.visit$Visit.Date <- data.late.visit$Visit.Date - data.early.visit$Visit.Date #calculate fold change
      data.late.visit$Visit.Date <- round(data.late.visit$Visit.Date) 
      data.late.visit$Visit.Date <- (round(data.late.visit$Visit.Date) / 365) 
      
      
      
      data.late.visit[, colNameFC] <- data.late.visit[, colNameFC] / as.numeric(data.late.visit$Visit.Date) # fold change
      
      
      
      data[data$VisitNumber == i,colNameFC]  <- data.late.visit[colNameFC] #add it to the main dataset
      
      

      
      
      
      if (maxSlopes == TRUE){
        
        # keep track of the max slope up to this visit
        if ( i == 2 ){  
          
          #if this is the first of the visits we're looking into, then max slope == the current visit
          data.max.slope <- data.frame(data.late.visit[, c("Subject",colNameFC)] )
          
        }else{
          
          #restrict the max slope vector to those subjects that have a current visit
          data.max.slope <- data.max.slope[data.max.slope$Subject %in% unique(data.late.visit$Subject),]
          
          if (pred.var %in% increasedZ){
          #if the current visit has a bigger slope than our previously stored max visit, call it our max visit
          data.max.slope[data.max.slope[,colNameFC] < data.late.visit[,colNameFC], colNameFC ] <- data.late.visit[data.max.slope[,colNameFC] < data.late.visit[,colNameFC], colNameFC ]
          }
          if (pred.var %in% decreasedZ){
            #if the current visit has a smaller slope than our previously stored minimum visit, call it our min visit
            data.max.slope[data.max.slope[,colNameFC] > data.late.visit[,colNameFC], colNameFC ] <- data.late.visit[data.max.slope[,colNameFC] > data.late.visit[,colNameFC], colNameFC ]
          }
        }
        
        
        #when done, label the result with the name of the gene plus _MAXFC
        data[data$VisitNumber == i,paste0(pred.var,"_MAXFC")] <- data.max.slope[,colNameFC]
        
        
      }

      

      
      if ( pred.var %in% increasedZ ) {
        
        
        if (maxSlopes == TRUE){
          
        if (!(paste0(pred.var,"_MAXFC") %in% increasedZ)){
          increased.pending.maxfc <- c(increased.pending.maxfc, paste0(pred.var,"_MAXFC") )
        }
        
        
      }
       
      if (slopes == TRUE){
        if ( !(colNameFC %in% increasedZ) ){
          increased.pending <-  c(increased.pending, colNameFC)
        }
      }
      
      }
      
      if ( pred.var %in% decreasedZ ) {
        


        
        if (maxSlopes == TRUE){

        
 
        if (!(paste0(pred.var,"_MAXFC") %in%  decreasedZ)){
           decreased.pending.maxfc <- c(decreased.pending.maxfc, paste0(pred.var,"_MAXFC")) 
        }
       }
        
    
    
    if (slopes == TRUE){
      if (!(colNameFC %in%  decreasedZ)){
        decreased.pending <- c(decreased.pending, colNameFC)
      }
    }     
      }
      

      
    }
    }
    
      
      increasedZ <- c(increasedZ, increased.pending, increased.pending.maxfc)
      decreasedZ <-  c(decreasedZ, decreased.pending, decreased.pending.maxfc)
    
      increasedZ <- unique(increasedZ)
      decreasedZ <- unique(decreasedZ)
      
    #drop first visits
    data <- data[data$VisitNumber != 1,]
    
}
    
  
  
    
    
    
    
  ######### END FOLD CHANGE
  
  
  
  
  

  
  
  
  
  
  
  
  ##Split dataset by dx and gender
  dataMBP <- data[data$dx == "M-BP",]
  dataFBP <- data[data$dx == "F-BP",]
  dataMSZ <- data[data$dx == "M-SZ",]
  dataFSZ <- data[data$dx == "F-SZ",]  
  dataMSZA <- data[data$dx == "M-SZA",]
  dataFSZA <- data[data$dx == "F-SZA",]
  dataMMDD <- data[data$dx == "M-MDD",]
  dataFMDD <- data[data$dx == "F-MDD",]
  dataMPTSD <- data[data$dx == "M-PTSD",]
  dataFPTSD <- data[data$dx == "F-PTSD",]
  dataMPsych <- data[data$dx == "M-PSYCH",]
  dataMMood <- data[data$dx == "M-MOOD",]
  
  
  
  
  
  
  for (column in c(increasedZ, decreasedZ)) {
    
    if (!(column %in%  names(data))  ){
      
      stop("The marker " ,column," (part of the panel ",predictor,") does not exist in the database")
    }
    
    ##for each of the requested genes/columns, including those that form the composite 
    #panels, create a new column that has the same name but with a z at the end so 
    #that we know it's the z-scored version##
    newCol <- paste(column, "z", sep="")
    

      ##for each of the requested columns, calculate z scores by dx
      #and put the values into the new column we just created## 
      dataMBP[newCol] <- scale(dataMBP[column])
      
      dataFBP[newCol] <- scale(dataFBP[column])
      
      dataMSZ[newCol] <- scale(dataMSZ[column])
      
      dataFSZ[newCol] <- scale(dataFSZ[column])
      
      dataMSZA[newCol] <- scale(dataMSZA[column])
      
      dataFSZA[newCol] <- scale(dataFSZA[column])
      
      dataMMDD[newCol] <- scale(dataMMDD[column])
      
      dataFMDD[newCol] <- scale(dataFMDD[column])
      
      dataMPTSD[newCol] <- scale(dataMPTSD[column])
      
      dataFPTSD[newCol] <- scale(dataFPTSD[column])
      
      dataMMood[newCol] <- scale(dataMMood[column])
      
      dataMPsych[newCol] <- scale(dataMPsych[column])
   
    
  }
  
  ##aggregate all the z scored data## 
  data <- rbind(dataMBP, dataFBP, dataMMDD, dataFMDD, 
                dataMSZ, dataFSZ, dataMSZA, dataFSZA, dataMPTSD, 
                dataFPTSD, dataMMood, dataMPsych)

  
  
  
  
  
  
  
  ###### DEAL WITH CORNERSTONE VARIABLES AS NEEDED

    
    panel.vars <- NULL
    if ( (predictor == "increasedPanel") | (predictor == "decreasedPanel") ) {
      panel.vars <- get(predictor)
    }   
    if ( predictor == "BIOM" | predictor == "UNIVERSAL" ){
      
      panel.vars <- c(increasedPanel, decreasedPanel)
      
    }
    
    ## per cornerstone
    for ( predict.var in c(increased[!increased %in% "increasedPanel"], decreased[!decreased %in% "decreasedPanel"], panel.vars ) ) {
      
    #CALCULATE CORNERSTONE VARS
    colNameCORNERSTONE <- paste0(predict.var,"_CORNERSTONEz")
    if ( !(colNameCORNERSTONE %in% names(data)) ){
      data[,colNameCORNERSTONE] <- 0  
    }
    
    if ( LEVELS ){
      LEVELS.Z <- paste(predict.var, "z", sep="") #levels z-scored
    data[colNameCORNERSTONE] <- data[colNameCORNERSTONE] + data[LEVELS.Z] 
    }
    
    if ( slopes ){
        FC.Z <- paste(predict.var, "_FCz", sep="") #fold changes z-scored
        data[colNameCORNERSTONE] <- data[colNameCORNERSTONE] + data[FC.Z] 
    }
    
    if ( MAX ){
      MAX.Z <- paste(predict.var, "_MAXz", sep="") #max change z-scored
      data[colNameCORNERSTONE] <- data[colNameCORNERSTONE] + data[MAX.Z] 
    }
    
    if ( maxSlopes ){
      maxSlopes.Z <- paste(predict.var, "_MAXFCz", sep="") #max change z-scored
      data[colNameCORNERSTONE] <- data[colNameCORNERSTONE] + data[maxSlopes.Z] 
    }
      
      #take the average
      data[colNameCORNERSTONE] <- data[colNameCORNERSTONE]/ (LEVELS + MAX + slopes + maxSlopes) 
    
    }
    ####### END CORNERSTONE CALC
    

    

  ######################################
  
  
    
    
    
    for (panel in compositePanels){
      
      if (panel %in% c(increased,decreased)){
        data[panel] <- (data$VisitNumber*0)
        
        for (marker in get(panel)) {
          
            colNameCORNERSTONE <- paste0(marker,"_CORNERSTONEz")
            data[panel] <- data[panel] + data[colNameCORNERSTONE]
 
          
          
        }
        

      }
    }
    
    
  
  
  
  ##create dummy variables for the increased and decreased markers 
  #we will be populating these with the calculated PREDICTOR values##
  data["increasePREDICTOR"] <- (data$VisitNumber * 0)
  data["decreasePREDICTOR"] <- (data$VisitNumber * 0)
  
  
  
  if (!is.null(increased)){
    
    for (increasePredictor in increased) {
      
    
      
      ##if the marker is not a composite marker, then make sure we're grabbing the 
      #z-score version of it rather than the raw score
      if (!increasePredictor %in% compositePanels){
        
        
        increaseColumn <- paste0(increasePredictor,"_CORNERSTONEz") 
        
        ##add all the increased predictors together ...
        data["increasePREDICTOR"] <- data["increasePREDICTOR"] + data[increaseColumn]
        
      } 
      
    }
  }
  
  
  ###calculate the average for the decreased variables (if there are any)  
  if (!is.null(decreased)){
    for (decreasePredictor in decreased) {
      ##if the marker is not a composite marker, then make sure we're grabbing the 
      #z-score version of it rather than the raw score
      if (!decreasePredictor %in% compositePanels){
        
        decreaseColumn <- paste0(decreasePredictor,"_CORNERSTONEz") 
        
        #add all the decrease markers together...
        data["decreasePREDICTOR"] <- data["decreasePREDICTOR"] + data[decreaseColumn]
      } 
      
    }
    
  }
  
  
  
  ##if the user only input decreased predictors, then the PREDICTOR value will be 
  #the negative of the average of the decreased markers 
  if( is.null(increased)){
    data["PREDICTOR"] <- -data["decreasePREDICTOR"]
    ##if the user only input increased predictors, then the PREDICTOR value will be 
    #the negative of the average of the decreased markers
  } else if ( is.null(decreased)) {
    data["PREDICTOR"] <-  data["increasePREDICTOR"]
    ##if the user input both increased and decreased predictors, then the PREDICTOR will 
    #be the average of all the increased predictors minus the average of all the decreased 
    #predictors
  } else data["PREDICTOR"] <- data["increasePREDICTOR"] - data["decreasePREDICTOR"]
  

  if (("increasedPanel" %in% increased) & ("decreasedPanel" %in% decreased) ){
    data["PREDICTOR"] <- data["PREDICTOR"] + ((data["increasedPanel"] - data["decreasedPanel"])/length( c(increasedPanel,decreasedPanel)))
  }
  
  
  
  ##collect the following stuff into a list:
  output <- list(data, singleNegativePredictor, increased, decreased)
  #note: when you want the individual components of the output, do it like this:
  #data <- output[[1]]
  #singleNegativePredictor <- output[[2]] 
  #increased <- output[[3]] (note: this is a vector containing all the increased markers that were inputted by the user)
  #decreased <- output[[4]] (note: same as output[[3]] but for the decreased predictors)
  
  #return the output for the user to access 
  return(output)
  
}








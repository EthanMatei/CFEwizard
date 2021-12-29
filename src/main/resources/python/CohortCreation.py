from pandas.core.dtypes.missing import notnull
import pandas as pd
import numpy as np
import re
import datetime as dt
from pandas.core.reshape.concat import concat
import sys # for command line arguments
import uuid   # for temporary file name
pd.options.mode.chained_assignment = None  # default='warn'

expectedNumArgs = 5
if len(sys.argv) != expectedNumArgs:
    sys.exit("Incorrect number of arguments for \"" + sys.argv[0] + "\" script expected: " + expectedNumArgs)

scoring_data_file = sys.argv[1]
phene_visit_file  = sys.argv[2]
admission_phene   = sys.argv[3]
temp_dir          = sys.argv[4]

# Print out command line arguments for debugging purposes
print("scoring data file:", scoring_data_file)
print("phene_visit_file:", phene_visit_file)
print("admission phene:", admission_phene)
print("temp dir:", temp_dir)

#### IMPORT FILES HERE  ####
data = pd.read_csv(scoring_data_file)
visit_dates = pd.read_csv(phene_visit_file)
#### IMPORT FILES HERE  ####

### remove the following with this data ###
data = data[data['Status(No need to followup Deseased, non-vet, or not tested)'] != 'NON-VET']
data = data[data['Status(No need to followup Deseased, non-vet, or not tested)'] != 'Not Tested']
data = data[data['Status(No need to followup Deseased, non-vet, or not tested)'] != 'control/no VA records']
data = data[data['Status(No need to followup Deseased, non-vet, or not tested)'] != 'VET but refuse to transfer record . NOCPRS,RECEIVESCAREELSEWHERE']
    
data['TestingDate'] = pd.to_datetime(data['TestingDate']) 
data['Admit date'] = pd.to_datetime(data['Admit date'])

### removing of duplicates 
df = pd.DataFrame(data)
selected_columns = df[['Hospitalizations Follow-up Database.SubjectID', 'TestingVisit','TestingDate']] ##take these columns out 
data2 = selected_columns.copy()
data2 = data2.drop_duplicates(subset=['Hospitalizations Follow-up Database.SubjectID','TestingVisit','TestingDate'],keep='first') #remove dupes'
data2['SubjectID copy'] = data['Actuarial and Subject Info.SubjectID'] #create copy of subjects
data2['Last Note'] = data['LastNote(System-Wide)'] 
data2 = data2.dropna()  
### first sheet done ###
data['SubjectID copy'] = data['Actuarial and Subject Info.SubjectID']
data = data.drop(['Hospitalizations Follow-up Database.SubjectID', 'TestingVisit','TestingDate'], axis=1) #drop them from original
result = data.append(data2)
### add first and second sheet together to get rid of dupes ###

### Date Order Creation, group by subjectid ###
result['Date Order'] = result[['Admit date','TestingDate']].max(axis=1)
result = result.sort_values(by=['SubjectID copy',"Date Order"], ascending=[True, True]) 
data = result.dropna(subset = ["Date Order"])

### merge so that there are visit dates for the phene visits to take out straggling duplicates later on###
data = pd.merge(data,visit_dates,left_on='TestingVisit', right_on = 'TestingVisit', how='left')
data['PheneVisit Date'] = pd.to_datetime(data['PheneVisit Date'])

### creating columns
data['Time to 1st Hosp'] = pd.to_datetime("")
data['first - last'] = pd.to_datetime("")
data['1st Date - LastNote'] = pd.to_datetime("")
data['Time Future'] = ("")
data['Length of Follow up for Future'] = ("")
data['yes/no'] = ("")
data['duped phene'] = ""
### ###


data['PheneVisit Date'] = data['PheneVisit Date'].fillna(0)
data['duped phene'] = data['TestingVisit'].duplicated(keep = False)

### find duplicates with same phenevisits but different phenedates, compare if phenevisit date == phenedate, if not drop.
for index, row in data.iterrows():
    if (data['PheneVisit Date'][index]) != 0:
        data['yes/no'][index] = (data['PheneVisit Date'][index] != data['TestingDate'][index])
        if data['duped phene'][index] and data['yes/no'][index]:
            data = data.drop(index)
### end duplicate dropping ###

data['Score'] = data[admission_phene]    # Admission phene set here

# Score of 0
data['Score'] = data['Score'].fillna(0)

###
# ## duplicates 
data = data.reset_index(drop=True)
data.loc[data['Score'] > 0, 'First Date'] = data['Date Order'] #get the date order where score is > 0 
data['First Date'] = data['First Date'].shift(-1) #shift up




# #^^^^^###

### getting the first - last date
subjects = [] 
for index, row in data.iterrows():
    if row['Actuarial and Subject Info.SubjectID'] not in subjects: #find the first date for each subject so we can see if its < 365
        subjects.append(data['Actuarial and Subject Info.SubjectID'][index])
        data['first - last'][index] = data['Date Order'][index] #place the datefirst in the correct row/cell so we can fill 

data['first - last'] = data['first - last'].ffill()
 #####

 ### finding the first date 
data['First Date'] = (data.groupby('Actuarial and Subject Info.SubjectID')
                      .apply(lambda x: x['Admit date'].where(x['Score'] == 1).bfill())
                      .fillna('12/30/1899').reset_index(level=0, drop=True))
data['First Date'] = data['First Date'].bfill()


### Filling in time to 1st hosp
for index, row in data.iterrows():
    if row['TestingDate'] == "N/A":
        data['Time to 1st Hosp'][index] = pd.to_timedelta(365, unit='D')
    else:
        data['Time to 1st Hosp'][index] = pd.to_datetime(data['First Date'][index]) - pd.to_datetime(data['TestingDate'][index]) #calc time to 1st hosp
        data['Time Future'][index] = pd.to_datetime(data['First Date'][index]) - pd.to_datetime(data['TestingDate'][index])  #time to future



### greater than 365 or less than 0 
data['Time to 1st Hosp']=pd.to_timedelta(data['Time to 1st Hosp']).dt.days #turn to days 
data.loc[data['Time to 1st Hosp'] > 365, 'Time to 1st Hosp'] = 365 #get the date order
data.loc[data['Time to 1st Hosp'] < 0, 'Time to 1st Hosp'] = 365
#data['Time to 1st Hosp'] = pd.to_timedelta(data['Time to 1st Hosp Date'])  

# get first date and time since to see if visit is < 365, give NA
for index, row in data.iterrows():
    data['1st Date - LastNote'][index] =(pd.to_datetime(data['LastNote(System-Wide)'][index]) - data['first - last'][index])
    if data['1st Date - LastNote'][index] < pd.to_timedelta(365, unit='D'):
        data['Time to 1st Hosp'][index] = "N/A"
## find first year hosp number

#### create firstyearscore of how many hospitlizations there are within a year of phenevisit
dfc = data.copy()
dct = {}
from datetime import datetime
for x in data['TestingVisit'].unique(): 
    val = data[data['TestingVisit']==x]['TestingDate'].unique()
    subj = data[data['TestingVisit'] == x]['SubjectID copy'].unique()
    if not val: 
        continue
    else:
        dfc['TestingVisit'] = val[0] 
    dfc['c'] = (dfc['Admit date'] - dfc['TestingVisit'])/np.timedelta64(1,'D')
    dfd = dfc[(dfc['Score'] == 1) & (dfc['c'] > 0) & (dfc['c'] <= 365) & (dfc['SubjectID copy'] == subj[0])]
    dct[x] = len(dfd)

fin_df = pd.DataFrame({'TestingVisit':list(dct.keys()),'FirstYearScore':list(dct.values())})
#### finished firstyearscore ###


### number of future hosp, counting bottom up ###
data['Number of all future Hospitilzation'] = (
    data.loc[::-1]
        .groupby(['SubjectID copy'])['Score']
        .transform(lambda s: s.shift(fill_value=0).cumsum())
)
### finished futurehosp score ###


### length of follow up for future to calculate hospital Frequency ###
data['Length of Follow up for Future'] =  pd.to_datetime(data['Last Note']) - pd.to_datetime(data['TestingDate'])
data['Length of Follow up for Future'] = data['Length of Follow up for Future'].fillna(pd.Timedelta(1))

data['Length of Follow up for Future'] = (data['Length of Follow up for Future'] / np.timedelta64(1, 'D')).astype(int)
data['HospFreq'] = ((data['Number of all future Hospitilzation']) / data['Length of Follow up for Future'])
### finished hosp freq

## check for NA cells where length of follow up < 365
data.loc[data['Length of Follow up for Future'] < 365, 'Time to 1st Hosp'] = 'N/A' 

finished = pd.DataFrame()
finished = data[data['TestingVisit'].notna()]
finished['HospitalizationCohort'] = 1 #cohort creation 
finished.loc[finished['Time to 1st Hosp'] != 'N/A', 'First Year Cohort'] = 1 #cohort creation give 1 where not N/A
finished['Time to 1st Hosp'].fillna(0) 

find_df = pd.merge(fin_df,finished,left_on='TestingVisit', right_on = 'TestingVisit', how='inner') #combine

find_df.dropna(how='all', axis=1, inplace=True)

output_file_name = temp_dir + "/prediction-cohort-" + str(uuid.uuid4()) + ".csv"

find_df.to_csv(output_file_name)

print ("Output file created: " + output_file_name)

# find_df.to_csv('./OutputFiles/cohort_finished.csv')







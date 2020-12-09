CFE Wizard
============================================

Convergent Functional Evidence Wizard

Steps/Databases:

1. Discovery
2. Prioritization (using the literature database)
3. Validation
4. Testing

Data is entered into MS Access databases, and those databases are uploaded to the CFE Wizard,
and the data from them are stored into the CFE Wizard's MySQL database.

**Scoring**

| Score          | Calculation                                                                                |
| -------------- | ------------------------------------------------------------------------------------------ |
| Discovery      | Use "AP Score" if set, otherwise, use "DE Score"                                           |
| Prioritization | Sum the HUBRAIN, HUPER, HUGENEASSOC, HUGCNV, NHBRAIN, NHPER, NHGENEASSOC and NHGCNV scores |
| Validation     | See table below                                                                            |
| Testing        | Sum the 4 scores in the testing database table                                             |
| Overall        | Calculated a weighted sum (default weight of 1.0 for each) for the above 4 scores          |


Validation scoring:

| Validation   | Score                                                |
| ------------ | ---------------------------------------------------- |
| Bonferroni   | 6 points (significance <= 0.05 with correction)      |
| Nominal      | 4 points (significance <= 0.05)                      |
| Stepwise     | 2 points (significance > 0.05)                       |
| Non-Stepwise | 0 points (significance > 0.05)                       |

---

Discovery Database
---------------------

Table: **Discovery**

| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |
| APscores                              | apScores                            | Double     |
| AP Percentile                         | apPercentile                        | Double     |
| AP Score                              | apScore                             | Double     |
| AP Change                             | apChange                            | String     |
| DEscores                              | deScores                            | Double     |
| DE Percentile                         | dePercentile                        | Double     |
| DE Score                              | deScore                             | Double     |
| DE Change                             | deChange                            | String     |
 

---

Priortization Database
----------------------

 Table: **Prioritization**
 
 
| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |
| HUBRAIN Score                         | huBrainScore                        | Double     |
| HUBRAIN Info                          | huBrainInfo                         | String     | 
| HUPER Score                           | huPerScore                          | Double     |
| HUPER Info                            | huPerInfo                           | String     | 
| HUGENEASSOC Score                     | huGeneAssocScore                    | Double     |
| HUGENEASSOC Info                      | huGeneAssocInfo                     | String     | 
| HUGCNV Score                          | huGCnvScore                         | Double     |
| HUGCNV Info                           | huGCnvInfo                          | String     | 
| NHBRAIN Score                         | nhBrainScore                        | Double     |
| NHBRAIN Info                          | nhBrainInfo                         | String     |
| NHPER Score                           | nhPerScore                          | Double     |
| NHPER Info                            | nhPerInfo                           | String     |
| NHGENEASSOC Score                     | nhGeneAssocScore                    | Double     |
| NHGENEASSOC Info                      | nhGeneAssocInfo                     | String     | 
| NHGCNV Score                          | nhGCnvScore                         | Double     |
| NHGCNV Info                           | nhGCnvInfo                          | String     | 


---

Validation Database
-------------------


Table: **Validation**

| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |
| Sig                                   | sig                                 | Double     |
| Validation                            | validation                          | String     |


---

Testing Database
----------------

Table: **Testing**
 

| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |
| SMSLowMood Score                      | smsLowMoodScore                     | Double     |
| HAMD Score                            | hamdScore                           | Double     |
| First Year Depression Score           | firstYearDepressionScore            | Double     |
| All Future Depression                 | allFutureDepression                 | Double     |




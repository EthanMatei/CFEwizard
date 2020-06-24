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

Discovery Database
---------------------

Table: **Discovery**

| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |
| APscores                              | apScore                             | Double     |
| AP Percentile                         | apPercentile                        | Double     |
| Ap Score                              | apScore                             | Integer    |
| AP Change                             | apChange                            | String     |
| DEscores                              | deScores                            | Double     |
| DE Percentile                         | dePercentile                        | Double     |
| DE Score                              | deScore                             | Integer    |
| DE change                             | deChange                            | String     |
 
 
 Table: **Prioritization**
 
 
| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |
| HUBRAIN Score                         | huBrainScore                        | Integer    |
| HUBRAIN Info                          | huBrainInfo                         | String     | 
| HUPER Score                           | huPerScore                          | Integer    |
| HUPER Info                            | huPerInfo                           | String     | 
| HUGENEASSOC Score                     | huGeneAssocScore                    | Integer    |
| HUGENEASSOC Info                      | huGeneAssocInfo                     | String     | 
 
...


Table: **Validation**

| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |

...

Table: **Testing**
 

| MS Access Column                      | MySQL Column                        | Type       |
| ------------------------------------- | ----------------------------------- |----------- |
| Probeset                              | probeset                            | String     |
| GeneCards Symbol                      | geneCardsSymbol                     | String     |
| Gene Title                            | geneTitle                           | String     |
| Change in expression in tracked phene | changeInExpressionInTrackedPhene    | String     |

...

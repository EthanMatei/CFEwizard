CFE Wizard
============================================

Convergent Functional Evidence Wizard

Steps/Databases:

1. Discovery
2. Prioritization (using the literature database)
3. Validation
4. Testing

Data inputs into the CFE Wizard include:

* MS Access Databases
* CSV files
* Manually entered values

The CFE Wizard uses a MySQL database to store values that need to persist.

For developer information, see:

* [Developers Guide](./docs/DevelopersGuide.md)
* [Development Environment Setup](./docs/DevelopmentEnvironmentSetup.md)


For information about the CFE Wizard's calculations, see: [Pipeline](./docs/Pipeline.md)

---


**Scoring**

| Score          | Calculation                                                                                |
| -------------- | ------------------------------------------------------------------------------------------ |
| Discovery      | Use "DE Score"                                                                             |
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




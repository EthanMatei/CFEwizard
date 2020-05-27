package cfg.rules;



/**
 *  
 * @author mtavares
 *
 * CFG scoring as follow for Mood Psychiatric domain
 * If a gene appears in each of the following lists, 
 * it gets the associated number of points
 * 1)	Human Postmortem Brain 4pts
 * 2)	Human Peripheral Blood 2pts
 * 3)	Human Genetics 2 pts
 * 4)	Animal Brain 2 pts, 
 *      Ff the psychiatric domain is associted with a treatment or addictions it gets 0.5 pts, 
 *      mood gets full point
 * 5)	Animal Blood 1pt
 * 6)	Animal Genetics 1pt
 * If the Psychiatric domain is Treatment or Addictions 
 * it should get half the points of #1-6 above and Treatment and Addictions are additive 
 * if one is addiction and another row is treatment 
 * If the same gene appears multiple times in one db, it only gets scored once
 *
 */


public class Rules {
	
    

}

/**
 * 
 * How to Calculate the score for Suicide

1. Get the necessary MS Access databases

a. HU-BRAIN.accdb (Human Brain)
	- hu-brain (GENE)
	- Hu-brain (PROMET)
b. HUGEN.accdb (Human Gene)
	- CNV
	- Full Linkage database
	- hugen association
	- linkage with low LOD scores
c. HU-PER.accdb
	- HU-PER(GENE)
	- HU-PER(PROMET)


2. Identify the required tables (data) from the MS Access databases

a. HU-BRAIN.accdb (Human Brain)
	- hu-brain (GENE)
	- Hu-brain (PROMET)
b. HUGEN.accdb (Human Gene)
	- CNV
	- hugen association
c. HU-PER.accdb
	- HU-PER(GENE)
	- HU-PER(PROMET)

3. Identify the columns necessary to calculate the score

	- GeneSymbol
	- Psychiatric Domain
	- Sub-domain
	- Relevant disorder

4. Identify the columns values necessary to determine if a certain record must be included into the score calculation

	- Psychiatric Domain = OTHER
	- Sub-domain = BEHAVIOR
	- Relevant disorder = SUICID (to include terms such as "suicidality")


5. Define the scoring weights

	- hu-brain (GENE) WEIGHT = 4
	- Hu-brain (PROMET) WEIGHT = 4

	- CNV WEIGHT = 2
	- hugen association WEIGHT = 2

	- HU-PER(GENE) WEIGHT = 2
	- HU-PER(PROMET) WEIGHT = 2

6. Define the ADDITIVEs

	- hu-brain (GENE) ADDITIVE = 4
	- Hu-brain (PROMET) ADDITIVE = 4

	- CNV ADDITIVE = 2
	- hugen association ADDITIVE = 2

	- HU-PER(GENE) ADDITIVE = 2
	- HU-PER(PROMET) ADDITIVE = 2

7. Define any condition associated with calculating the score

	- hu-brain (GENE) MAX_SCORE = 4
	- Hu-brain (PROMET) MAX_SCORE = 4

	- CNV MAX_SCORE = 2
	- hugen association MAX_SCORE = 2

	- HU-PER(GENE) MAX_SCORE = 2
	- HU-PER(PROMET) MAX_SCORE = 2

	- If a gene occurs multiple times in one table, score it only once.

8. (Optional) Obtain master list of the genes that need the scores to be calculated
	
	- GeneSymbolMasterList


9. Process the data

	- hu-brain (GENE)
		- read GeneSymbol Psychiatric Domain  Sub-domain  Relevant disorder from hu-brain (GENE) table
		- If GeneSymbol Not in HuBrainGeneSymbolList
			- add GeneSymbol to HuBrainGeneSymbolList
			- GeneSymbolScore = hu-brain (GENE) WEIGHT
		  Else
			- get current GeneSymbolScore
			- If GeneSymbolScore < hu-brain (GENE) MAX_SCORE
				GeneSymbolScore = GeneSymbolScore + u-brain (GENE) ADDITIVE
			

	- Hu-brain (PROMET)
		- read GeneSymbol Psychiatric Domain  Sub-domain  Relevant disorder from Hu-brain (PROMET) table
		- If GeneSymbol Not in HuBrainGeneSymbolList
			- add GeneSymbol to HuBrainGeneSymbolList
			- GeneSymbolScore = hu-brain (PROMET) WEIGHT
		  Else
			- get current GeneSymbolScore
			- If GeneSymbolScore < hu-brain (PROMET) MAX_SCORE
				GeneSymbolScore = GeneSymbolScore + u-brain (PROMET) ADDITIVE



	- HU-PER (GENE)
		- read GeneSymbol Psychiatric Domain  Sub-domain  Relevant disorder from HU-PER (GENE) table
		- If GeneSymbol Not in HuPerGeneSymbolList
			- add GeneSymbol to HuPerGeneSymbolList
			- GeneSymbolScore = HU-PER (GENE) WEIGHT
		  Else
			- get current GeneSymbolScore
			- If GeneSymbolScore < HU-PER (GENE) MAX_SCORE
				GeneSymbolScore = GeneSymbolScore + HU-PER (GENE) ADDITIVE
			

	- HU-PER (PROMET)
		- read GeneSymbol Psychiatric Domain  Sub-domain  Relevant disorder from HU-PER (PROMET) table
		- If GeneSymbol Not in HuPerGeneSymbolList
			- add GeneSymbol to HuPerGeneSymbolList
			- GeneSymbolScore = HU-PER (PROMET) WEIGHT
		  Else
			- get current GeneSymbolScore
			- If GeneSymbolScore < HU-PER (PROMET) MAX_SCORE
				GeneSymbolScore = GeneSymbolScore + HU-PER (PROMET) ADDITIVE


	- CNV
		- read GeneSymbol Psychiatric Domain  Sub-domain  Relevant disorder from CNV table
		- If GeneSymbol Not in HuCNVAsscGeneSymbolList
			- add GeneSymbol to HuCNVAsscGeneSymbolList
			- GeneSymbolScore = CNV WEIGHT
		  Else
			- get current GeneSymbolScore
			- If GeneSymbolScore < CNV MAX_SCORE
				GeneSymbolScore = GeneSymbolScore + CNV ADDITIVE

	- hugen association
		- read GeneSymbol Psychiatric Domain  Sub-domain  Relevant disorder from hugen association table
		- If GeneSymbol Not in HuCNVAsscGeneSymbolList
			- add GeneSymbol to HuCNVAsscGeneSymbolList
			- GeneSymbolScore = hugen association WEIGHT
		  Else
			- get current GeneSymbolScore
			- If GeneSymbolScore < hugen association MAX_SCORE
				GeneSymbolScore = GeneSymbolScore + hugen association ADDITIVE


10. Verify that the conditions specified in 7 were met
	- Check to see that none of the scores in HuBrainGeneSymbolList > min (hu-brain (GENE) MAX_SCORE, hu-brain (PROMET) MAX_SCORE)
	- Check to see that none of the scores in HuPerGeneSymbolList > min (HU-PER (GENE) MAX_SCORE, HU-PER (PROMET) MAX_SCORE)
	- Check to see that none of the scores in HuCNVAsscGeneSymbolList > min (CNV MAX_SCORE, hugen association MAX_SCORE)

11. Combine all the scores

12. Display the results
	- If GeneSymbolMasterList is defined, display only the scores for the genes in the list


 */

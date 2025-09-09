-- V1_2__expand_columns.sql
-- Expanding relevantDisorder and subDomain columns

ALTER TABLE disorder MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE disorder MODIFY subdomain VARCHAR(255);

ALTER TABLE genelist MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE genelist MODIFY subDomain VARCHAR(255);


ALTER TABLE hubrainmet MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE hubrainmet MODIFY subDomain VARCHAR(255);

ALTER TABLE hubrainprot MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE hubrainprot MODIFY subDomain VARCHAR(255);

ALTER TABLE hupermet MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE hupermet MODIFY subDomain VARCHAR(255);

ALTER TABLE huperprot MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE huperprot MODIFY subDomain VARCHAR(255);

ALTER TABLE hubraingex MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE hubraingex MODIFY subDomain VARCHAR(255);

ALTER TABLE nhbraingex MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE nhbraingex MODIFY subDomain VARCHAR(255);

ALTER TABLE nhpergex MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE nhpergex MODIFY subDomain VARCHAR(255);

ALTER TABLE nhpermet MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE nhpermet MODIFY subDomain VARCHAR(255);

ALTER TABLE nhperprot MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE nhperprot MODIFY subDomain VARCHAR(255);

ALTER TABLE nhbrainmet MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE nhbrainmet MODIFY subDomain VARCHAR(255);

ALTER TABLE nhbrainprot MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE nhbrainprot MODIFY subDomain VARCHAR(255);

ALTER TABLE hupergex MODIFY relevantDisorder VARCHAR(255);
ALTER TABLE hupergex MODIFY subDomain VARCHAR(255);

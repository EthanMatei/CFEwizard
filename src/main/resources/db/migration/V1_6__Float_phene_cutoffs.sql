-- Change phene cutoffs from int to double
ALTER TABLE `CfeResults` MODIFY `lowCutoff` double;
ALTER TABLE `CfeResults` MODIFY `highCutoff` double;

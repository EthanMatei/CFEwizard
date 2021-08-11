
create table CfeResults (
  `cfeResultsId` bigint(20) NOT NULL AUTO_INCREMENT,
  `generatedTime` datetime not null,
  `results` mediumblob,
  `resultsType` varchar(255),
  `phene` varchar(255),
  `lowCutoff` int(11),
  `highCutoff` int(11),
  PRIMARY KEY (`cfeResultsId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


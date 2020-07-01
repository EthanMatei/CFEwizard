
CREATE TABLE `databaseuploadinfo` (
  `databaseUpdloadInfoId` int(11) NOT NULL AUTO_INCREMENT,
  `databaseName` varchar(255) DEFAULT NULL,
  `uploadFileName` varchar(255) DEFAULT NULL,
  `uploadTime` datetime DEFAULT NULL,
  PRIMARY KEY (`databaseUpdloadInfoId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `disorder` (
  `disorderId` int(11) NOT NULL AUTO_INCREMENT,
  `domain` varchar(255) DEFAULT NULL,
  `relevantDisorder` varchar(255) DEFAULT NULL,
  `subdomain` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`disorderId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `genelist` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `directionChange` varchar(255) DEFAULT NULL,
  `genecardSymbol` varchar(255) DEFAULT NULL,
  `psychiatricDomain` varchar(50) DEFAULT NULL,
  `pubMedID` double NOT NULL,
  `relevantDisorder` varchar(50) DEFAULT NULL,
  `subDomain` varchar(50) DEFAULT NULL,
  `tissue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `hubraingex` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `directionChange` varchar(255) DEFAULT NULL,
  `genecardSymbol` varchar(255) DEFAULT NULL,
  `psychiatricDomain` varchar(50) DEFAULT NULL,
  `pubMedID` double NOT NULL,
  `relevantDisorder` varchar(50) DEFAULT NULL,
  `subDomain` varchar(50) DEFAULT NULL,
  `tissue` varchar(255) DEFAULT NULL,
  `CChange` varchar(255) DEFAULT NULL,
  `accessionNumber` varchar(255) DEFAULT NULL,
  `addedBy` varchar(255) DEFAULT NULL,
  `additionalInfo` varchar(255) DEFAULT NULL,
  `authorId` varchar(255) DEFAULT NULL,
  `brainRegion` varchar(255) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `dateAdded` datetime DEFAULT NULL,
  `descriptiveName` varchar(255) DEFAULT NULL,
  `females` varchar(255) DEFAULT NULL,
  `flaggedStudies` varchar(255) DEFAULT NULL,
  `geneName` varchar(255) DEFAULT NULL,
  `males` varchar(255) DEFAULT NULL,
  `numberOfSubjects` varchar(255) DEFAULT NULL,
  `paperSymbol` varchar(255) DEFAULT NULL,
  `sampleSource` varchar(255) DEFAULT NULL,
  `sourceInfo` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `discovery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `probeset` varchar(255) DEFAULT NULL,
  `genecardSymbol` varchar(255) DEFAULT NULL,
  `geneTitle` varchar(255) DEFAULT NULL,
  `changeInExpressionInTrackedPhene` varchar(255) DEFAULT NULL,
  `apScores` double DEFAULT NULL,
  `apPercentile` double DEFAULT NULL,
  `apScore` int DEFAULT NULL,
  `apChange` varchar(255) DEFAULT NULL,
  `deScores` double DEFAULT NULL,
  `dePercentile` double DEFAULT NULL,
  `deScore` int DEFAULT NULL,
  `deChange` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



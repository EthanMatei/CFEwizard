
CREATE TABLE `databaseuploadinfo` (
  `databaseUpdloadInfoId` int(11) NOT NULL AUTO_INCREMENT,
  `databaseName` varchar(255) DEFAULT NULL,
  `uploadFileName` varchar(255) DEFAULT NULL,
  `uploadTime` datetime DEFAULT NULL,
  PRIMARY KEY (`databaseUpdloadInfoId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `discovery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `probeset` varchar(255) DEFAULT NULL,
  `geneCardsSymbol` varchar(255) DEFAULT NULL,
  `geneTitle` varchar(255) DEFAULT NULL,
  `changeInExpressionInTrackedPhene` varchar(255) DEFAULT NULL,
  `apScores` double DEFAULT NULL,
  `apPercentile` double DEFAULT NULL,
  `apScore` double DEFAULT NULL,
  `apChange` varchar(255) DEFAULT NULL,
  `deScores` double DEFAULT NULL,
  `dePercentile` double DEFAULT NULL,
  `deScore` double DEFAULT NULL,
  `deChange` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `prioritization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `probeset` varchar(255) DEFAULT NULL,
  `geneCardsSymbol` varchar(255) DEFAULT NULL,
  `geneTitle` varchar(255) DEFAULT NULL,
  `changeInExpressionInTrackedPhene` varchar(255) DEFAULT NULL,

  `huBrainScore` double DEFAULT NULL,
  `huBrainInfo`  varchar(255) DEFAULT NULL,
  `huPerScore`   double DEFAULT NULL,
  `huPerInfo`    varchar(255) DEFAULT NULL,
  `huGeneAssocScore`  double DEFAULT NULL,
  `huGeneAssocInfo`   varchar(255) DEFAULT NULL,
  `huGCnvScore`       double DEFAULT NULL,
  `huGCnvInfo`        varchar(255) DEFAULT NULL,
  `nhBrainScore`  double DEFAULT NULL,
  `nhBrainInfo`   varchar(255) DEFAULT NULL,
  `nhPerScore`    double DEFAULT NULL,
  `nhPerInfo`     varchar(255) DEFAULT NULL,
  `nhGeneAssocScore` double DEFAULT NULL,
  `nhGeneAssocInfo`  varchar(255) DEFAULT NULL,
  `nhGCnvScore` double DEFAULT NULL,
  `nhGCnvInfo`  varchar(255) DEFAULT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

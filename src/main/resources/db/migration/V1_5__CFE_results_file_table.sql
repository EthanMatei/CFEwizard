create table CfeResultsFile (
    `cfeResultsFileId` bigint(20) NOT NULL AUTO_INCREMENT,
    `fileType` varchar(255) NOT NULL,
    `mimeType` varchar(255),
    `creationTime` datetime NOT NULL,
    `content` mediumblob,
    `cfeResultsId` bigint(20) NOT NULL,
    INDEX `cfeResultsIndex`(`cfeResultsId`),
    CONSTRAINT `resultsAndNameUniqueConstraint` UNIQUE(`cfeResultsId`, `fileType`),
    FOREIGN KEY (`cfeResultsId`) REFERENCES `CfeResults`(`cfeResultsId`)
      ON UPDATE CASCADE
      ON DELETE CASCADE,
    PRIMARY KEY (`cfeResultsFileId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

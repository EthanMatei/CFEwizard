This directory contains the R scripts that are used in the pipeline.

R 3.5.3 is what was used to run the stand alone R scripts that are run on Windows.
We need to run with R 4.

The Windows versions of the Discovery R scripts used the RODBC package for accessing MS Access, but
this package is not available on Linux.  So, the JDBC driver was used, which needs .jar (Java ARchive) files to
access MS Access databases. However, the code was since rewritten so that the web app creates a
CSV file with the needed data for the Discovery R scripts, so the Discovery R scripts no longer access
MS Access directly, and the .jar files are no longer needed.

#-------------------------------------------------------------------
# This script generated html (compatible with Confluence) that
# describe the MS Acess database schemas for the CFE Wizard.
# The output can be copied and pasted into Conflunce (in HTML mode).
# 
# Note: This script ONLY RUNS ON WINDOWS. And MS Acess, and the
#       database driver for it that is used below, need to be
#       installed.
#--------------------------------------------------------------------
use strict;
use DBI;
use POSIX;

$| = 1;


my $databaseDir = '..\test-data\ms-access\\';

my @databases = ('HUBRAIN (NJ 10-30-2013).accdb', 
                 'HUGEN (NJ 11-1-2013).accdb', 
                 'HUPER (NJ 11-1-2013).accdb',
                 'NHBRAIN (NJ 11-1-2013).accdb',
                 'NHGEN (NJ 11-1-2013).accdb',
                 'NHPER (NJ 10-30-2013).accdb');


#-------------------------------------------
# Print the header
#-------------------------------------------
print "<p><strong>Schemas generated: ".strftime("%Y-%m-%d %H:%M:%S", localtime)."</strong></p>\n";


foreach (@databases) {
	my $database = $_;
	
	#print "${databaseDir}${database}\n";

    my $databaseName = $database;
    $databaseName =~ s/ \(.*//ig;    
    print "\n\n<hr />\n";
    print "<h2>DATABASE: $databaseName</h2>\n";
    
        
    #----------------------------------------------------------------------------------------------------------------
    # Note: The driver referenced below must be installed, and it looks like spacing for driver name is significant.
    #
    # To check for the driver being installed on Windows 7:
    # 1) Select "Start -> Control Panel""
    # 2) Select "System and Security"
    # 3) Select "Aministrative Tools"
    # 4) Select "Data Sources (ODBC)"
    # 5) Select the "Drivers" tab
    #----------------------------------------------------------------------------------------------------------------
    my $dbh = DBI->connect("dbi:ODBC:driver=Microsoft Access Driver (*.mdb, *.accdb);dbq=${databaseDir}${database}", undef, undef, {PrintError => 1, RaiseError => 1});

    my $sth = $dbh->table_info( '', '', '', 'TABLE' );
    while ( my ( undef, undef, $name ) = $sth->fetchrow_array() ) {
    	
    	my $tableName = $name;
    	$tableName =~ s/ .*//ig;
        print "<h3>TABLE: $tableName</h3>\n";
        print "<table>\n";
        print "<tbody>\n";
        
        print "<tr>\n";
        print "<th>Name</th> <th>Type</th> <th>Size</th> <th>Nullable?</th> <th>Default Value</th> <th>Comments</th>\n";
        print "</tr>\n";
        
        my $colsth = $dbh->column_info( '', '', $name, '' );
        while ( my ($tableCatalog, $tableSchema, $tableName, $columnName, $dataType, $typeName, $columnSize,
                    $bufferLength, $decimalDigits, $numericPrecisionRadix, $nullable, $remarks, $columnDefault ) = $colsth->fetchrow_array() ) {
            my $isNullable = 'no';
            if ($nullable == 1) {
            	$isNullable = 'yes';
            }
            elsif ($nullable == 2) {
            	$isNullable = 'unknown';
            }
        	print "<tr>\n";
            print "<td>$columnName</td> <td>$typeName</td> <td>$columnSize</td> <td>$isNullable</td> "
                  ." <td>$columnDefault</td> <td>$remarks</td> \n";
            print "</tr>\n";
        }
        
        print "</tbody>\n";
        print "</table>\n";

    }

    $dbh->disconnect();
}

#!/usr/bin/perl

#---------------------------------------------------------------
# mysql2twiki.pl - convert a MySQL database schema into
#                  TWiki/Foswiki format
#
#                  This program assumes that a .my.cnf
#                  file with username and password has
#                  been set up
#---------------------------------------------------------------
use strict;

my $dbUser;
my $dbName;
my $dbWikiTitle;


$dbUser      = shift @ARGV or &usage();
$dbName      = shift @ARGV or &usage();
$dbWikiTitle = shift @ARGV or &usage();
&usage() if ($#ARGV >= 0);   # too many arguments

my $mysql = "mysql -u ${dbUser} -p ";

my @tables = ();

my $command = 
    "${mysql} --disable-column-names << ENDSQL\n"
    ." use ${dbName};\n"
    ." show tables;\n"
    ."ENDSQL\n";


#print "$command\n";

open INPUT, "$command |";

while (<INPUT>) {
	my $table = $_;
	chomp($table);
	push(@tables, $table);
}
close INPUT;


print "<h2>${dbWikiTitle} Database Schema</h2>\n\n";

print "<h3>Schema generated: ".`date`."</h3>\n\n";


foreach (@tables) {
	
	my $table = $_;
	chomp($table);
	
	print "<h3>$table</h3>\n";
    
    print "<table>\n";
    print "<tbody>\n";

    my $command2 = "${mysql} << ENDSQL\n"
                   ." use ${dbName};\n"
                   ." desc $table;\n"
                   ."ENDSQL\n";
    open INPUT2, "$command2 |";
    	
	my $lineCount = 0;
    while (<INPUT2>) {
    	$lineCount++;
        my $line = $_;
        chomp( $line );
        my @values = split("\t", $line);
 
        print "<tr>";
        if ($lineCount == 1) {
            print "<th>".$values[0]."</th>";
            print "<th>".$values[1]."</th>";
            print "<th>".$values[2]."</th>";
            print "<th>".$values[3]."</th>";        	
        }
        else {
            print "<td>".$values[0]."</td>";
            print "<td>".$values[1]."</td>";
            print "<td>".$values[2]."</td>";
            print "<td>".$values[3]."</td>";
        }
        print "</tr>\n";
        print "<hr />\n";

    }
    close INPUT2;
    
    print "</tbody>\n";
    print "</table>\n";
}

exit(0);

foreach (@tables) {

	
	my $table = $_;
	chomp($table);
    my $command2 = "${mysql} << ENDSQL\n"
                   ." use ${dbName};\n"
                   ." desc $table;\n"
                   ."ENDSQL\n";
    open INPUT2, "$command2 |";
    
    print "<t3>$table</t3>\n";
    
    print "<table border=\"1\" cellpadding=\"3\">\n";
    print "<tbody>\n";
    print "<tr> <th> Attributes </th> <th> Create Statement </th> </tr>\n";
    print "<tr valign=\"top\">\n";
	print "<td>\n";
	
    my $lineCount = 0;
    while (<INPUT2>) {
    	$lineCount++;
        my $line = $_;
        chomp( $line );
        my @values = split("\t", $line);
        if ($lineCount == 1) {
            print "| *".$values[0]
                ."* | *".$values[1]
                ."* | *".$values[2]
                ."* | *".$values[3]
                ."* | "."\n";        	
        }
        else {
            print "| !".$values[0]
                ." | ".$values[1]
                ." | ".$values[2]
                ." | ".$values[3]
                ." | "."\n";
        }
    }
    close INPUT2;
    
    print "</td>\n";
    
    my $command3 = "${mysql} --disable-column-names << ENDSQL\n"
                   ." use ${dbName};\n"
                   ." show create table $table;\n"
                   ."ENDSQL\n";
    open INPUT3, "$command3 |";
    
    print "<td><pre>";
    while (<INPUT3>) {
        my $line = $_;
        chomp($line);
        $line =~ s/\\n/\n/g; # turn escaped newlines into actual newlines
        $line -~ s/\n$//;    # get rid of the last newline
        $line =~ s/^.*CREATE TABLE/CREATE TABLE/;
        #chomp($line);
        print $line;
    }    
    print "</pre></td>\n";

    print "</tr>\n";
    print "</tbody>\n";
    print "</table>\n";
    close INPUT3;
    
    
}

sub usage() {
    print "\n";
    print "usage: mysql2twiki.pl <db_user> <db_name> <db_wiki_title>\n";
    print "\n";
    print "    example: mysql2twiki.pl cfg \"CFG Wizard\"\n\n";
    exit(1);
}


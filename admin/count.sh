# Script for counting the lines of code in the CFE Wizard
echo "CFE Wizard - count of lines of code"
cloc --quiet --hide-rate --exclude-ext=md,html \
    ../src/main/java/cfe \
    ../src/main/resources/R \
    ../src/main/resources/python \
    ../src/main/webapp/pages


echo
echo
echo "CFE Wizard - count of lines of test code"
cloc --quiet --hide-rate --exclude-ext=md \
    ../src/test/java/cfe


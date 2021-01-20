CFE/CFG Wizard Development Environment Setup
================================================

Java
---------------------------------------

You need to install Java 1.8. On Ubuntu use:

```shell
sudo apt install openjdk-8-jdk openjdk-8-jre
sudo cat >> /etc/environment << EOF
JAVA_HOME= /usr/lib/jvm/java-8-openjdk-amd64
JRE_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
EOF
```

MySQL
-----------------------------------------

MySQL is the database system used by the CFE and CFG Wizards for stogin data.

To install MySQL on Ubuntu, use:

```shell
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```


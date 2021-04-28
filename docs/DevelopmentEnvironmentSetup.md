CFE/CFG Wizard Development Environment Setup
================================================

Development has been done using Ubuntu Linux virtual machines. We use [Oracle's VirtualBox](https://www.virtualbox.org/)
to run virtual machines on our PCs.

You need to download and install Oracle's VirtualBox software: [https://www.virtualbox.org/](https://www.virtualbox.org/)

Then, you need to download [Ubuntu 18](http://releases.ubuntu.com/18.04/), the
current version of Ubuntu we are using, and create a VM 
in VirutalBox using the Ubuntu 18 download file.

Unless otherwise stated, the setup instructions in this document are for Ubuntu.


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

Maven (build and dependency management tool)
---------------------------------------------
sudo apt install maven


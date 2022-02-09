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

You need to install Java 1.8. Java is the primary programming langauge used by the CFE and CFG Wizards.
On Ubuntu use:

```shell
sudo apt install openjdk-8-jdk openjdk-8-jre
sudo cat >> /etc/environment << EOF
JAVA_HOME= /usr/lib/jvm/java-8-openjdk-amd64
JRE_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
EOF
```

MySQL
-----------------------------------------

MySQL is the database system used by the CFE and CFG Wizards for storing data.

To install MySQL on Ubuntu, use:

```shell
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

Maven
---------------------------------------------

Maven is the tool used for building the Wizards and managing their dependencies.

```shell
sudo apt install maven
```

R
-------------------------------------------------------------------

The web app executes R scripts, so R (version 4) needs to be installed.

Several different R packages that are used by the scripts need to be installed.
These packages can be installed as in the example shown below:

    sudo R

    # For the DEdiscovery script:
    install.packages("plyr")
    install.packages("gtools")
    install.packages("dplyr")

    # For the Validation script:
    install.packages("pROC")
    install.packages("ROCR")
    install.packages("verification")

Python
----------------------------------------------------------

The web app executes Python scripts, so Python 3 needs to be available, and is by default on most Linux installations. 

Package information for what was used originally:

* pandas: 1.3.1
* numpy: 1.20.3


How to install the needed packages on Ubuntu:

    sudo apt install python3-pip
    pip install pandas==1.3.1


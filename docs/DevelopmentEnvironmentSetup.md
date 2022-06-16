CFE/CFG Wizard Development Environment Setup
================================================

Development has been done using Ubuntu Linux virtual machines. We use [Oracle's VirtualBox](https://www.virtualbox.org/)
to run virtual machines on our PCs.

You need to download and install Oracle's VirtualBox software: [https://www.virtualbox.org/](https://www.virtualbox.org/)

Then, you need to download [Ubuntu 20](https://releases.ubuntu.com/20.04/), the
current version of Ubuntu we are using, and create a VM 
in VirutalBox using the Ubuntu download file.

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
    
    # For Prediction script
    install.packages("coin")
    install.packages("data.table")
    install.packages("readr")

Python
----------------------------------------------------------

The web app executes Python scripts, so Python 3 needs to be available, and is by default on most Linux installations. 

Package information for what was used originally:

* pandas: 1.3.1
* numpy: 1.20.3


How to install the needed packages on Ubuntu:

    sudo apt install python3-pip
    pip install pandas==1.3.1

Tomcat (Web Application Server)
---------------------------------------------------------------

Tomcat is the web application server that has been used. In theory, it should be possible
to run the CFE Wizard on any web application server that supports Java web applications.

To install Tomcat 9 on Ubuntu 20, use the following command:

    sudo apt install tomcat9

This will create directories place Tomcat 9 in the following directory:

    /var/lib/tomcat9
    
**Increasing Memory**

Increasing the memory in Tomcat can be done by creating/modifying the following file:

    /var/lib/tomcat9/bin/setenv.sh
    
In this file, add the following, with numbers modified appropriately (the example below sets the
memory size to 3GB):

    export CATALINA_OPTS="-Xms3072M -Xmx3072M"

You need to restart Tomcat after this option has been changed, which in Ubuntu 20,
can be done with the following command:

    sudo systemctl restart tomcat9

You can verify that the amount of memory was increased by checking the system status page in the
CFE Wizard.

**Increasing the Number of Request/Post Parameters**

Another limit issue that can occur is that the number of allowed request/post parameters is
exceeded. This issue can be fixed by modifying Tomcat's **server.xml** file, which with the
distribution version of Tomcat 9 on Ubuntu 20, will be at the following location:

    /var/lib/tomcat9/conf/server.xml

You need to add the **maxParameterCount** parameter in the section shown below:

    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
           redirectPort="8443"
           maxParameterCount="100000"
           />



  
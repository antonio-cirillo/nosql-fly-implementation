echo "Checking that git is installed"
which git 
if [ $? -eq 0 ]; then
  echo "git installed, continuing..."
else
  echo "sudo apt-get install git"
  sudo apt-get install git
fi

echo "Checking java is installed"
which java
if [ $? -eq 0 ]; then
  echo "java installed, continuing..."
else
  echo "sudo apt-get install openjdk-11-jdk"
  sudo apt-get install openjdk-11-jdk
fi

echo "Checking that nodejs is installed"
which nodejs
if [ $? -eq 0 ]; then
  echo "nodejs installed, continuing..."
else
  echo "sudo apt-get install nodejs"
  sudo apt-get install nodejs
fi

echo "Checking that npm is installed"
which npm
if [ $? -eq 0 ]; then
  echo "npm installed, continuing..."
else
  echo "sudo apt-get install npm"
  sudo apt-get install npm
fi

echo "Checking that python3 is installed"
which python3
if [ $? -eq 0 ]; then
  echo "python3 installed, continuing..."
else
  echo "sudo apt-get install python3"
  sudo apt-get install python3
fi

echo "Checking that maven is installed"
which mvn
if [ $? -eq 0 ]; then
  echo "maven installed, continuing..."
else
  echo "sudo apt-get install maven"
  sudo apt-get install maven
fi

echo "Checking that curl is installed"
which aws
if [ $? -eq 0 ]; then
  echo "curl is installed, continuing..."
else
  echo "sudo apt-get install curl"
  sudo apt-get install curl
fi

echo "Checking that aws-cli is installed"
which aws
if [ $? -eq 0 ]; then
  echo "aws-cli is installed, continuing..."
else
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  unzip awscliv2.zip
  sudo ./aws/install
  rm -rf aws
  rm awscliv2.zip
fi

echo "Checking that docker is installed"
which docker
if [ $? -eq 0 ]; then
  echo "docker is installed, continuing..."
else
  sudo apt-get update
  sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release
  curl -fsSL https://get.docker.com -o get-docker.sh
  sudo sh get-docker.sh
  sudo groupadd docker
  sudo usermod -aG docker $USER
  newgrp docker
  rm get-docker.sh
fi

echo "Checking that docker-compose is installed"
which docker-compose
if [ $? -eq 0 ]; then
  echo "docker-compose is installed, continuing..."
else
  sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
fi

echo "Checking that azure-cli is installed"
which az
if [ $? -eq 0 ]; then
  echo "azure-cli is installed, continuing..."
else
  sudo apt-get update
  sudo apt-get install ca-certificates curl apt-transport-https lsb-release gnupg -y
  curl -sL https://packages.microsoft.com/keys/microsoft.asc |
  gpg --dearmor |
  sudo tee /etc/apt/trusted.gpg.d/microsoft.gpg > /dev/null
  echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ $(lsb_release -cs) main" |
  sudo tee /etc/apt/sources.list.d/azure-cli.list
  sudo apt-get update
  sudo apt-get install azure-cli -y
fi

echo "Checking that Azure Function Core Tools is installed"
which func
if [ $? -eq 0 ]; then
  echo "Azure Function Core Tools is installed, continuing..."
else
  wget -q https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb
  sudo dpkg -i packages-microsoft-prod.deb
  sudo apt-get update
  sudo apt-get install azure-functions-core-tools-3
  rm packages-microsoft-prod.deb 
fi

echo "Checking wheter python3-pip is installed"
which virtualenv
if [ $? -eq 0 ]; then
  echo "python3-pip is installed, continuing..."
else
  echo "sudo apt-get install python3-pip"
  sudo apt-get install python3-pip
fi

echo "Checking wheter virtualenv is installed"
which virtualenv
if [ $? -eq 0 ]; then
  echo "virtualenv is installed, continuing..."
else
  sudo pip3 install virtualenv
fi

clear
echo "Install maven archetype of FLY Project"
cd fly-project-quickstart
cd fly-project-quickstart
mvn install
mvn archetype:crawl

clear
echo "Install Eclipse DLS"
cd $HOME
clear
wget https://rhlx01.hs-esslingen.de/pub/Mirrors/eclipse/technology/epp/downloads/release/2020-12/R/eclipse-dsl-2020-12-R-linux-gtk-x86_64.tar.gz
sudo tar xvzf eclipse-dsl-2020-12-R-linux-gtk-x86_64.tar.gz
rm eclipse-dsl-2020-12-R-linux-gtk-x86_64.tar.gz
mkdir runtime-EclipseApplication
mkdir runtime-EclipseApplication/.metadata
mkdir runtime-EclipseApplication/.metadata/.plugins
mkdir runtime-EclipseApplication/.metadata/.plugins/org.eclipse.m2e.core
echo "Eclipse DLS was install"

clear
echo "Adding Archetype Catalogs on Eclipse DLS"
echo '<?xml version="1.0" encoding="UTF-8"?><archetypeCatalogs>
<archetypeCatalogs>
<catalog type="system" id="nexusIndexer" enabled="true"/>
<catalog type="system" id="internal" enabled="true"/>
<catalog type="system" id="defaultLocal" enabled="true"/>
<catalog type="system" id="https://repo1.maven.org/maven2/archetype-catalog.xml" enabled="true"/>
<catalog type="local" location="$HOME/.m2/repository/archetype-catalog.xml" description="Local $HOME/.m2/repository/archetype-catalog.xml" enabled="true"/>
</archetypeCatalogs>' > $HOME/runtime-EclipseApplication/.metadata/.plugins/org.eclipse.m2e.core/archetypesInfo.xml

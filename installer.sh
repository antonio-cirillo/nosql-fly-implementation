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

clear
cd fly-project-quickstart
cd fly-project-quickstart
mvn install
mvn archetype:crawl

clear
cd $HOME
clear
wget https://rhlx01.hs-esslingen.de/pub/Mirrors/eclipse/technology/epp/downloads/release/2020-12/R/eclipse-dsl-2020-12-R-linux-gtk-x86_64.tar.gz
sudo tar xvzf eclipse-dsl-2020-12-R-linux-gtk-x86_64.tar.gz
rm eclipse-dsl-2020-12-R-linux-gtk-x86_64.tar.gz
mkdir runtime-EclipseApplication
mkdir runtime-EclipseApplication/.metadata
mkdir runtime-EclipseApplication/.metadata/.plugins
mkdir runtime-EclipseApplication/.metadata/.plugins/org.eclipse.m2e.core

echo '<?xml version="1.0" encoding="UTF-8"?><archetypeCatalogs>
<archetypeCatalogs>
<catalog type="system" id="nexusIndexer" enabled="true"/>
<catalog type="system" id="internal" enabled="true"/>
<catalog type="system" id="defaultLocal" enabled="true"/>
<catalog type="system" id="https://repo1.maven.org/maven2/archetype-catalog.xml" enabled="true"/>
<catalog type="local" location="$HOME/.m2/repository/archetype-catalog.xml" description="Local $HOME/.m2/repository/archetype-catalog.xml" enabled="true"/>
</archetypeCatalogs>' > $HOME/runtime-EclipseApplication/.metadata/.plugins/org.eclipse.m2e.core/archetypeInfo.xml

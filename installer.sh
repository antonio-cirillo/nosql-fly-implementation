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
if [ ? -eq 0 ]; then
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

echo "Git clone della repository"
git clone https://github.com/fly-language/fly-language.git

echo "Installazione archetype"
cd fly-language
cd fly-project-quickstart
cd fly-project-quickstart
mvn install
mvn archetype:crawl
clear
echo "Fine installazione"

echo "Edit Maven Archetype catalogs..."
cd $HOME/runtime-EclipseApplication/.metadata/.plugins/org.eclipse.m2e.core
if [ -n "$(xmlstarlet sel -T -t -v "archetypeCatalogs/catalog[@location='$HOME/.m2/repository/archetype-catalog.xml']/@type" archetypesInfo.xml)" ]; then

  echo "Archetype già presente!"
  if [ "true" = "$(xmlstarlet sel -T -t -v "archetypeCatalogs/catalog[@location='$HOME/.m2/repository/archetype-catalog.xml']/@enabled" archetypesInfo.xml)" ]; then
    echo "L'archetype già abilitato"
  else
    xmlstarlet ed --inplace -s "/arcgetypeCatalogs" \
    -t elem -n catalog -v "" \
    -u "/archetypeCatalogs/catalog[@location='$HOME/.m2/repository/archetype-catalog.xml']/@enabled" \
    -v true \
    archetypesInfo.xml
    echo "L'archetype è stato abilitato"
  fi

else
  xmlstarlet ed --inplace -s "/archetypeCatalogs" \
  -t elem -n catalog -v "" \
  -i "/archetypeCatalogs/catalog[last()]" -t attr -n type -v local \
  -i "/archetypeCatalogs/catalog[last()]" -t attr -n location -v $HOME/.m2/repository/archetype-catalog.xml \
  -i "/archetypeCatalogs/catalog[last()]" -t attr -n description -v "Local $HOME/.m2/repository/archetype-catalog.xml" \
  -i "/archetypeCatalogs/catalog[last()]" -t attr -n enabled -v true \
  archetypesInfo.xml
  echo "Archetype aggiunto!"

fi

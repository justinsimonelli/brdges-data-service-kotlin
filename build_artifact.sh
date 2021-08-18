#!/usr/bin/env bash
deploy_to_aws() {
  echo "#### Starting Build ####"
  mvn clean package
  cp target/brdges-data-service-*.jar brdges-data-service-kotlin.jar
  zip deploy.zip -r Procfile brdges-data-service-kotlin.jar .ebextensions .elasticbeanstalk .platform
  rm -rf brdges-data-service-kotlin.jar
  eb deploy
  rm -rf deploy.zip
}

echo_commit_message() {
  echo "#### Commit the changes before trying to deploy them ####"
}

echo "Did you commit the changes??"
select yn in "Yes" "No"; do
    case $yn in
        Yes ) deploy_to_aws; exit;;
        No ) echo_commit_message; exit;;
    esac
done
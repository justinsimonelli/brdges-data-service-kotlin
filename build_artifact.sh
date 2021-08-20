#!/usr/bin/env bash

build_artifacts() {
    echo "#### Starting Build ####"
    mvn clean package
    package_files
    echo "Do you want to deploy to EB?"
    select yn in "Yes" "No"; do
        case $yn in
            Yes ) deploy_to_aws; exit;;
            No ) echo_commit_message; exit;;
        esac
    done
}

package_files() {
  echo "Packaging files"
  rm -f deploy.zip
  cp target/brdges-data-service-*.jar brdges-data-service-kotlin.jar
  zip deploy.zip -r Procfile brdges-data-service-kotlin.jar .elasticbeanstalk .ebextensions .platform
  rm -rf brdges-data-service-kotlin.jar
}

deploy_to_aws() {
  echo "#### Starting Deploy to AWS - brdges-data-service ####"
  eb deploy brdges-data-service
  rm -rf deploy.zip
}

echo_commit_message() {
  echo "#### Commit the changes before trying to deploy them ####"
}

echo "Build the application?"
select yn in "Yes" "No"; do
    case $yn in
        Yes ) build_artifacts; exit;;
        No ) package_files; exit;;
    esac
done
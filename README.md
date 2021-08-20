# brdges-data-service-kotlin

## Description
This service provides the bridge data for the iOS app Brdges. It will periodically update the data based on configuration and cache the data so clients don't constantly hit downstream endpoints.

# Build
`mvn clean install`
OR  
`sh build_artifact.sh -l 'ssl-1.4' -d 'ssl-1.4'`  
- `-l` version label
- `-d` description

# Spoofing
Responses can be forced/spoofed by adding the following query parameters:  
`?force=true&spoofName=<spoof_fileName>`  
Available spoofs:  
- `bridges_up`
- `bridges_down`

# Notes
## EXTREMELY IMPORTANT ##
Make sure it's a SINGLE INSTANCE type for the app!!!
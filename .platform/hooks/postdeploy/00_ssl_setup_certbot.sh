#!/usr/bin/env bash
# Postdeploy script for enabling SSL (single instance)
# Compatible only with Amazon Linux 2 EC2 instances

LOG_PATH=$(find /var/log/ -type f -iname 'eb-hooks.log')
DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

log() {
    if [ -n "$LOG_PATH" ] && [ -n "$DATE" ]; then
        echo "$DATE | $1" | tee -a "$LOG_PATH"
    fi
}

if openssl x509 -checkend 86400 -noout -in /etc/letsencrypt/live/ebcert/fullchain.pem
then
  log 'Certificate is good for another day!'
else
  log 'Certificate has expired or will do so within 24 hours!'
  certbot --nginx --debug --redirect --non-interactive --cert-name ebcert --email ${CERTBOT_EMAIL} --agree-tos --domains ${CERTBOT_DOMAIN} --keep-until-expiring
fi
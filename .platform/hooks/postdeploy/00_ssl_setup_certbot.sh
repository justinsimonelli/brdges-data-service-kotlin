#!/usr/bin/env bash
# Postdeploy script for enabling SSL (single instance)
# Compatible only with Amazon Linux 2 EC2 instances

LOG_PATH=$(find /var/log/ -type f -iname 'eb-hooks.log')
DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ")


#env vars come from config file

crontab_exists() {
    crontab -u root -l 2>/dev/null | grep 'certbot -q renew' >/dev/null 2>/dev/null
}

log_and_exit() {
    if [ -n "$LOG_PATH" ] && [ -n "$DATE" ]; then
        echo "$DATE | $1" | tee -a "$LOG_PATH"
    fi
    exit 0
}

echo "Updating certs for ${CERTBOT_DOMAINS}"

# Variable check

if [ ! -n "$CERTBOT_NAME" ] || [ ! -n "$CERTBOT_EMAIL" ] || [ ! -n "$CERTBOT_DOMAINS" ]; then
    log_and_exit 'INFO: Certbot and/or proxy server information is missing.'
fi

# Auto allow yes for all yum install
# SUGGESTION: Remove after deployment

if ! grep -q 'assumeyes=1' /etc/yum.conf; then
    echo 'assumeyes=1' | tee -a /etc/yum.conf
fi


HTTP_STRING='^http\s*{$'
NAME_LIMIT='http {\nserver_names_hash_bucket_size 192;\n'

# Prevent replace if not clean sample app

if ! grep -Fxq "$NAME_LIMIT" /etc/nginx/nginx.conf; then
    # Increase size of string name for --domains (for default EB configs)

    if ! sed -i "s/$HTTP_STRING/$NAME_LIMIT/g" /etc/nginx/nginx.conf; then
        log_and_exit 'ERROR: Changing server name limit failed'
    fi
fi

# Set up certificates

if command -v certbot &>/dev/null; then
    if nginx -t; then
        certbot --nginx --cert-name "$CERTBOT_NAME" -m "$CERTBOT_EMAIL" --domains "$CERTBOT_DOMAINS" --redirect --agree-tos --no-eff-email --keep-until-expiring
    else
        log_and_exit 'ERROR: Nginx configuration is invalid.'
    fi
else
    log_and_exit 'ERROR: Certbot installation may have failed.'
fi

# Create cron task (attempt) to renew certificate every 29 days

if ! crontab_exists; then
    systemctl start crond
    systemctl enable crond

    LINE="0 0 */29 * * certbot -q renew; systemctl reload nginx; systemctl reload nginx"

    (
        crontab -u root -l
        echo "$LINE"
    ) | crontab -u root -
fi

log_and_exit 'SUCCESS: Script ran successfully.'

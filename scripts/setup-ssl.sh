#!/bin/bash
set -e

DOMAIN="cyfl.yatat"
DOMAINS="-d $DOMAIN -d www.$DOMAIN"
EMAIL="admin@$DOMAIN"

echo "============================================"
echo "  Configuración SSL para $DOMAIN"
echo "============================================"

# 1. Respaldo de la config HTTPS y copia de la HTTP bootstrap
echo ""
echo "[1/4] Preparando config temporal HTTP..."
cp nginx/nginx.conf nginx/nginx.conf.bak
cp nginx/nginx.conf.http nginx/nginx.conf

# 2. Iniciar servicios (nginx solo HTTP)
echo ""
echo "[2/4] Iniciando nginx para validación ACME..."
docker compose up -d nginx
sleep 5

# 3. Solicitar certificados Let's Encrypt
echo ""
echo "[3/4] Solicitando certificados Let's Encrypt..."
docker compose run --rm certbot certonly --webroot -w /var/www/html $DOMAINS \
    --email $EMAIL --agree-tos --no-eff-email --non-interactive

# 4. Restaurar config HTTPS y recargar nginx
echo ""
echo "[4/4] Restaurando config HTTPS y recargando nginx..."
cp nginx/nginx.conf.bak nginx/nginx.conf
rm nginx/nginx.conf.bak
docker compose exec nginx nginx -s reload

echo ""
echo "============================================"
echo "  SSL configurado correctamente ✅"
echo "============================================"
echo "  Dominio: https://$DOMAIN"
echo "  Renovación automática cada 12h"
echo ""

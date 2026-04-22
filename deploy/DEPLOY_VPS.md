# Deploy to VPS `79.143.188.153`

This project uses Spring Boot, Maven, MySQL, and server-side session cookies.

## 1. Install packages on the VPS

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk nginx mysql-server git
java -version
```

Use Java 21. The Maven project declares `java.version=21`.

## 2. Copy the project to the VPS

If the repository already exists on GitHub/GitLab:

```bash
cd /home/ubuntu
git clone <your-repo-url> med_office
cd med_office
```

If not, upload the project folder to `/home/ubuntu/med_office`.

## 3. Create the database

```bash
sudo mysql
```

```sql
CREATE DATABASE med_office CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'med_office'@'localhost' IDENTIFIED BY 'change_me';
GRANT ALL PRIVILEGES ON med_office.* TO 'med_office'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

Import the schema:

```bash
mysql -u med_office -p med_office < src/main/resources/schema-mysql.sql
```

## 4. Create the environment file

```bash
cp deploy/.env.production.example .env
nano .env
```

Set at least:

- `MED_OFFICE_DB_PASSWORD`
- `AI_API_KEY` if you use the AI endpoint
- `CORS_ALLOWED_ORIGINS` with the frontend URL, for example `http://192.168.54.100:5173`

Do not set `CORS_ALLOWED_ORIGINS` to the backend URL such as `http://79.143.188.153`.
The browser sends the frontend origin in the `Origin` header, so the backend must allow the frontend host and port.

If your frontend host changes inside the same LAN, you can use patterns instead:

```properties
CORS_ALLOWED_ORIGIN_PATTERNS=http://192.168.*:5173,http://10.*:5173
```

For raw IP deployment without HTTPS, keep:

```properties
SESSION_COOKIE_SECURE=false
SESSION_COOKIE_SAME_SITE=lax
```

If you later move to a real domain with HTTPS, switch to:

```properties
SESSION_COOKIE_SECURE=true
SESSION_COOKIE_SAME_SITE=none
```

## 5. Build the application

```bash
chmod +x mvnw
./mvnw clean package -DskipTests
```

The jar will be created at:

```text
target/med_office-0.0.1-SNAPSHOT.jar
```

## 6. Create the systemd service

```bash
sudo cp deploy/med-office.service /etc/systemd/system/med-office.service
sudo systemctl daemon-reload
sudo systemctl enable med-office
sudo systemctl start med-office
sudo systemctl status med-office
```

If your VPS user is not `ubuntu`, edit `/etc/systemd/system/med-office.service` and replace:

- `/home/ubuntu/med_office`
- `User=ubuntu`

## 7. Configure Nginx

```bash
sudo cp deploy/nginx-ip.conf /etc/nginx/sites-available/med-office
sudo ln -s /etc/nginx/sites-available/med-office /etc/nginx/sites-enabled/med-office
sudo nginx -t
sudo systemctl reload nginx
```

If the default site is still active, disable it:

```bash
sudo rm -f /etc/nginx/sites-enabled/default
sudo systemctl reload nginx
```

If you also deploy the frontend to the same VPS, use `deploy/nginx-fullstack-ip.conf` instead of `deploy/nginx-ip.conf`.
That config serves the frontend from `/var/www/fe-med-office` and proxies `/api/` to Spring Boot on `127.0.0.1:8080`.

## 8. Open the firewall

If UFW is enabled:

```bash
sudo ufw allow 80/tcp
sudo ufw allow 22/tcp
sudo ufw status
```

## 9. Check the app

From your browser:

```text
http://79.143.188.153/swagger-ui.html
```

Swagger is disabled by default in production. To enable it temporarily:

```properties
SPRINGDOC_ENABLED=true
```

Then restart:

```bash
sudo systemctl restart med-office
```

## 9a. Deploy the frontend on the same VPS

Build the frontend on your local machine:

```bash
cd /path/to/fe-med-office
npm install
npm run build
```

The app already uses `/api/...` in development and can be served from the same origin in production.
If you keep `VITE_API_IP_URL=http://79.143.188.153`, production requests still work because the frontend is hosted on the same host.

Copy the built files to the VPS:

```bash
scp -r dist/* ubuntu@79.143.188.153:/tmp/fe-med-office-dist/
```

SSH into the VPS and install the frontend files:

```bash
ssh ubuntu@79.143.188.153
sudo mkdir -p /var/www/fe-med-office
sudo cp -r /tmp/fe-med-office-dist/* /var/www/fe-med-office/
sudo chown -R www-data:www-data /var/www/fe-med-office
```

Switch Nginx to the fullstack config:

```bash
sudo cp /home/ubuntu/med_office/deploy/nginx-fullstack-ip.conf /etc/nginx/sites-available/med-office
sudo nginx -t
sudo systemctl reload nginx
```

Now the public entry point is:

```text
http://79.143.188.153
```

And the backend stays available behind the same origin at:

```text
http://79.143.188.153/api/login
```

## 10. Update after new code

```bash
cd /home/ubuntu/med_office
git pull
./mvnw clean package -DskipTests
sudo systemctl restart med-office
```

## 11. Optional GitHub Actions auto-deploy on push to `main`

This repository now contains a production deploy workflow in `.github/workflows/branch-pipeline.yml`.
To make it work, add these GitHub Actions secrets in the repository settings:

- `PROD_HOST`: for example `79.143.188.153`
- `PROD_PORT`: usually `22`
- `PROD_USERNAME`: for example `ubuntu`
- `PROD_SSH_KEY`: private SSH key that can log in to the VPS
- `PROD_APP_DIR`: for example `/home/ubuntu/med_office`

The workflow will SSH into the VPS, run `git pull`, build the jar, and restart `med-office` after every push to `main`.

The SSH user must be allowed to restart the service without an interactive password prompt. One simple approach is:

```bash
sudo visudo
```

Add a rule like:

```text
ubuntu ALL=NOPASSWD:/bin/systemctl restart med-office,/bin/systemctl status med-office
```

If you use a different deploy user, replace `ubuntu` with that username.

## Logs

```bash
journalctl -u med-office -f
```

## Important note about using a raw IP

Authentication in this app uses session cookies. When you access the backend via plain HTTP and a raw IP address:

- do not use `SameSite=None`
- do not use `Secure=true`

That is why the production profile now reads those values from environment variables.

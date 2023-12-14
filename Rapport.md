### DAI Lab - HTTP infrastructure

## Romain Fleury, Dr. Ing. Julien Billeter


## Step 1: Static Web site

### You are able to explain the content of the nginx.conf file.

La configuration de base de *nginx* se trouve dans ```etc/nginx/nginx.conf```. Le contenu de ce fichier est le suivant :
```
user              nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;
}
```
Les directives du fichier de configuration de base sont expliquées ci-dessous :
- ```user nginx``` : utilisateur sous lequel ```nginx``` est lancé
- ```worker_processes auto``` : nombre de processus ```worker``` que *nginx* doit utiliser (*auto* permettant de déterminer automatiquement le nombre optimal de processus en fonction des capacités du système)
- ```error_log /var/log/nginx/error.log notice``` : chemin du journal d'erreurs et le niveau de gravité des messages à enregistrer (les messages de niveau *notice* (et plus graves) sont enregistrés)
- ```pid /var/run/nginx.pid``` : chemin d'un fichier dans lequel le PID (*Process ID*) de *nginx* est enregistré
- ```events { ... }``` : section définissant les paramètres liés aux événements
- ```worker_connections 1024``` : nombre maximum de connexions par worker
- ```http { ... }``` : section définissant les paramètres liés à la configuration HTTP
- ```include /etc/nginx/mime.types``` : emplacement du fichier de types MIME, qui associe les extensions de fichiers aux types de contenu
- ```default_type application/octet-stream``` : type MIME par défaut à utiliser lorsque le type ne peut pas être déterminé
- ```log_format main ...``` : format des journaux d'accès (par exemple, des informations sur l'adresse IP du client, l'utilisateur distant, l'heure de la requête, etc...)
- ```access_log /var/log/nginx/access.log main;``` : chemin du journal d'accès et format des journaux à utiliser
- ```sendfile on``` : active l'utilisation de la fonction *sendfile* pour la transmission de fichiers statiques à partir du disque vers le client
- ```keepalive_timeout 65``` : délai d'attente maximum pour les connexions persistantes avec le client
- ```include /etc/nginx/conf.d/*.conf``` : inclut tous les fichiers de configuration se terminant par ```.conf``` dans le répertoire spécifié

La configuration particulière du serveur virtuel HTTP est incluse dans la configuration de base de *nginx* via la dernière ligne du fichier ```etc/nginx/nginx.conf```. La configuration particulière se trouve le fichier ```/etc/nginx/conf.d/default.conf``` :
```
server {
listen       80;
listen  [::]:80;
server_name  localhost;
    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```
Les directives du fichier de configuration particulière sont expliquées ci-dessous :
- ```server { ... }``` : section définissant un bloc de configuration spécifique au serveur virtuel
- ```listen 80;```: le serveur écoute les connexions sur le port 80 pour les requêtes HTTP
- ```listen [::]:80``` : le serveur accepte les connexions IPv6 sur le port 80
- ```server_name localhost``` : définit le nom du serveur virtuel (réponses aux requêtes pour le domaine *localhost*).
- ```location / { ...}``` : section définissant la configuration d'une directive de localisation (cette section s'applique à l'URL racine (```/```)
- ```root /usr/share/nginx/html``` : répertoire racine où les fichiers à servir pour cette localisation doivent être pris
- ```index index.html index.htm``` : ordre de recherche des fichiers *index* lorsqu'une requête est faite sur le répertoire (dans ce cas, recherche d'abord "index.html", puis "index.htm")
- ```error_page 500 502 503 504 /50x.html``` : page à afficher en cas d'erreur de serveur (redirige vers */50x.html*)
- ```location = /50x.html { ... }``` : section définissant la configuration d'une directive de localisation pour */50x.html*
- ```root /usr/share/nginx/html``` : répertoire où se trouve le fichier */50x.html*

### You have documented your configuration in your report.

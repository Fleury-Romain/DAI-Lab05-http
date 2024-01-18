## Laboratoire DAI - HTTP infrastructure

### Romain Fleury, Dr. Ing. Julien Billeter

## Etape 0: Dépôt GitHub

Pour ce projet, nous avons créé un dépôt GitHub avec un fichier *README.md*. Nous avons documenté notre projet dans le fichier *Rapport.md*.

## Etape 1: Site web statique

Nous avons créé un répertoire séparé pour notre serveur Web statique, ainsi qu'un fichier Dockerfile nommé *Dockerfile-api* basé une image *nging* qui permet de construire une image Docker.

Nous avons configuré le fichier ```nginx.conf``` pour servir le contenu statique du site sur le port 80. Il est ainsi possible de lancer l'image et d'accéder au contenu statique du site depuis un navigateur.

La configuration de base de *nginx* se trouve dans `etc/nginx/nginx.conf`, dont le contenu est le suivant :
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

La configuration particulière du serveur virtuel HTTP est incluse dans la configuration de base de *nginx* via la dernière ligne du fichier `etc/nginx/nginx.conf`. La configuration particulière se trouve le fichier `/etc/nginx/conf.d/default.conf` :
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

## Etape 2: Docker compose

Nous avons ajouté un fichier de configuration *docker compose* nommé `docker-compose.yml` dans notre dépôt.
Grâce à *docker compose*, il est possible de démarrer et de stopper l'infrastructure avec un seul serveur web statique et d'y accéder sur une machine locale (sur le port 80).
De plus, il est possible de construire les images définies dans le fichier `docker-compose.yml` avec la commande `docker compose build`.
Le contenu du fichier `docker-compose.yml` est le suivant :
```
version: '3.8'
services:
    webserver:
    image: dai_http:latest
    ports:
        - "80:80"
```
Les directives du fichier *docker-compose.yml* sont expliquées ci-dessous :
- `version: '3.8'` : version de la syntaxe utilisée dans le fichier docker-compose.yml
- `services` : section des services dans un fichier Docker Compose (i.e. conteneurs qui sont exécutés)
- `webserver`: nom du service à déclarer (i.e. un service appelé *webserver*)
- `image: dai_http:latest` : nom de l'image Docker à utiliser pour ce service
- `ports: - "80:80"` : mapping de ports entre le système hôte (à gauche) et le conteneur Docker (à droite); la mapping de port indique que les requêtes HTTP faites au port 80 de la machine hôte seront redirigées vers le port 80 du conteneur *webserver* (*dai_http* version la plus récente, dans le cas présent).

En lançant la commande `docker compose up -d` (avec `-d` pour le mode *détaché*), on lance le serveur HTTP sur le port 80 de la machine locale.
Dans un navigateur, en tappant l'adresse `localhost:80`, on trouve le contenu de la page statique (page *index.html*), comme le montre la copie d'écran ci-dessous :

## Etape 3: API du serveur HTTP
Nous avons développé une API (Application Programming Interface) supportant toutes les opérations CRUD (Create Read Update Delete).
Il est possible de démarrer et d'arrêter l'API du serveur en utilisant docker compose, d'accéder à l'API et au serveur statique depuis un navigateur,
et de reconstruire l'image docker avec docker compose.

L'API est construite en utilisant le framework *Javalin* et décrit une petite application de gestion de personnes avec les champs prénom `fname`, nom de famille `lname`, année de naissance `birthyear`, et activité professionnelle `job`. Chaque personne est identifiée de manière unique à l'aide d'un numéro d'identification unique `id` déterminé par l'API. 
L'API repose sur les trois fichiers `HTTPServer.java`, `Controller.java` et `Person.java`. Le fichier HTTPServer.java contient une méthode main() responsable de créer une instance de Javalin et du Controller (voir plus loin). 
Il lie les opérations `GET`, `POST`, `PUT`, et `DELETE` à des méthodes implémentées dans la classe Controller, comme le montre le mapping ci-dessous :
- `GET / -> Controller::welcome(Context ctx)` : message de bienvenue, et affichage des commandes disponibles
- `GET /person -> Controller::getAll(Context ctx)` : affiche toutes les personnes enregistrées
- `GET /person/{id} -> Controller::getOne(Context ctx)` : affiche une personne enregistrée à partir de son id
- `POST /person -> Controller::createOne(Context ctx)` : crée une personne (avec les champs fname:, lname:, birthyear:, et job: devant être fournis dans le corps de la requête)
- `PUT /person/{id} -> Controller::updateOne(Context ctx)` : met à jour le job d'une personne dont l'id est donné (avec le champ job: devant être fourni dans le corps de la requête)
- `DELETE /person/{id} -> Controller::delete(Context ctx)` : supprime des enregistrements la personne dont l'id est fourni
- `DELETE /person/all -> Controller::delete(Context ctx)` : supprime tous les enregistrements de personnes

Des méthodes internes au Controller ont également été écrites de façon à factoriser le code.

Le fichier Person.java implémente la classe `Person` permet de stocker chaque individu créé via l'API. Une personne possède les attributs suivants :

- `String firstname` : prénom
- `String lastname` : nom de famille
- `int birthyear` : année de naissance
- `String job` : activité professionnelle
- `final int id` : numéro d'identification individuel et unique

Une personne est identifiée de manière unique à l'aide de l'attribut `id` qui est construit à partir d'une variable statique de classe `int count` qui s'incrémente à chaque création d'une personne.
Les méthodes suivantes ont été écrites :

- `public int getId()` : accesseur (getter) de l'attribut id
- `public static void deleteId()` : décrémentation de l'id (à faire avant la suppression d'une instance de Person)
- `public static void resetId()` : remise à 0 du compteur d'id
- `public void setJob(String job)` : modificateur (setter) de l'attribut job
- `public String toString()` : redéfinition de la méthode String toString()
- `boolean equals(Person p)` : redéfinition de la méthode boolean equals(Object o)

Durant notre présentation, nous serons heureux de vous guider à travers le code de l'API et vous faire une démonstration pour vous montrer que toutes les opérations CRUD sont fonctionnelles.

## Etape 4: Proxy inverse (Reverse Proxy) avec Traefik

Un proxy inverse, tel que Traefik, est utile pour améliorer la sécurité d'une infrastructure web pour les raisons suivantes :

- Protection contre les attaques DDoS : un proxy inverse peut agir comme un bouclier contre les attaques DDoS en distribuant le trafic entrant de manière équilibrée entre les serveurs, ce qui peut aider à atténuer l'impact de ces attaques.
- Masquage de l'infrastructure interne : un proxy inverse permet de masquer l'infrastructure interne des serveurs, les clients accédant au site web via le proxy, qui renvoie ensuite les requêtes aux serveurs internes ; l'exposition directe des serveurs internes au trafic public est ainsi réduites, renforçant ainsi la sécurité.
- Gestion des certificats SSL/TLS : un proxy inverse peut gérer les certificats SSL/TLS, fournissant une couche de sécurité supplémentaire en chiffrant les communications entre les clients et le proxy ; cela permet également de centraliser la gestion des certificats, simplifiant ainsi les mises à jour et les renouvellements.
- Filtrage et protection contre les menaces : un proxy inverse peut être configuré pour filtrer le trafic en fonction de règles prédéfinies, permettant de bloquer les requêtes malveillantes ou les attaques connues, contribuant à protéger les applications web contre les tentatives d'intrusion.
- Gestion fine des autorisations : un proxy inverse peut être utilisé pour définir des règles d'accès et de contrôle d'accès basées sur des adresses IP, des en-têtes HTTP, ou d'autres critères ; cela offre une gestion fine des autorisations et permet de limiter l'accès à certaines parties de l'infrastructure.
- Séparation des services : un proxy inverse peut être configuré pour diriger le trafic vers des services spécifiques en fonction des règles définies, permettant de séparer les services et d'appliquer des politiques de sécurité différenciées en fonction des besoins.

Pour accéder au tableau de bord Traefik, il est nécessaire d'avoir dans le fichier `docker-compose.yml` les lignes suivantes sous `services: traefik:` :
- `command: - "--api.dashboard=true"` : active le tableau de bord
- `ports: - "8080:8080"` : port pour accéder au tableau de bord

Grâce au tableau de bord de Traefik, on peut accéder aux informations suivantes :
- Vue d'ensemble : vue d'ensemble des services, des routes, des middlewares et d'autres configurations associées à l'infrastructure.
- Statistiques en temps réel : statistiques en temps réel sur le trafic, les requêtes par seconde, les codes de réponse HTTP, etc...
- Gestion des configurations : visualisation et gestion des configurations, y compris les routes, les services, et d'autres paramètres.
- Accès aux journaux : accès aux journaux (pour le débogage et la surveillance).
- Sécurité : il est recommandé de configurer l'authentification pour sécuriser l'accès au tableau de bord.

Le contenu du fichier `Dockerfile-api` pour la conteneurisation (dockerisation) de l'API est le suivant :
```
FROM openjdk:latest
WORKDIR /app
COPY ../code/target/HTTPServer-1.0.jar .
CMD ["java", "-jar", "HTTPServer-1.0.jar"]
```

Le fichier `docker-compose.yml` est modifié comme suit :
```
version: '3.8'

services:

  # Traefik Reverse Proxy
  traefik:
    image: traefik:v2.5
    container_name: traefik
    restart: always
    ports:
      - "80:80"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock

  # Static Web Server with Nginx
  nginx:
    image: nginx:latest
    restart: always
    volumes:
      - ./Ressources/www:/usr/share/nginx/html
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.nginx.rule=Host(`localhost`)"
    expose:
      - "80"
  # Dynamic API Server with Javalin
  api:
    build:
      dockerfile: Ressources/Dockerfile-api
      context: ./
    restart: always
    expose:
      - "7000"
    labels:
     - "traefik.enable=true"
     - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api/`)"
     - "traefik.http.routers.api.service=api"
     - "traefik.http.middlewares.api-stripprefix.stripprefix.prefixes=/api"
     - "traefik.http.middlewares.api.stripprefix.forceSlash=false"
     - "traefik.http.routers.api.middlewares=api-stripprefix"
```

Durant notre présentation, nous serons heureux de vous guider à travers la configuration et vous faire une démonstration pour vous montrer que nous pouvons démarrer l'infrastructure avec 3 conteneurs (serveur statique, serveur dynamique, et proxy inverse) en partant d'un environnement Docker vide et en utilisant docker compose, accéder à chaque serveur à partir d'un navigateur, et prouver que le routage est effectué correctement par le proxy inverse.

## Etape 5: Scalability et répartition de charge

Nous pouvons utiliser docker compose pour démarrer l'infrastructure avec plusieurs instances de chaque serveur (statique et dynamique), et ajouter/supprimer dynamiquement des instances de chaque serveur.

Il convient de modifier le fichier `docker-compose.yml` pour ajouter les champs suivants : 
- `services: nginx: deploy: replicas: 3` et
- `services: api: deploy: replicas: 3`.

Durant notre présentation, nous serons heureux de vous faire une démonstration pour montrer que Traefik effectue un équilibrage de charge entre les instances, et que si l'on ajoute/supprime des instances, la répartition de charge est dynamiquement mise à jour en utilisant les instances disponibles.

## Etape 6: Repartition de charge par Round-Robin et Sticky Sessions

Il faut modifier le fichier `docker-compose.yml` en ajoutant le champ `services: api: labels: - "traefik.http.services.api.loadbalancer.sticky=true"`. Repartition de charge par Round-Robin est activée par défaut.

Pour vérifier la fonctionnalité de la sessions collante, il suffit de se connecter à l'API (via `http://localhost/api/`) via un navigateur, et d'aller inspecter les cookies stockés. Malheureusement, aucun cookie n'est renvoyé au navigateur.

Durant notre présentation, nous serons heureux de vous faire une démonstration pour montrer que notre répartiteur de charge peut distribuer des requêtes HTTP de manière round-robin aux noeuds de serveurs statiques sans état, et qu'il ne gère pas des sessions collantes lorsqu'il transmet des requêtes HTTP aux noeuds de serveurs dynamiques.

## Etape 7: Sécurisation de Traefik par le protocole HTTPS

Des certificats ont été générés par *OpenSSL* avec la commande `openssl req -x509 -nodes -newkey rsa:2048 -keyout localhost.key -out localhost.crt -days 365`.

La configuration du Proxy inverse Traefik est située dans le fichier `traefik.yaml` dont le contenu est donnée ci-dessous :
```
api:
  insecure: true
  
entryPoints:
  http:
    address: ":80"
  https:
    address: ":443"

providers:
    docker:
        exposedByDefault: false

tls:
  options:
    default:
      minVersion: VersionTLS12
      sniStrict: true
      cipherSuites:
        - TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
        - TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
        - TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305

  certificates:
      certFile: "DAI-SSL/localhost.crt"
      keyFile: "DAI-SSL/localhost.key"
      
certificatesResolvers:
  myresolver:
    acme:
        email: romain.fleury@heig-vd.ch
        storage: /etc/traefik/certificates
        caServer: "https://acme-v02.api.letsencrypt.org/directory"
```
Les directives du fichier `traefik.yaml` sont expliquées ci-dessous :

- `api: insecure: true` : activation de l'API de Traefik de manière non sécurisée (sans authentification, utile pour le développement ou les tests, mais pas recommandé en production en raison de problèmes de sécurité)
- `entryPoints` : définition des points d'entrée pour le trafic (un point d'entrée pour le trafic HTTP sur le port 80, et un autre pour le trafic HTTPS sur le port 443)
- `providers: docker:` : Traefik utilise Docker pour détecter automatiquement les services à exposer (`exposedByDefault: false` indique que par défaut, les services ne seront pas exposés sauf si explicitement spécifié par Docker)
- `tls: options: default:` : configuration des options TLS par défaut pour les connexions HTTPS (`minVersion: VersionTLS12` spécifie la version minimale TLS, `sniStrict: true` active la vérification stricte du SNI (Server Name Indication), et `cipherSuites:` détaille les suites de chiffrement autorisées)
- `tls: certificates:` : chemin vers le fichier du certificat SSL (`certFile:` localhost.crt) et la clé privée associée (`keyFile:` localhost.key) utilisés pour configurer le support SSL/TLS
- `certificatesResolvers: myresolver: acme:` : configuration d'un résolveur de certificats utilisant le protocole ACME (`email:` spécifie l'adresse e-mail associée à l'enregistrement ACME, `storage:` définit le répertoire de stockage pour les certificats, et `caServer:` indique l'URL du serveur ACME (Let's Encrypt ici)

Les *Entrypoints* ont été créés dans `traefik.yaml` et dans le fichier `docker-compose.yml` on indique quel service utilise quel *Entrypoint*.

La version finale du fichier `docker-compose.yml` est le suivant
```
version: '3.8'

services:

  # Traefik Reverse Proxy
  traefik:
    image: traefik:v2.5
    container_name: traefik
    restart: always
    ports:
      - "80:80"
      - "8080:8080"
      - "443:443"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./Ressources/traefik.yaml:/etc/traefik/traefik.yaml
      - ./Ressources/DAI-SSL:/etc/traefik/certificates

  # Static Web Server with Nginx
  nginx:
    image: nginx:latest
    restart: always
    deploy:
      replicas: 3
    volumes:
      - ./Ressources/www:/usr/share/nginx/html
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.nginx.rule=Host(`localhost`)"
      - "traefik.http.routers.nginx.entrypoints=https"
      - "traefik.http.routers.nginx.tls=true"
    expose:
      - "80"
  # Dynamic API Server with Javalin
  api:
    build:
      dockerfile: Ressources/Dockerfile-api
      context: ./
    restart: always
    expose:
      - "7000"
    deploy:
      replicas: 3
    labels:
     - "traefik.enable=true"
     - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api/`)"
     - "traefik.http.routers.api.service=api"
     - "traefik.http.routers.api.entrypoints=https"    
     - "traefik.http.routers.api.tls=true"
     - "traefik.http.services.api.loadbalancer.sticky=true"
     - "traefik.http.middlewares.api-stripprefix.stripprefix.prefixes=/api"
     - "traefik.http.middlewares.api.stripprefix.forceSlash=false"
     - "traefik.http.routers.api.middlewares=api-stripprefix"
```
A la fin de cette étapes, les deux services utilisent l'*Entrypoint* HTTPS (port 443).

Durant notre présentation, nous serons heureux de vous faire une démonstration pour montrer que les serveurs statiques et dynamiques sont accessibles via HTTPS

## Etapes optionnelles
Nous n'avons malheureusement pas eu le temps de faire d'étapes optionnelles.
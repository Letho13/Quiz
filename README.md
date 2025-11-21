#  Projet Quiz Microservices

Une application Quiz de culture générale permettant aux utilisateurs de tester leurs connaissances, suivre leurs scores et se comparer aux autres joueurs.

Ce projet met en avant une architecture microservices complète avec Spring Boot 3, Angular, PostgreSQL, Spring Security (JWT & OAuth2) et Docker — développée dans un but à la fois professionnel et pédagogique.

##  Table des matières

1. [À propos du projet](#-à-propos-du-projet)
2. [Stack Technique](#-stack-technique)
3. [Architecture Microservices](#-architecture-microservices)
4. [Démarrage](#-démarrage)
5. [Licence](#-licence)

---

##  À propos du projet

Ce projet a été conçu pour :

* Illustrer mes compétences en développement backend & frontend full-stack ;
* Mettre en pratique des notions de microservices, sécurité, CI/CD, et conteneurisation ;
* Créer une application concrète et évolutive pouvant servir de base à un produit réel.

---

##  Stack Technique

Voici les principales technologies utilisées dans ce projet :

<p align="left">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security"/>
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular"/>
  <img src="https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/OAuth2-2396F3?style=for-the-badge&logo=auth0&logoColor=white" alt="OAuth2"/>
</p>

---

##  Architecture Microservices

L’application repose sur plusieurs services indépendants, interconnectés via **Spring Cloud** :

| Service                          | Rôle principal |
|:---------------------------------| :--- |
| **Config Server**             | Fournit la configuration centralisée à tous les services. |
| **Discovery Service (Eureka)** | Gère l’enregistrement et la découverte des microservices. |
| **Gateway Service**              | Point d’entrée unique (API Gateway, filtrage, routage, sécurité). |
| **Quiz Service**                 | Gestion des quiz, questions et réponses. |
| **User Service**                 | Gestion des utilisateurs, rôles, authentification (JWT & OAuth2). |
| **Reward Service**               | Calcul des scores, récompenses et classement des utilisateurs. |
| **Front Service (Angular)**      | Interface utilisateur. |
| **PostgreSQL**                   | Base de données dédiée pour chaque service (user, quiz, reward). |
| **Zipkin**                       | Traçabilité et monitoring des requêtes distribuées. |

---

##  Démarrage

Pour lancer le projet en local, suivez ces étapes :

### 1. Prérequis

* Java 21
* Docker & Docker Compose
* Docker Desktop
* Node.js & Angular CLI

### 2. Installation

1.  Clonez le dépôt :
    ```sh
    git clone https://github.com/Letho13/Quiz.git
    cd Quiz
    ```

2.  Lancez l'environnement (le docker-compose.override) :
    ```sh
    docker-compose up -d
    ```


---

### Utilisateurs par défaut

Un utilisateur administrateur est créé par défaut pour tester l'application :

- **Username** : admin
- **Mot de passe** : Admin123!
- **Rôle** : ADMIN

Vous pouvez vous connecter avec cet compte pour gérer les utilisateurs.

### Démo live disponible ici :

https://quizcult.dewal.fr/auth/login

---

### Intéressé(e) ?

Je suis activement à la recherche d'opportunités ! Si ce projet vous a plu ou si vous souhaitez simplement échanger, n'hésitez pas à me contacter.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/th%C3%A9otime-lebreton-5ba87368/)

---

##  Licence

Ce projet est sous licence MIT - voir le fichier `LICENSE.md` pour plus de détails.

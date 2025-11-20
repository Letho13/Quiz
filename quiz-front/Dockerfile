# Étape de build Angular
FROM node:20-alpine AS build

WORKDIR /app

# Copier les fichiers package.json et package-lock.json
COPY package*.json ./

# Installer les dépendances
RUN npm install

# Copier le reste des sources
COPY . .

# Construire l'application Angular en mode production
# Ajout de --base-href / pour s'assurer que les assets (JS/CSS) sont chargés à la racine
RUN npm run build -- --configuration production --base-href /


# Étape de production : image Nginx
FROM nginx:alpine

# Supprimer la configuration par défaut de Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copier une configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers Angular construits dans le dossier Nginx
COPY --from=build /app/dist/quiz-front/browser /usr/share/nginx/html

# Exposer le port par défaut
EXPOSE 80

# Lancer Nginx
CMD ["nginx", "-g", "daemon off;"]

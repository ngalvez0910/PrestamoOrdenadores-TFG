# Usa Node.js oficial
#FROM node:18-alpine

# Crea y entra al directorio de la app
#WORKDIR /app

# Copia solo los archivos necesarios primero (para caché)
#COPY package*.json ./

# Instala dependencias
#RUN npm install

# Copia el resto del proyecto
#COPY . .

# Expone el puerto de Vite
#EXPOSE 5175

# Inicia el servidor de desarrollo
#CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5175"]

FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Etapa de producción (servir con Nginx)
FROM nginx:alpine
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

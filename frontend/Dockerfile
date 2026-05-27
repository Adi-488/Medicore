# ─────────────────────────────────────────────────────────
# CurePulse HMS — Dockerfile (React Frontend)
# Builds and serves the Vite React Web Dashboard using Nginx
# ─────────────────────────────────────────────────────────

# Stage 1: Build stage
FROM node:18-alpine AS build
WORKDIR /app

# Copy package descriptors first to optimize caching
COPY package*.json ./
RUN npm ci

# Copy sources
COPY . .

# Pass build time environment variables
ARG VITE_API_URL
ARG VITE_AUTH_API_URL

ENV VITE_API_URL=$VITE_API_URL
ENV VITE_AUTH_API_URL=$VITE_AUTH_API_URL

# Build the production bundle
RUN npm run build

# Stage 2: Web Server Stage
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Expose HTTP port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

# Use an official Node.js runtime as the base image
FROM node:18-alpine AS builder

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install the application dependencies
# RUN npm ci

# Copy the rest of the application code to the working directory
COPY . .

# Set build-time environment variable to skip prefetch
ENV NEXT_TELEMETRY_DISABLED 1
# ENV NEXT_PUBLIC_API_URL http://localhost/api
ENV NEXT_PUBLIC_API_URL http://ecommerce-app1:8080


# Build the Next.js application
RUN npm run build

# Start a new stage for a smaller production image
FROM node:18-alpine

WORKDIR /app

# Copy built assets from the builder stage
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/public ./public

# Expose the port that Next.js runs on
EXPOSE 3000

# Start the Next.js application
CMD ["npm", "start"]

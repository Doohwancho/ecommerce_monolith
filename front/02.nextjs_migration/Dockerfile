# Use an official Node.js runtime as the base image
FROM node:18-alpine

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install the application dependencies
RUN npm install

# Copy the models directory
COPY models ./models

# Copy the rest of the application code to the working directory
COPY . .

# TODO - build fail because pre-render시 nextjs -> nginx container로 fetch fail. 네트워크 문제.
RUN npm run build

# Expose the port that Next.js runs on
EXPOSE 3000

# Start the Next.js application
# CMD ["npm", "run", "dev"]
CMD ["npm", "start"]
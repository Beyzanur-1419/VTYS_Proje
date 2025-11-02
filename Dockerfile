FROM node:16-alpine

# Add package for security
RUN apk add --no-cache dumb-init

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
COPY package*.json ./
RUN npm ci --only=production

# Bundle app source
COPY . .

# Create uploads directory
RUN mkdir -p uploads && chown -R node:node uploads

# Use non-root user
USER node

# Use dumb-init as entrypoint
ENTRYPOINT ["dumb-init", "--"]

# Start the application
CMD ["npm", "start"]
# ASID-MS

# 1. Para os containers
docker-compose down

# 2. Remove a imagem antiga do book-service
docker rmi asid-ms-book-service:latest

# 3. Rebuild da imagem sem cache
docker-compose build --no-cache

# 4. Sobe os containers novamente
docker-compose up
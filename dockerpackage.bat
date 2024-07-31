docker buildx build -t minecopre/webapp . --no-cache --platform=linux/arm64 --push --target package
docker rmi $(docker images -f "dangling=true" -q)
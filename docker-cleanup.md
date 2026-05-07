# Docker Cleanup

after a docker compose down, to clean up all lingering docker data (containers, volumes, networks, images)\

in a ubuntu shell where dockerd is running:
```bash
docker system prune -a --volumes -f
```
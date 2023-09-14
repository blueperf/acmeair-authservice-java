podman build -t ol-auth -f Dockerfile-daily . 
podman build -t wf-auth -f Dockerfile-wf .
podman build -t pm-auth -f Dockerfile-pm .
podman build -t qu-auth -f Dockerfile-qu .
podman build -t qn-auth -f Dockerfile-qn .
podman build -t tm-auth -f Dockerfile-tm .
podman build -t hd-auth -f Dockerfile-hd .
podman build -t ol-io-auth -f Dockerfile-daily-io --cpuset-cpus="2-3" --cap-add=CHECKPOINT_RESTORE --cap-add=SYS_PTRACE --security-opt seccomp=unconfined .

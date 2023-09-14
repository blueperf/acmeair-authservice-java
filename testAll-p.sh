CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" ol-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" wf-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" pm-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" tm-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" hd-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" qu-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" qn-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" --cap-add=CHECKPOINT_RESTORE --cap-add=SETPCAP --security-opt seccomp=unconfined ol-io-auth)
sleep 20
curl localhost:9080/auth/status
podman stop ${CID}
podman rm ${CID}

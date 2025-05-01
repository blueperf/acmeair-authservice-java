
podman build -t ol-auth -f Dockerfile-ol .
podman build -t wl-auth -f Dockerfile-wl .
podman build -t wf-auth -f Dockerfile-wf .
podman build -t qn-auth -f Dockerfile-qn .
podman build -t pm-auth -f Dockerfile-pm .
podman build -t qu-auth -f Dockerfile-qu .
podman build -t tm-auth -f Dockerfile-tm .
podman build -t hd-auth -f Dockerfile-hd .
podman build -t hd4-auth -f Dockerfile-hd4 .
podman build -t ol-io-auth -f Dockerfile-io --cpu-quota=200000 -m 1g --cap-add=CHECKPOINT_RESTORE --cap-add=SYS_PTRACE --security-opt seccomp=unconfined .

sed -i "s@<feature>microProfile-7.0</feature>@<feature>microProfile-6.1</feature>@" src/main/liberty/config/server.xml
podman build -t old-auth -f Dockerfile-old .
podman build -t ol-61-auth -f Dockerfile-ol .
sed -i "s@<feature>microProfile-6.1</feature>@<feature>microProfile-7.0</feature>@" src/main/liberty/config/server.xml

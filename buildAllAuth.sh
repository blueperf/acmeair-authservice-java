
docker build -t ol-auth -f Dockerfile .
docker build -t wf-auth -f Dockerfile-wf .
docker build -t tm-auth -f Dockerfile-tm .
docker build -t pm-auth -f Dockerfile-pm .
docker build -t hd-auth -f Dockerfile-hd .


CID=$(docker run -p 9080:9080 -d ol-auth)
sleep 15
call=$(curl -v localhost:9080/auth/status 2>/dev/null)

if [[ $(echo ${call} | grep -c "OK") == 1 ]]
then
  echo "SUCCESS-OL"
else
  echo "FAIL-OL"
fi
docker stop ${CID}  > /dev/null 2>&1

CID=$(docker run -p 9080:9080 -d wf-auth)
sleep 15
call=$(curl -v localhost:9080/auth/status 2>/dev/null)

if [[ $(echo ${call} | grep -c "OK") == 1 ]]
then
  echo "SUCCESS-WF"
else
  echo "FAIL-WF"
fi
docker stop ${CID}  > /dev/null 2>&1

CID=$(docker run -p 9080:9080 -d tm-auth)
sleep 15
call=$(curl -v localhost:9080/auth/status 2>/dev/null)

if [[ $(echo ${call} | grep -c "OK") == 1 ]]
then
  echo "SUCCESS-TM"
else
  echo "FAIL-TM"
fi
docker stop ${CID}  > /dev/null 2>&1

CID=$(docker run -p 9080:9080 -d pm-auth)
sleep 15
call=$(curl -v localhost:9080/auth/status 2>/dev/null)

if [[ $(echo ${call} | grep -c "OK") == 1 ]]
then
  echo "SUCCESS-PM"
else
  echo "FAIL-PM"
fi
docker stop ${CID}  > /dev/null 2>&1

CID=$(docker run -p 9080:9080 -d hd-auth)
sleep 15
call=$(curl -v localhost:9080/auth/status 2>/dev/null)

if [[ $(echo ${call} | grep -c "OK") == 1 ]]
then
  echo "SUCCESS-HD"
else
  echo "FAIL-HD"
fi
docker stop ${CID}  > /dev/null 2>&1

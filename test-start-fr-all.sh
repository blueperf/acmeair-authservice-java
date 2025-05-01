set -x

./doStartFr.sh ol-auth > ol-run 2>&1
./doStartFr.sh older-auth > older-run 2>&1
./doStartFr.sh old-auth > old-run 2>&1
./doStartFr.sh wl-auth > wl-run 2>&1
./doStartFr-io.sh ol-io-auth > ol-io-run 2>&1
./doStartFr.sh wf-auth > wf-run 2>&1
./doStartFr.sh pm-auth > pm-run 2>&1
./doStartFr.sh tm-auth > tm-run 2>&1
./doStartFr.sh hd-auth > hd-run 2>&1
./doStartFr.sh hd4-auth > hd4-run 2>&1
./doStartFr.sh qu-auth > qu-run 2>&1
./doStartFr.sh qn-auth > qn-run 2>&1

echo "ol"
./parse.sh ol-run
echo "older"
./parse.sh older-run
echo "old"
./parse.sh old-run
echo "wl"
./parse.sh wl-run
echo "ol-io"
./parse.sh ol-io-run
echo "wf"
./parse.sh wf-run
echo "pm"
./parse.sh pm-run
echo "tm"
./parse.sh tm-run
echo "hd"
./parse.sh hd-run
echo "hd4"
./parse.sh hd4-run
echo "qu"
./parse.sh qu-run
echo "qn"
./parse.sh qn-run

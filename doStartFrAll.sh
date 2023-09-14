./doStartFr.sh ol-auth > ol-run 2>&1
./doStartFr.sh wf-auth > wd-run 2>&1
./doStartFr.sh pm-auth > pm-run 2>&1
./doStartFr.sh tm-auth > tm-run 2>&1
./doStartFr.sh hd-auth > hd-run 2>&1
./doStartFr.sh qu-auth > qu-run 2>&1
./doStartFr.sh qn-auth > qn-run 2>&1
./doStartFr-io.sh ol-io-auth > ol-io-run 2>&1

echo "ol"
./parse.sh ol-run
echo "wf"
./parse.sh wf-run
echo "pm"
./parse.sh pm-run
echo "tm"
./parse.sh tm-run
echo "hd"
./parse.sh hd-run
echo "qu"
./parse.sh qu-run
echo "qn"
./parse.sh qn-run
echo "ol-io"
./parse.sh ol-io-run

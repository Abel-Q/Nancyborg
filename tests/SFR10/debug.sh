command='cd java
pid=$(netstat -ltpn | grep :8000 | grep -Po "(\d+)(?=/java)")

if [[ ! -z $pid ]]; then
    kill $pid;
fi

export CLASSPATH=jna.jar:sfr10

java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000 Main
'

ssh root@pi.local "$command"

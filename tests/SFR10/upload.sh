#!/bin/sh
cd `dirname "$0"`

ssh root@pi.local mkdir -p java/sfr10
scp -r bin/* root@pi.local:java/sfr10/

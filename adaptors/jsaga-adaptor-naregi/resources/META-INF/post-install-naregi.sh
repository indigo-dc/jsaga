#!/bin/sh

if test -z "$1" ; then
    echo "Missing required argument: <install-dir>"
    exit 1
fi
NAREGI_HOME=$1

SOURCE=`mktemp -d`
PWD_SAVED=$PWD

# Install NAREGI-SS client
cd $SOURCE
wget http://rpm1.naregi.org/beta2/html/Packages/SS_r1349.tar.gz
tar xzvf SS_r1349.tar.gz
cd ./Progs/
sh OPENDIST.sh $NAREGI_HOME << EOF
y
n
EOF

# Install patch
wget http://grid.in2p3.fr/maven2/org/naregi/ss/naregi-ss-api/beta-2/naregi-ss-api-beta-2-patch.tar.gz
tar xzvf naregi-ss-api-beta-2-patch.tar.gz -C $NAREGI_HOME

# Cleanup
cd $PWD_SAVED
rm -rf $SOURCE

#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#Workspace is set by the jenkins, by default (local run) is the parent directory of the checkout.
WORKSPACE=${WORKSPACE:-$DIR/../../..}
cd $WORKSPACE

YETUSDIR=${WORKSPACE}/yetus
TESTPATCHBIN=${YETUSDIR}/bin/test-patch
ARTIFACTS=${WORKSPACE}/out
BASEDIR=${WORKSPACE}/sourcedir
TOOLS=${WORKSPACE}/tools
rm -rf "${ARTIFACTS}" #"${YETUSDIR}"
mkdir -p "${ARTIFACTS}" "${YETUSDIR}" "${TOOLS}"

#It's not on all the branch, so we need to copy it from the checkout out source
cp $BASEDIR/dev-support/yetus-personality.sh $WORKSPACE/
cp $BASEDIR/dev-support/docker/Dockerfile $WORKSPACE/

echo "Downloading Yetus"
#curl -L https://archive.apache.org/dist/yetus/0.5.0/yetus-0.5.0-bin.tar.gz -o yetus.tar.gz
#gunzip -c yetus.tar.gz | tar xpf - -C "${YETUSDIR}" --strip-components 1

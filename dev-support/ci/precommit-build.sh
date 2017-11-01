#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#Workspace is set by the jenkins, by default (local run) is the parent directory of the checkout.
WORKSPACE=${WORKSPACE:-$DIR/../../..}

YETUSDIR=${WORKSPACE}/yetus
TESTPATCHBIN=${YETUSDIR}/bin/test-patch
ARTIFACTS=${WORKSPACE}/out
BASEDIR=${WORKSPACE}/sourcedir
TOOLS=${WORKSPACE}/tools
rm -rf "${ARTIFACTS}" "${YETUSDIR}"
mkdir -p "${ARTIFACTS}" "${YETUSDIR}" "${TOOLS}"

#It's not on all the branch, so we need to copy it from the checkout out source
cp $BASEDIR/dev-support/yetus-personality.sh $WORKSPACE/

echo "Downloading Yetus"
#curl -L https://archive.apache.org/dist/yetus/0.5.0/yetus-0.5.0-bin.tar.gz -o yetus.tar.gz
#gunzip -c yetus.tar.gz | tar xpf - -C "${YETUSDIR}" --strip-components 1


YETUS_ARGS+=("--archive-list=checkstyle-errors.xml,findbugsXml.xml")
YETUS_ARGS+=("--basedir=${BASEDIR}")
YETUS_ARGS+=("--brief-report-file=${ARTIFACTS}/email-report.txt")
YETUS_ARGS+=("--build-url-artifacts=artifact/out")
YETUS_ARGS+=("--console-report-file=${ARTIFACTS}/console-report.txt")
YETUS_ARGS+=("--console-urls")
YETUS_ARGS+=("--docker")
YETUS_ARGS+=("--dockerfile=${BASEDIR}/dev-support/docker/Dockerfile")
YETUS_ARGS+=("--dockermemlimit=20g")
YETUS_ARGS+=("--findbugs-strict-precheck")
YETUS_ARGS+=("--html-report-file=${ARTIFACTS}/console-report.html")
YETUS_ARGS+=("--jenkins")
YETUS_ARGS+=("--jira-password=${JIRA_PASSWORD}")
YETUS_ARGS+=("--jira-user=hadoopqa")
YETUS_ARGS+=("--multijdkdirs=/usr/lib/jvm/java-8-oracle")
YETUS_ARGS+=("--mvn-custom-repos")
YETUS_ARGS+=("--patch-dir=${ARTIFACTS}")
YETUS_ARGS+=("--project=ratis")
YETUS_ARGS+=("--personality=${WORKSPACE}/yetus-personality.sh")
YETUS_ARGS+=("--proclimit=5000")
YETUS_ARGS+=("--resetrepo")
YETUS_ARGS+=("--sentinel")
YETUS_ARGS+=("--shelldocs=/testptch/hadoop/dev-support/bin/shelldocs")
YETUS_ARGS+=("--skip-dir=dev-support")
YETUS_ARGS+=("--tests-filter=checkstyle,pylint,shelldocs")

YETUS_ARGS+=("RATIS-${ISSUE_NUM}")


/bin/bash ${TESTPATCHBIN} "${YETUS_ARGS[@]}"

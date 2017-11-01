#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/common.sh

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

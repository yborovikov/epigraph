#!/usr/bin/env bash
##
# Deploys artifacts to the repository. The new version is taken from NEW_VERSION variable or inferred from the latest
# version of the main pom in the repository by incrementing its last (e.g. PATCH) part by 1.
# Note: This script cannot currently handle -SNAPSHOT version deployments.
# Note: The script is expected to be run from the root of the project.

# command line args, all optional:
# -t {github access token} -v {new version}

set -e

BUILD_PLUGIN=true

while getopts ":v:t:" opt; do
  case $opt in
    v) NEW_VERSION="$OPTARG"
    ;;
    t) GITHUB_TOKEN="$OPTARG"
    ;;
    \?) echo "Invalid option -$OPTARG" >&2
    ;;
  esac
done

##
# Splits the string argument around '.' as delimiter, increments the last part, and echoes incremented and re-assembled
# version.
function bump() {
  # TODO check version argument is well-formed (at theast the last segment being numeric)?
  local PARTS IFS='.'
  read -a PARTS <<< "$1"
  local LAST=$((${#PARTS[*]} - 1))
  PARTS[$LAST]=$((${PARTS[$LAST]} + 1))
  echo "${PARTS[*]}"
}

if [ -z "$NEW_VERSION" ]; then
  RELEASED_VERSION="$(\
    ./mvnw --quiet --non-recursive build-helper:released-version \
      exec:exec -Dexec.executable='echo' -Dexec.args='${releasedVersion.version}' \
  )"
  echo "Latest released version: $RELEASED_VERSION"
  NEW_VERSION=$(bump "$RELEASED_VERSION")
fi

# project poms will be flattened (build-time sections removed, etc.) for release - extract release repo coordinates first
RELEASE_REPO="$(\
  ./mvnw --quiet --non-recursive exec:exec -Dexec.executable='echo' \
  -Dexec.args='${project.distributionManagement.repository.id}::${project.distributionManagement.repository.layout}::${project.distributionManagement.repository.url}' \
)"
# set the option if all the properties were resolved
[[ "$RELEASE_REPO" == *'${'* ]] || RELEASE_REPO_OPTION="-DaltReleaseDeploymentRepository=$RELEASE_REPO"
# TODO similar to above for snapshotRepository (if we're planning to release -SNAPSHOT versions; update bump logic then)

echo -------------------------------------------------------------------------------
echo ------ Releasing version $NEW_VERSION
echo -------------------------------------------------------------------------------

set -x

./mvnw --show-version --batch-mode -Dbuildtime.output.log "$RELEASE_REPO_OPTION" \
  clean deploy -Plight-psi,release "-Drevision=$NEW_VERSION" -DdeployAtEnd=true

if $BUILD_PLUGIN; then
  ./gradlew -c settings-bootstrap.gradle clean publishGradlePlugins
  ./gradlew -PepigraphVersion=$NEW_VERSION clean :idea-plugin:buildPlugin
fi

git tag "v$NEW_VERSION" && git push origin "v$NEW_VERSION"

if [ ! -z "$GITHUB_TOKEN" ]; then
    { set +x; } 2>/dev/null
    rm -f /tmp/epigraph_release.json
    PLUGIN="epigraph-idea-plugin-$NEW_VERSION.zip"
    GITHUB_REPO="SumoLogic/epigraph"
    set -x

    curl -s --data "{\"tag_name\":\"v$NEW_VERSION\",\"name\": \"v$NEW_VERSION\",\"body\": \"Release $NEW_VERSION\",\"draft\": false,\"prerelease\": false}" \
      "https://api.github.com/repos/$GITHUB_REPO/releases?access_token=$GITHUB_TOKEN" > /tmp/epigraph_release.json

    if $BUILD_PLUGIN; then
      RELEASE_ID=$(grep "^  \"id\":" /tmp/epigraph_release.json | sed -e "s/^  \"id\": \([0-9]*\),$/\1/")

      curl -s -X POST --header "Content-Type:application/zip" --data-binary "@idea-plugin/build/distributions/$PLUGIN" \
      "https://uploads.github.com/repos/$GITHUB_REPO/releases/$RELEASE_ID/assets?name=$PLUGIN&access_token=$GITHUB_TOKEN"
    fi
fi

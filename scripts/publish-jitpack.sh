#!/usr/bin/env bash
# JitPack reads artifacts from Maven Local. Pass another directory for local verification.
set -euo pipefail
cd "$(dirname "$0")/.."

repository="${1:-$HOME/.m2/repository}"
mkdir -p "$repository"
repository="$(cd "$repository" && pwd)"
# Kotlin 2.1.x requires this opt-in to produce Apple klibs on Linux.
# This publishes libraries; it does not link or test Apple frameworks.
arguments=("--no-daemon" "--no-configuration-cache"
    "-Pkotlin.native.enableKlibsCrossCompilation=true" "-Dmaven.repo.local=$repository")

# JitPack may build a release tag, candidate tag, or commit hash.
# Every publication and its internal dependencies must use that exact version.
if [[ "${JITPACK:-false}" == "true" ]]; then
    : "${VERSION:?JitPack must supply the version being built}"
    arguments+=("-PlibraryVersion=$VERSION")
fi

command -v python3 >/dev/null
./gradlew "${arguments[@]}" :core:clean :charts:clean
./gradlew "${arguments[@]}" :core:publishToMavenLocal :charts:publishToMavenLocal
python3 scripts/verify-publications.py "$repository"

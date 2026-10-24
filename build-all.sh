#!/usr/bin/env bash
set -u
cd "$(dirname "$0")"
rm -f build/libs/*mc*.jar
mkdir -p build/libs

TARGETS=(
  "fabric-1.20 1.20.1 1.20.1"
  "forge-1.20.1 1.20.1 1.20.1"
  "neoforge 26.3 26.3"
  "neoforge 26.2 26.2"
  "neoforge 26.1.2 26.1.x"
  "neoforge 1.21.11 1.21.11"
  "neoforge 1.21.10 1.21.10"
  "neoforge 1.21.9 1.21.9"
  "neoforge 1.21.8 1.21.8"
  "neoforge 1.21.4 1.21.4"
  "neoforge 1.21.1 1.21.1"
  "fabric-26 26.3 26.3"
  "fabric-26 26.2 26.2"
  "fabric-26 26.1.2 26.1.x"
  "forge-26 26.3 26.3"
  "forge-26 26.2 26.2"
  "forge-26 26.1.2 26.1.x"
  "fabric-1.21 1.21.11 1.21.11"
  "fabric-1.21 1.21.10 1.21.9-1.21.10"
  "fabric-1.21 1.21.8 1.21.8"
  "fabric-1.21 1.21.4 1.21.4"
  "fabric-1.21 1.21.2 1.21.2"
  "fabric-1.21 1.21.1 1.21.1"
  "forge-1.21 1.21.11 1.21.11"
  "forge-1.21 1.21.10 1.21.10"
  "forge-1.21 1.21.9 1.21.9"
  "forge-1.21 1.21.8 1.21.8"
  "forge-1.21 1.21.4 1.21.4"
  "forge-1.21 1.21.1 1.21.1"
)

JDK21="${JDK21:-C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot}"

failed=()
for t in "${TARGETS[@]}"; do
  read -r dir mc label <<<"$t"
  echo "=== $dir  mc=$mc  ($label)"
  extra=()
  [ "$dir" = "forge-1.20.1" ] && extra=("-Dorg.gradle.java.home=$JDK21")
  if [ "$dir" = "fabric-1.20" ]; then wd="."; else wd="targets/$dir"; fi
  if ! (cd "$wd" && ./gradlew build -PmcVersion="$mc" -PmcLabel="$label" "${extra[@]}" --console=plain --no-configuration-cache -q); then
    failed+=("$t")
  fi
done

echo
ls build/libs
if [ ${#failed[@]} -gt 0 ]; then
  echo "FAILED:"; printf '  %s\n' "${failed[@]}"; exit 1
fi

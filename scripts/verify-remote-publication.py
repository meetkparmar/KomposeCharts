"""Verify JitPack artifacts using the same classifier repair as installation."""
import concurrent.futures
import hashlib
import json
import re
import sys
import time
import urllib.error
import urllib.request

version = sys.argv[1]
if not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+(?:-rc\.[0-9]+)?", version):
    raise SystemExit("Expected a stable or rc version")
base = "https://jitpack.io/com/github/meetkparmar/KomposeCharts"


def download(url, attempts=4):
    for attempt in range(attempts):
        try:
            with urllib.request.urlopen(url, timeout=45) as response:
                return response.read()
        except (urllib.error.URLError, TimeoutError) as error:
            if attempt == attempts - 1:
                raise
            delay = 30
            if isinstance(error, urllib.error.HTTPError) and error.code == 429:
                retry_after = error.headers.get("Retry-After", "60")
                delay = max(60, int(retry_after) if retry_after.isdigit() else 60)
            print(f"Waiting {delay}s for artifact (attempt {attempt + 1}): {url}", flush=True)
            time.sleep(delay)


def check(name):
    directory = f"{base}/{name}/{version}"
    metadata = json.loads(download(f"{directory}/{name}-{version}.module"))
    if metadata["component"]["version"] != version:
        raise ValueError(f"Wrong version: {name}")
    seen = set()
    core_found = False
    for variant in metadata["variants"]:
        available = variant.get("available-at")
        if available and (available["group"] != "com.github.meetkparmar.KomposeCharts" or available["version"] != version):
            raise ValueError(f"Wrong platform reference: {name}")
        for dependency in variant.get("dependencies", []):
            if dependency["group"] == "com.github.meetkparmar.KomposeCharts":
                if dependency["version"]["requires"] != version:
                    raise ValueError(f"Wrong internal dependency version: {name}")
                core_found |= dependency["module"].startswith("core")
        for file in variant.get("files", []):
            filename = file["url"]
            if "SourcesElements" in variant["name"]:
                filename = f"{name}-{version}-sources.jar"
            elif name.endswith(("-iosarm64", "-iossimulatorarm64", "-iosx64")) and "MetadataElements" in variant["name"]:
                filename = f"{name}-{version}-metadata.jar"
            elif name.startswith("charts-ios") and "ResourcesElements" in variant["name"]:
                filename = f"{name}-{version}-kotlin_resources.kotlin_resources.zip"
            if filename in seen:
                continue
            seen.add(filename)
            binary = download(f"{directory}/{filename}")
            if len(binary) != file["size"]:
                raise ValueError(f"Size mismatch: {filename}")
            for algorithm in ("sha256", "sha512"):
                if algorithm in file and hashlib.new(algorithm, binary).hexdigest() != file[algorithm]:
                    raise ValueError(f"Checksum mismatch: {filename}")
    if name.startswith("charts") and not core_found:
        raise ValueError(f"Missing transitive core dependency: {name}")
    return f"Verified {name}: {len(seen)} binary/source/metadata files"


names = [lib + suffix for lib in ("core", "charts") for suffix in
         ("", "-android", "-desktop", "-iosarm64", "-iossimulatorarm64", "-iosx64")]
# A POM request starts JitPack's build; probing .module files alone can return
# 404 without starting it. Wait for readiness before requesting platform files.
download(f"https://jitpack.io/com/github/meetkparmar/KomposeCharts/{version}/KomposeCharts-{version}.pom", attempts=12)
with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
    for result in pool.map(check, names):
        print(result, flush=True)
if len(sys.argv) > 2:
    build = json.loads(download(f"https://jitpack.io/api/builds/com.github.meetkparmar/KomposeCharts/{version}"))
    if build.get("status") != "ok" or build.get("commit") != sys.argv[2]:
        raise SystemExit("JitPack build did not match the expected commit")
print(f"All 12 publications verified for {version}, with the documented classifier repair.")

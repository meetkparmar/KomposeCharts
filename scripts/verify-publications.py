"""Reject incomplete Maven Local output before JitPack serves it."""

import json
import sys
from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
REPOSITORY = Path(sys.argv[1]).resolve()
GROUP = "com.github.meetkparmar.KomposeCharts"
NAMESPACE = {"m": "http://maven.apache.org/POM/4.0.0"}
TARGETS = {
    "": "jar",
    "-android": "aar",
    "-desktop": "jar",
    "-iosarm64": "klib",
    "-iossimulatorarm64": "klib",
    "-iosx64": "klib",
}


def require(condition, message):
    if not condition:
        raise SystemExit(message)


# Read the version Gradle actually used, including any JitPack override.
pom = ET.parse(ROOT / "core/build/publications/kotlinMultiplatform/pom-default.xml")
version = pom.findtext("m:version", namespaces=NAMESPACE)
require(version, "Missing publication version")
base = REPOSITORY / GROUP.replace(".", "/")

for library in ("core", "charts"):
    for suffix, extension in TARGETS.items():
        artifact = library + suffix
        directory = base / artifact / version
        prefix = f"{artifact}-{version}"
        for filename in (f"{prefix}.pom", f"{prefix}.module",
                         f"{prefix}.{extension}", f"{prefix}-sources.jar"):
            path = directory / filename
            require(path.is_file() and path.stat().st_size > 0,
                    f"Missing or empty publication file: {path}")

        published_pom = ET.parse(directory / f"{prefix}.pom")
        require(published_pom.findtext("m:groupId", namespaces=NAMESPACE) == GROUP,
                f"Incorrect group in {artifact}")
        require(published_pom.findtext("m:version", namespaces=NAMESPACE) == version,
                f"Incorrect version in {artifact}")

        metadata = json.loads((directory / f"{prefix}.module").read_text())
        require(metadata["component"]["group"] == GROUP, f"Incorrect metadata group: {artifact}")
        require(metadata["component"]["version"] == version, f"Incorrect metadata version: {artifact}")
        core_dependencies = []
        for variant in metadata["variants"]:
            for file in variant.get("files", []):
                file_path = directory / file["url"]
                require(file_path.is_file(), f"Missing metadata artifact: {file_path}")
                require(file_path.stat().st_size == file["size"], f"Incorrect artifact size: {file_path}")
            available = variant.get("available-at")
            if available:
                require(available["group"] == GROUP and available["version"] == version,
                        f"Incorrect platform coordinates in {artifact}")
                require((directory / available["url"]).is_file(),
                        f"Missing platform metadata: {available['url']}")
            for dependency in variant.get("dependencies", []):
                if dependency["group"] == GROUP:
                    require(dependency["version"]["requires"] == version,
                            f"Mismatched internal version: {artifact}")
                    target = base / dependency["module"] / version
                    require(target.is_dir(), f"Missing internal dependency: {target}")
                    if dependency["module"].startswith("core"):
                        core_dependencies.append(dependency)
        if library == "charts":
            require(core_dependencies, f"No transitive core dependency in {artifact}")
        print(f"Verified {GROUP}:{artifact}:{version}")

print("Complete publication verified: both modules, shared metadata, Android, desktop, and three iOS targets.")

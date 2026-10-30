# Changelog

## 0.2.0-rc.2 — candidate under verification

Candidate prepared on 2026-10-30. This is not a stable release; final remote verification is pending.

This candidate includes the required JitPack metadata workaround, source-download checks, public consumer tests, and gated release automation.

### Added

- Regression coverage for chart math, geometry, and desktop Compose interaction.
- Android and desktop public API baselines.
- JitPack publication verification for both modules and their platform artifacts.
- GitHub Actions checks for code quality, tests, platform builds, publication, and API documentation.
- README examples, a rendered chart preview, and a GitHub Pages documentation workflow.

### Fixed

- Axis tick generation for extreme numeric ranges.
- Stacked-bar ranges, hidden-series identity, and point selection indices.
- Stale interaction callbacks and selection state after data changes.
- Percent-stacked calculations with invalid values and pie normalization overflow.
- Negative color palette tokens and animation restarts after configuration/data changes.

### Changed

- Publishing identity now matches `meetkparmar/KomposeCharts`.
- `charts` exposes `core` transitively, so consumers need only one dependency.
- Publishing uses JDK 21 without a vendor restriction; JVM bytecode remains Java 17.

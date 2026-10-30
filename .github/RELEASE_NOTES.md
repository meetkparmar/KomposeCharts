KomposeCharts provides bar, line, pie, and donut charts for Compose Multiplatform applications on Android, iOS, and desktop.

### Installation

Follow [INSTALLATION.md](INSTALLATION.md) for the dependency and the **required JitPack metadata workaround**. The dependency line alone is insufficient for shared Kotlin metadata compilation and source downloads.

### Changes

- Fixed extreme axis ranges, stacked-bar bounds, percent calculations, and pie normalization.
- Corrected hidden-series identity, point selection, stale callbacks, and animation updates.
- Exposed core data models transitively through the charts module.
- Added API compatibility checks, regression tests, and release validation against downloaded artifacts.

The release workflow requires passing CI, artifact/source checksum verification, clean downloaded Android/desktop/iOS consumers, shared metadata compilation, and public interaction tests before publishing these notes. Physical-device coverage is not claimed.

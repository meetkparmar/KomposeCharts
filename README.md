# KomposeCharts

A Kotlin Multiplatform + Compose Multiplatform chart library — beautiful, animated, and fully customizable charts across Android, iOS, and Desktop.

## Platforms

| Platform | Status |
|---|---|
| Android (minSdk 24) | Supported |
| iOS (16+) | Supported |
| Desktop / JVM (Mac/Windows) | Supported |

## Modules

| Module | Description |
|---|---|
| `:core` | Pure Kotlin — data models, axis math, animation config. Zero Compose dependency. |
| `:charts` | Compose Multiplatform chart composables (rendering, theming, animations). |
| `:samples:common` | Shared interactive chart demos. |
| `:samples:android` | Android sample host. |
| `:samples:desktop` | Desktop JVM sample host. |
| `:samples:ios` | Kotlin framework for the SwiftUI sample host. |

## Status

Under active development. The configured library version is `0.2.0`; this is not a claim of a published release.

See [project documentation](documents/README.md) for architecture, setup, and validation results.

Local consumers can depend on `:charts`; its public `:core` data types are exposed transitively.

## License

```
Copyright 2026 KomposeCharts Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

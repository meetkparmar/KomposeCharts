# Contributing to KomposeCharts

## Set up

1. Clone this repository and install JDK 21.
2. Install Android SDK platform 35 and build tools 34.0.0. Configure `ANDROID_HOME` or an untracked `local.properties` file with `sdk.dir`.
3. Open the project in Android Studio or IntelliJ and let Gradle sync.
4. Run `./gradlew :samples:desktop:run` to explore the charts.

For iOS, use macOS with Xcode and its simulator SDK installed. Open `samples/ios/iosApp/iosApp.xcodeproj` and select the `iosApp` scheme. The checked-in toolchain uses Kotlin 2.1.20, Compose 1.8.0, and Gradle 8.11.1; avoid unrelated upgrades in a bug-fix PR.

## Where changes belong

- `core`: data models, chart math, and non-Compose utilities.
- `charts`: composables, drawing, interaction, styles, and themes.
- `samples/common`: shared examples; platform sample modules host them.
- `documents/`: local project notes. This folder is currently ignored by Git; public guidance belongs in the README and this guide.

## Before opening a pull request

Run the checks relevant to your change:

```sh
./gradlew :core:desktopTest :charts:desktopTest \
  :core:detekt :charts:detekt :charts:apiCheck \
  :samples:android:assembleDebug :samples:android:assembleRelease \
  :samples:desktop:desktopJar
```

On headless Linux, prefix the command with `xvfb-run -a` for Compose interaction tests. For documentation changes, run `./gradlew :core:dokkaHtml :charts:dokkaHtml`.

The API baselines under `charts/api/` must be committed. If you intentionally change public APIs, run `./gradlew :charts:apiDump`, review the baseline diff, and explain the compatibility impact. Do not regenerate baselines just to make a failing check pass.

For publishing changes, run:

```sh
bash scripts/publish-jitpack.sh "$PWD/build/publication-check"
```

This creates and verifies local artifacts; it does not publish a public release. CI separately checks the Linux publishing path and an unsigned iOS simulator build. There are no signing credentials needed for these checks.

## Pull requests and bug reports

Keep changes focused, explain the problem and resulting behavior, and state what you tested. Include a regression test when fixing behavior that can be reproduced automatically. Attach screenshots for visual changes.

For a bug report, include the library version, Kotlin/Compose versions, platform, minimal input data, reproduction steps, and expected/actual behavior. Remove credentials and private data from logs.

## Documentation hosting

The `API documentation` workflow builds both Dokka sites and deploys them through GitHub Pages on a published release or a manual run from the default branch. A manual run documents the development branch; release runs document the tagged source. It does not publish SDK artifacts.

GitHub Pages was configured with **GitHub Actions** as its publishing source on 2026-10-21. After pushing the workflow, run it from the Actions tab. The `github-pages` environment must permit the default branch and release tags. Until the first deployment succeeds, the README marks the documentation address as planned.

## License

Contributions are provided under the repository's [Apache 2.0 license](LICENSE). Preserve existing copyright and attribution notices.

"""Assemble generated Dokka sites without mixing their assets or search indexes."""

import html
import os
from pathlib import Path
import shutil

root = Path(__file__).resolve().parent.parent
destination = root / "build/docs-site"
for module in ("charts", "core"):
    source = root / module / "build/dokka/html"
    if not (source / "index.html").is_file():
        raise SystemExit(f"Generate {module} documentation with :{module}:dokkaHtml first")

if destination.exists():
    shutil.rmtree(destination)
destination.mkdir(parents=True)
for module in ("charts", "core"):
    shutil.copytree(root / module / "build/dokka/html", destination / module)

ref = html.escape(os.environ.get("DOCS_REF", "local development"))
sha = html.escape(os.environ.get("DOCS_SHA", "local working tree"))
(destination / "index.html").write_text(f"""<!doctype html>
<html lang="en"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>KomposeCharts API reference</title>
<style>body{{font:18px/1.6 system-ui,sans-serif;max-width:760px;margin:64px auto;padding:0 24px;color:#18233a}}a{{color:#075fa8}}code{{overflow-wrap:anywhere}}</style>
</head><body><h1>KomposeCharts API reference</h1>
<p>Bar, line, pie, and donut charts for Compose Multiplatform.</p>
<p>Source: <strong>{ref}</strong><br>Commit: <code>{sha}</code></p>
<p>This reference describes the source above. Documentation availability does not imply a published SDK release.</p>
<ul><li><a href="charts/index.html">Charts: composables, styles, interaction, and themes</a></li>
<li><a href="core/index.html">Core: data models, animation configuration, and chart math</a></li></ul>
<p><a href="https://github.com/meetkparmar/KomposeCharts">Installation and examples on GitHub</a></p>
</body></html>
""", encoding="utf-8")
(destination / ".nojekyll").touch()
print(f"Documentation site prepared at {destination}")

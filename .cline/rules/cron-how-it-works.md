# Cline Schedule: How It Works & Lessons Learned

## The Intended (Easy) Way

Cline's cron system works via **file-based auto-discovery**:

```
1. You create a .md file in ~/.cline/cron/
2. Hub daemon detects it → auto-creates a record in cron.db
3. If YAML frontmatter is correct, it parses fields (schedule_expr, timeout, etc.)
4. Next run is calculated → schedule is live
```

Or via CLI (one command):
```bash
cline schedule create "FinanControl Sprint Workflow" \
  --cron "0 5 * * *" \
  --timeout 1800 \
  --retries 3 \
  --workspace /path/to/FinanControl \
  --prompt-file /path/to/prompt.md
```

## What Actually Happened

| Step | Intended | What Happened |
|------|----------|---------------|
| **CLI** | `cline schedule create ...` | ❌ Blocked by port 25463 conflict with running hub daemon |
| **File auto-discovery** | Create `.md` → daemon parses YAML → live | ⚠️ Daemon detected file but created a **placeholder** (`enabled=0, schedule_expr=None`) instead of parsing the YAML frontmatter |
| **Workaround** | — | ✅ Direct SQLite insert into `~/.cline/data/db/cron.db` → `cron_specs` table with all fields filled |

## Root Cause

The hub daemon created a partial record from the file but didn't extract YAML frontmatter fields. This suggests either:
1. Cline expects a **different YAML key naming** than what we used
2. The daemon only does full parsing on the first scan, not on re-scans after the DB entry exists
3. The YAML frontmatter parser has specific format requirements

## Why the DB Record Works

Once a properly-filled record exists in `cron_specs`:
- The hub daemon calculates `next_run_at` automatically
- The schedule fires on time (`0 5 * * *` → 5am local)
- Retries are honored via `extensions_json: {"retries": 3}`
- `timeout_seconds` enforces hard stop

## For Next Time (Cleaner Path)

1. **Restart hub daemon to free port 25463**, then use `cline schedule create`
2. **OR**: Investigate the exact YAML frontmatter format Cline expects (check `cline schedule create --help` or daemon logs at `~/.cline/data/logs/hub-daemon.log`)
3. **OR**: Use the file+DB hybrid approach used here (file for source tracking, DB for config)

## Current Schedule Config

```yaml
spec_id: 5c30e25f-ddc5-4558-999a-ae195a54d91b
title: FinanControl Sprint Workflow
schedule: 0 5 * * * (daily at 5am America/La_Paz)
timeout: 1800s (30 min)
retries: 3
workspace: /home/locotito-linux/AndroidStudioProjects/FinanControl
next_run: 2026-07-01T09:00:00.000Z (tomorrow 5am)
```

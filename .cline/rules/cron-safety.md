# CRON SAFETY RULES

- 🔴 This task runs on a **30-minute hard timeout** — you MUST exit within it
- 🟡 If you cannot finish, **stop early** and report what's remaining
- 🟢 Only work on **one ticket at a time** (the highest-priority unstarted)
- ❌ Never force-push (`git push --force`) or hard-reset (`git reset --hard`)
- ❌ Never install packages or dependencies
- ❌ Never make external network requests beyond the repo's configured remotes
- 📝 Always create a branch (`feature/CHRIS-XXX`) — never commit to `main`
- 🧪 Always run `./gradlew testDebugUnitTest` before creating a PR
- 📊 Log what was done and what remains in the task summary

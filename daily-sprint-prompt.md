Work through this systematically, validating at each checkpoint before
moving to the next step.

## 1. Sprint Backlog → Task Details
- Find the active sprint for project CHRIS
- Get the highest-priority unstarted ticket (by rank/priority field)
- Read full ticket details: key, summary, description, acceptance criteria,
  linked issues, attachments
- Summarize what needs to be built/tested

## 2. Branch Setup
- `git checkout main && git pull`
- Create `feature/CHRIS-XXX` branch
- Read existing AI/ docs and .clinerules to understand current conventions

## 3. Implementation
Build the solution per ticket requirements. Group work into focused,
reviewable commits following conventional commits format:
`type(scope): message`

Suggested commit groups:
- CHRIS-XXX test: Add Page Object Model + unit tests (MockK)
- CHRIS-XXX feat: Core implementation
- CHRIS-XXX test: Integration/edge case tests
- CHRIS-XXX chore: Update AI/ docs, .clinerules

## 4. Testing & Validation
- Run `./gradlew app:testDebugUnitTest` — fix any failures
- Run `./gradlew app:assembleDebug` — fix any build errors
- All tests must pass before PR

## 5. PR via GitHub MCP
Create a PR with:
- Title: Exact Jira summary (prefixed with CHRIS-XXX:)
- Body: Include ticket link, summary of changes per commit group, and
  test coverage notes

## 6. Retrospective
Document learnings from this session:
- What build/test friction did we hit? How can we fix it?
- What was missing from .clinerules or AI/ docs?
- What would speed up the next CHRIS-XXX task?

## 7. Move the jira ticket to "in review" using jira mcp

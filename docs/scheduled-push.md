# Scheduled pushes

Run `python3 scripts/scheduled-push.py automation/changes.json` from the project.
The runner waits 15 minutes before every attempt. Keep the process running in a
terminal or a persistent session; stopping it stops the timer. Restart the same
command to resume progress. Git authentication must work without an interactive
prompt. Pushes use normal fast-forward rules; the runner never force-pushes.

First review and commit the initial project, including `.gitignore`. This project
currently has no baseline commit. Review files for credentials before committing.
The runner does not create that initial commit or invent product changes.

Add reviewed, independent patches to `automation/patches/` and list them in
`automation/changes.json`. Paths are relative to the queue file:

```json
[
  {
    "patch": "patches/fix-documentation.patch",
    "message": "Clarify local setup instructions",
    "checks": [["python3", "scripts/check-architecture.py"]]
  }
]
```

Choose checks appropriate to each actual change. For backend changes use
`["mvn", "-B", "test"]`; frontend checks may include `["npm", "--prefix",
"frontend", "ci"]`, followed by frontend tests and a build. Dependencies must be
installed inside each isolated worktree, or provided by the environment. Commands
are argument arrays, not shell expressions. Only run trusted queues and patches.
No additional Python packages are required; Git and Python 3 are required.

Each patch is applied to the last successfully pushed revision in a temporary
worktree. Tests run there before committing only the staged patch. Failed changes
are logged and skipped. A failed push retains its commit and retries at the next
interval before advancing. This prevents losing or duplicating commits on a
network failure. Choose an existing reviewed baseline that can fast-forward the
remote branch. The local working branch is not advanced by this runner.

Progress, push counts, and timestamped logs are stored in `.scheduled-push/`
(ignored by Git). Keep these files when restarting. To start another queue,
stop the runner and archive that directory first. A lock prevents concurrent
runners in this checkout. A pending commit is protected by a local Git ref.
Check the completion log for skipped changes; completion does not mean skipped
changes were pushed.

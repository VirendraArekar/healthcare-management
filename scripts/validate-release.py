import json,re,sys
from pathlib import Path
path=Path(sys.argv[1]).resolve()
root=(Path.cwd()/"releases").resolve()
if not path.is_relative_to(root):raise SystemExit("Release file must be under releases/")
data=json.loads(path.read_text())
required={"frontend","api-gateway","auth-service","patient-service","staff-service","appointment-service","medical-record-service","lab-service","pharmacy-service","billing-service","notification-service","reporting-service"}
assert set(data["images"])==required,"All images must be specified"
for name,image in data["images"].items():assert re.fullmatch(r"[a-zA-Z0-9./:_-]+@sha256:[0-9a-f]{64}",image),f"Invalid immutable digest for {name}"
print("All 12 application images use immutable digests. Verify approval evidence separately.")

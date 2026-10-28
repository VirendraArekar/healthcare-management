#!/usr/bin/env python3
"""Build, scan and publish a reviewed release. Run only in a protected authenticated CI job."""
import argparse,json,re,subprocess
from pathlib import Path
parser=argparse.ArgumentParser();parser.add_argument('--registry',required=True);parser.add_argument('--version',required=True);args=parser.parse_args()
if not re.fullmatch(r'[a-z0-9][a-z0-9./_-]+',args.registry):parser.error('Use a lowercase registry repository prefix')
if not re.fullmatch(r'[a-zA-Z0-9][a-zA-Z0-9_.-]{0,127}',args.version):parser.error('Invalid image tag')
root=Path(__file__).resolve().parents[1];out=root/'releases'/args.version;out.mkdir(parents=True,exist_ok=False)
services=['frontend','api-gateway','auth-service','patient-service','staff-service','appointment-service','medical-record-service','lab-service','pharmacy-service','billing-service','notification-service','reporting-service']
def run(*argv):return subprocess.check_output(argv,cwd=root,text=True).strip()
images={};source=run('git','rev-parse','HEAD')
# Scan every built image before publishing any image.
for service in services:
 dockerfile='frontend/Dockerfile' if service=='frontend' else f'backend/{service}/Dockerfile'
 image=f'{args.registry}/{service}:{args.version}'
 subprocess.run(['docker','build','--pull','--label',f'org.opencontainers.image.revision={source}','-f',dockerfile,'-t',image,'.'],cwd=root,check=True)
 subprocess.run(['trivy','image','--severity','HIGH,CRITICAL','--exit-code','1',image],cwd=root,check=True)
 subprocess.run(['trivy','image','--format','cyclonedx','--output',str(out/(service+'.cdx.json')),image],cwd=root,check=True)
 images[service]=image
for service,image in images.items():
 subprocess.run(['docker','push',image],cwd=root,check=True)
 digests=json.loads(run('docker','image','inspect','--format','{{json .RepoDigests}}',image))
 prefix=args.registry+'/'+service+'@sha256:'
 images[service]=next(d for d in digests if d.startswith(prefix))
(out/'values.json').write_text(json.dumps({'images':images},indent=2)+'\n')
(out/'metadata.json').write_text(json.dumps({'version':args.version,'sourceCommit':source,'images':images,'sbomFormat':'CycloneDX','promotion':'Use identical digests across environments; protected approvals required.'},indent=2)+'\n')
subprocess.run(['python3','scripts/validate-release.py',str(out/'values.json')],cwd=root,check=True)
print('Published scanned immutable release:',out.relative_to(root))

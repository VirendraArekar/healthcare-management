#!/usr/bin/env python3
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
errors=[]
for file in (root/'backend').rglob('src/main/java/**/*.java'):
 text=file.read_text()
 if any(part in file.parts for part in ['controller','service','event','scheduler']):
  if 'JdbcTemplate' in text or 'EntityManager' in text or re.search(r'"(?:select |insert into |update [a-z_]+ set )',text,re.I):
   # Outbox/event infrastructure persistence is intentionally isolated in repositories too.
   if file.name!='EventPublisher.java':errors.append(str(file.relative_to(root))+': persistence belongs in repository')
 if 'controller' in file.parts and ('@Transactional' in text or 'new BCrypt' in text):errors.append(str(file.relative_to(root))+': controller owns business work')
 if 'service' in file.parts and re.search(r'@(Get|Post|Put|Patch|Delete|Request)Mapping',text):errors.append(str(file.relative_to(root))+': service owns HTTP mapping')
for folder in ['pages','components','store','api','routes','hooks','types','config']:
 if not (root/'frontend/src'/folder).is_dir():errors.append('Missing frontend folder '+folder)
if errors:raise SystemExit('\n'.join(errors))
print('PASS: controller/service/repository boundaries and frontend structure')

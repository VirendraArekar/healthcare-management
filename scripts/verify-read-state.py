#!/usr/bin/env python3
"""Regression: reading a notification is scoped to one user. Synthetic seeded local stack only."""
import os,json,urllib.request
base=os.environ.get('BASE_URL','http://localhost:3000')
if not base.startswith(('http://localhost:','http://127.0.0.1:')):raise SystemExit('Local demo only')
def call(method,path,body=None,token=None):
 headers={'Content-Type':'application/json'}
 if token:headers['Authorization']='Bearer '+token
 r=urllib.request.Request(base+'/api'+path,method=method,headers=headers,data=json.dumps(body).encode()if body is not None else None)
 with urllib.request.urlopen(r,timeout=30)as response:return json.load(response)
def login(role):return call('POST','/auth/login',{'email':role+'@demo.hospital','password':os.environ['DEMO_PASSWORD']})['accessToken']
a=login('admin');p=login('patient');admin=call('GET','/notifications',token=a)['content'];patient=call('GET','/notifications',token=p)['content'];patient_ids={row['id']for row in patient if not row['read']}
notice=next(row['id']for row in admin if row['id']in patient_ids)
call('POST','/notifications/'+notice+'/read',{},a)
assert not next(row for row in call('GET','/notifications',token=p)['content']if row['id']==notice)['read']
call('POST','/notifications/'+notice+'/read',{},p)
assert next(row for row in call('GET','/notifications',token=p)['content']if row['id']==notice)['read']
print('PASS notification read state is isolated between administrator and patient')

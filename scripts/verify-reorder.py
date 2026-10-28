#!/usr/bin/env python3
"""Regression: catalogue reorder levels must drive inventory reports. Synthetic local data only."""
import os,json,urllib.request,uuid
base=os.environ.get('BASE_URL','http://localhost:3000');auth=os.environ.get('AUTH_BASE_URL',base)
if not all(url.startswith(('http://localhost:','http://127.0.0.1:'))for url in [base,auth]):raise SystemExit('Local demo only')
def call(origin,method,path,body=None,token=None):
 headers={'Content-Type':'application/json'}
 if token:headers['Authorization']='Bearer '+token
 request=urllib.request.Request(origin+'/api'+path,method=method,headers=headers,data=json.dumps(body).encode()if body is not None else None)
 with urllib.request.urlopen(request,timeout=30)as response:return json.load(response)
token=call(auth,'POST','/auth/login',{'email':'pharmacist@demo.hospital','password':os.environ['DEMO_PASSWORD']})['accessToken']
name='Synthetic threshold regression '+str(uuid.uuid4())[:8]
call(base,'POST','/pharmacy/catalog',{'name':name,'strength':'1mg','unit':'tablet','reorderLevel':30},token)
batch=call(base,'POST','/pharmacy',{'medicine':name,'quantity':25,'expiresOn':'2030-01-01'},token)
alerts=call(base,'GET','/pharmacy/alerts',token=token)['content']
assert any(row['id']==batch['id']for row in alerts),'Quantity 25 must be reported below configured reorder level 30'
print('PASS configurable reorder threshold: stock 25 triggers configured level 30')

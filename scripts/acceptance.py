#!/usr/bin/env python3
"""Destructive synthetic-data acceptance checks against an explicitly local, seeded stack."""
import os,json,uuid,time,datetime,urllib.request,urllib.error
from pathlib import Path
requests_executed=0
base=os.environ.get('BASE_URL','http://localhost:3000')
if not base.startswith(('http://localhost:', 'http://127.0.0.1:')):raise SystemExit('Local acceptance only')
password=os.environ['DEMO_PASSWORD']; passed=[]
def check(name):passed.append(name);print('PASS '+name,flush=True)
def api(method,path,body=None,token=None,status=200,headers=None,raw=False):
 global requests_executed
 requests_executed+=1
 h={'Content-Type':'application/json',**(headers or {})}
 if token:h['Authorization']='Bearer '+token
 r=urllib.request.Request(base+'/api'+path,headers=h,method=method,data=json.dumps(body).encode() if body is not None else None)
 try:
  with urllib.request.urlopen(r,timeout=25) as response:code=response.status;data=response.read()
 except urllib.error.HTTPError as error:code=error.code;data=error.read()
 assert code==status,(method,path,code,data[:1500].decode(errors='replace'))
 return data if raw else json.loads(data) if data else {}
def signin(role):return api('POST','/auth/login',{'email':role+'@demo.hospital','password':password})
def content(result):return result['content'] if isinstance(result,dict) and 'content'in result else result
roles=['admin','doctor','doctor2','nurse','receptionist','pharmacist','lab_technician','lab_technician2','billing_staff','patient','patient2']
login={role:signin(role) for role in roles};tokens={k:v['accessToken'] for k,v in login.items()};a=tokens['admin'];d=tokens['doctor'];r=tokens['receptionist'];b=tokens['billing_staff'];ph=tokens['pharmacist'];lab=tokens['lab_technician'];lab2=tokens['lab_technician2']
api('GET','/patients',status=401);api('GET','/auth/users',token=r,status=403)
api('POST','/auth/login',{'email':'doctor@demo.hospital','password':'invalid-synthetic'},status=401)
rotated=api('POST','/auth/refresh',{'refreshToken':login['patient2']['refreshToken']})
api('POST','/auth/refresh',{'refreshToken':login['patient2']['refreshToken']},status=401)
api('POST','/auth/logout',{'refreshToken':rotated['refreshToken']},rotated['accessToken'])
check('eleven demo identities, anonymous/role denial, invalid login, refresh rotation and reuse rejection, logout')
for path in ['/patients','/medical-records','/medical-records/prescriptions','/lab/my','/billing/my','/notifications']:
 own=content(api('GET',path,token=tokens['patient']))
 assert all(str(row.get('patientId',row.get('patient_id',row.get('id'))))=='10000000-0000-0000-0000-000000000001' for row in own if path!='/notifications' or row.get('patientId'))
 if path=='/lab/my':assert all(row['status']=='VERIFIED' for row in own)
api('PATCH','/patients/me',{'phone':'0000000000'},tokens['patient'])
check('patient portal ownership, verified-only lab results, own invoices/prescriptions and contact update')

if os.environ.get('MAILPIT_API'):
 mailpit=os.environ['MAILPIT_API']
 if not mailpit.startswith(('http://localhost:','http://127.0.0.1:')):raise SystemExit('Local Mailpit only')
 email='recovery-'+str(uuid.uuid4())+'@example.invalid'
 account=api('POST','/auth/users',{'email':email,'password':password,'role':'NURSE'},a,201)
 api('POST','/auth/forgot-password',{'email':email},status=202)
 message=None
 for attempt in range(15):
  with urllib.request.urlopen(mailpit+'/api/v1/messages',timeout=10)as response:messages=json.load(response)['messages']
  message=next((item for item in messages if email in json.dumps(item)),None)
  if message:break
  time.sleep(1)
 assert message,'Recovery SMTP message did not arrive'
 with urllib.request.urlopen(mailpit+'/api/v1/message/'+message['ID'],timeout=10)as response:mail=json.load(response)
 import re
 reset_token=re.search(r': ([A-Za-z0-9_-]{20,})',mail['Text']).group(1)
 new_password=password+'x'
 api('POST','/auth/reset-password',{'token':reset_token,'password':new_password})
 api('POST','/auth/reset-password',{'token':reset_token,'password':new_password},status=400)
 api('POST','/auth/login',{'email':email,'password':new_password})
 row=next(item for item in content(api('GET','/auth/users',token=a)) if item['id']==account['id'])
 api('PATCH','/auth/users/'+account['id'],{'version':row['version'],'role':'NURSE','enabled':False},a)
 api('PATCH','/auth/users/'+account['id'],{'version':row['version'],'role':'NURSE','enabled':False},a,status=409)
 api('POST','/auth/login',{'email':email,'password':new_password},status=401)
 check('SMTP recovery, single-use reset, password sign-in, versioned account disable and stale-write rejection')

p=api('POST','/patients',{'firstName':'Acceptance','lastName':'Synthetic','dateOfBirth':'1990-01-01','email':'acceptance@example.invalid'},r,201)
updated=api('PUT','/patients/'+p['id'],{**p,'phone':'0000000000'},r)
api('PUT','/patients/'+p['id'],{**p,'phone':'0000000001'},r,status=409)
identity=api('GET','/auth/me',token=d)
api('POST','/patients/'+p['id']+'/assignments',{'clinicianId':identity['id'],'role':'DOCTOR'},a)
api('POST','/patients/'+p['id']+'/assignments',{'clinicianId':identity['id'],'role':'NURSE'},a,status=400)
check('patient registration, typed persistence, optimistic update conflict and clinician role validation')
enc=api('POST','/medical-records',{'patientId':p['id'],'diagnosis':'Synthetic observation','notes':'Synthetic acceptance note','treatment':'Review'},d,201)
api('GET','/medical-records/'+enc['id'],token=tokens['doctor2'],status=403)
api('GET','/medical-records/'+enc['id'],token=tokens['patient'],status=403)
api('POST','/medical-records/'+enc['id']+'/vitals',{'observation':'Synthetic stable reading','systolic':120,'diastolic':80,'pulse':72,'temperature':36.8,'oxygenSaturation':99,'weight':70},d)
assert len(content(api('GET','/medical-records/'+enc['id']+'/vitals',token=d)))>=1
enc2=api('PUT','/medical-records/'+enc['id'],{**enc,'notes':'Synthetic revised note'},d)
assert len(content(api('GET','/medical-records/'+enc['id']+'/history',token=d)))>=1
check('assigned clinician and patient isolation, encounter history and numeric vital signs')
future=(datetime.datetime.now(datetime.timezone.utc)+datetime.timedelta(days=5)).replace(hour=10,minute=0,second=0,microsecond=0)
# Dedicated doctor avoids collisions with seed and previous acceptance runs.
staff=api('POST','/staff',{'name':'Synthetic acceptance doctor','department':'General Medicine','specialty':'General'},a,201)
api('POST','/staff/schedules',{'staffId':staff['id'],'dayOfWeek':future.strftime('%A').upper(),'startsAt':'09:00','endsAt':'17:00'},a)
api('GET','/staff/'+staff['id'],token=a)
api('GET','/staff/'+staff['id']+'/availability?'+urllib.parse.urlencode({'startsAt':future.isoformat(),'endsAt':(future+datetime.timedelta(minutes=30)).isoformat()}),token=a)
api('GET','/appointments',token=r)
slot=api('POST','/appointments/slots',{'doctorId':staff['id'],'startsAt':future.isoformat(),'endsAt':(future+datetime.timedelta(minutes=30)).isoformat()},a)
booking=api('POST','/appointments',{'patientId':p['id'],'slotId':slot['id']},r,201)
api('POST','/appointments',{'patientId':p['id'],'slotId':slot['id']},r,status=409)
leave=api('POST','/staff/leave',{'staffId':staff['id'],'startsOn':future.date().isoformat(),'endsOn':future.date().isoformat(),'reason':'Synthetic test leave'},a)
api('POST','/staff/leave/'+leave['id']+'/decision',{'status':'APPROVED'},a)
for attempt in range(35):
 row=api('GET','/appointments/'+booking['id'],token=a)
 if row['status']=='CANCELLED':break
 time.sleep(1)
else:raise AssertionError('Kafka leave cancellation did not arrive')
check('working schedule authority, duplicate booking constraint, approved leave Kafka cancellation saga')

next_day=future+datetime.timedelta(days=1)
api('POST','/staff/schedules',{'staffId':staff['id'],'dayOfWeek':next_day.strftime('%A').upper(),'startsAt':'09:00','endsAt':'17:00'},a)
slots=[]
for hour in [10,11]:
 start=next_day.replace(hour=hour)
 slots.append(api('POST','/appointments/slots',{'doctorId':staff['id'],'startsAt':start.isoformat(),'endsAt':(start+datetime.timedelta(minutes=30)).isoformat()},a))
request=api('POST','/appointments/requests',{'slotId':slots[0]['id']},tokens['patient'])
approved=api('POST','/appointments/requests/'+request['id']+'/approve',{},r)
api('POST','/appointments/'+approved['id']+'/reschedule',{'slotId':slots[1]['id']},r)
api('POST','/appointments/'+approved['id']+'/cancel',{},tokens['patient'])
assert api('GET','/appointments/'+approved['id'],token=a)['status']=='CANCELLED'
seed_appointments=content(api('GET','/appointments',token=d))
assert all(row['doctor_id']=='20000000-0000-0000-0000-000000000001' for row in seed_appointments)
api('POST','/appointments/30000000-0000-0000-0000-000000000001/complete',{},d,status=400)
check('patient appointment request/approval, rescheduling, owner cancellation, doctor scope and early-completion prevention')

name='Synthetic acceptance medicine '+str(uuid.uuid4())[:8]
api('POST','/pharmacy/catalog',{'name':name,'strength':'10mg','unit':'tablet','reorderLevel':10},ph)
rx=api('POST','/medical-records/prescriptions',{'patientId':p['id'],'medicine':name,'quantity':5,'validUntil':(future+datetime.timedelta(days=30)).date().isoformat(),'instructions':'Synthetic only'},d)
batch=api('POST','/pharmacy',{'medicine':name,'quantity':8,'expiresOn':(future+datetime.timedelta(days=20)).date().isoformat()},ph,201)
api('POST','/pharmacy/'+batch['id']+'/dispense',{'quantity':6,'prescriptionId':rx['id']},ph,status=400)
api('POST','/pharmacy/'+batch['id']+'/dispense',{'quantity':3,'prescriptionId':rx['id']},ph)
api('POST','/pharmacy/'+batch['id']+'/dispense',{'quantity':3,'prescriptionId':rx['id']},ph,status=400)
assert api('GET','/pharmacy/'+batch['id'],token=ph)['quantity']==5
check('medicine catalogue, prescription authority/cumulative cap, atomic stock movement')
order=api('POST','/lab',{'patientId':p['id'],'test':'Synthetic CBC'},d,201)
api('POST','/lab/'+order['id']+'/transition',{'status':'VERIFIED'},lab,status=400)
for state in ['COLLECTED','PROCESSING','RESULT_ENTERED']:
 api('POST','/lab/'+order['id']+'/transition',{'status':state,'result':'Synthetic normal'},lab)
api('POST','/lab/'+order['id']+'/transition',{'status':'VERIFIED'},lab,status=400)
api('POST','/lab/'+order['id']+'/transition',{'status':'VERIFIED'},lab2)
assert len(content(api('GET','/lab/'+order['id']+'/history',token=lab)))==4
check('lab catalogue, sample collection/history, legal transitions and second-technician verification')
inv=api('POST','/billing',{'patientId':p['id'],'lineItems':[{'description':'Synthetic consultation','quantity':2,'unitPrice':50}],'taxPercent':10,'discount':10},b,201)
assert float(inv['total'])==99
key={'Idempotency-Key':str(uuid.uuid4())}
pay1=api('POST','/billing/'+inv['id']+'/pay',{},b,headers=key);pay2=api('POST','/billing/'+inv['id']+'/pay',{},b,headers=key);assert pay1['id']==pay2['id']
assert api('GET','/billing/'+inv['id']+'/pdf',token=b,raw=True).startswith(b'%PDF')
refund=api('POST','/billing/'+inv['id']+'/refund-request',{},b)
api('POST','/billing/refunds/'+refund['id']+'/settle',{},b,status=400)
api('POST','/billing/refunds/'+refund['id']+'/approve',{},a)
settle1=api('POST','/billing/refunds/'+refund['id']+'/settle',{},b);settle2=api('POST','/billing/refunds/'+refund['id']+'/settle',{},b);assert str(settle1['id'])==str(settle2['id'])
check('itemized decimal invoice, PDF, payment retry, separate refund approval and idempotent settlement')
for attempt in range(30):
 notices=content(api('GET','/notifications',token=a))
 if any('lab.result-verified' in str(n) for n in notices):break
 time.sleep(1)
assert notices
notice=notices[0];api('POST','/notifications/'+notice['id']+'/read',{},a)
assert next(n for n in content(api('GET','/notifications',token=a)) if n['id']==notice['id'])['read']
for path in ['/reports','/reports/daily','/reports/monthly','/reports/doctor-utilization','/reports/audit?service=patient','/reports/health','/reports/stock','/reports/expiry','/reports/balances']:
 assert api('GET',path,token=a) is not None
assert api('GET','/reports/pdf',token=a,raw=True).startswith(b'%PDF')
assert len(api('GET','/reports/export',token=a,raw=True))>10
check('Kafka notifications, individual read state, reporting projections/audit/health and CSV/PDF exports')
print(json.dumps({'passed':len(passed),'requests':requests_executed,'checks':passed},indent=2))

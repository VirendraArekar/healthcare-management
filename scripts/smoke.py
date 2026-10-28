#!/usr/bin/env python3
"""Local API acceptance workflow. Requires running seeded stack and DEMO_PASSWORD."""
import json,os,time,uuid,urllib.request,urllib.error,datetime
base=os.environ.get('BASE_URL','http://localhost:3000')
if not base.startswith(('http://localhost:','http://127.0.0.1:')):raise SystemExit('Local smoke tests only')
password=os.environ['DEMO_PASSWORD']
def call(method,path,body=None,token=None,expected=200,headers=None):
 h={'Content-Type':'application/json',**(headers or {})}
 if token:h['Authorization']='Bearer '+token
 request=urllib.request.Request(base+'/api'+path,data=json.dumps(body).encode() if body is not None else None,headers=h,method=method)
 try:
  with urllib.request.urlopen(request,timeout=20) as r:status=r.status;data=r.read()
 except urllib.error.HTTPError as e:status=e.code;data=e.read()
 assert status==expected,(method,path,status,data)
 return json.loads(data) if data else {}
def login(role):return call('POST','/auth/login',{'email':role+'@demo.hospital','password':password})['accessToken']
admin=login('admin');reception=login('receptionist');pharmacist=login('pharmacist');billing=login('billing_staff')
call('GET','/patients',expected=401)
p=call('POST','/patients',{'firstName':'Smoke','lastName':'Synthetic','dateOfBirth':'1990-01-01','email':'smoke@example.invalid'},reception,201)
assert call('GET','/patients/'+p['id'],token=reception)['id']==p['id']
start=datetime.datetime.now(datetime.timezone.utc)+datetime.timedelta(days=3)
slot=call('POST','/appointments/slots',{'doctorId':'20000000-0000-0000-0000-000000000001','startsAt':start.isoformat(),'endsAt':(start+datetime.timedelta(minutes=30)).isoformat()},admin)
booking={'patientId':p['id'],'slotId':slot['id']}
a=call('POST','/appointments',booking,reception,201)
call('POST','/appointments',booking,reception,409)
for attempt in range(30):
 if 'appointment.booked.v1' in json.dumps(call('GET','/notifications',token=admin)):break
 time.sleep(2)
else:raise AssertionError('Notification projection did not arrive')
call('POST','/pharmacy',{'medicine':'No access','quantity':1,'expiresOn':'2030-01-01'},reception,403)
doctor=login('doctor')
identity=call('GET','/auth/me',token=doctor)
call('POST','/patients/'+p['id']+'/assignments',{'clinicianId':identity['id'],'role':'DOCTOR'},admin)
prescription=call('POST','/medical-records/prescriptions',{'patientId':p['id'],'medicine':'Synthetic smoke medicine','quantity':5,'validUntil':'2030-01-01'},doctor)
batch=call('POST','/pharmacy' ,{'medicine':'Synthetic smoke medicine','quantity':5,'expiresOn':'2030-01-01'},pharmacist,201)
call('POST','/pharmacy/'+batch['id']+'/dispense',{'quantity':6,'prescriptionId':prescription['id']},pharmacist,400)
invoice=call('POST','/billing',{'patientId':p['id'],'subtotal':'100','taxPercent':'10','discount':'10'},billing,201)
assert invoice['total']==99
headers={'Idempotency-Key':str(uuid.uuid4())}
first=call('POST','/billing/'+invoice['id']+'/pay',{},billing,headers=headers)
second=call('POST','/billing/'+invoice['id']+'/pay',{},billing,headers=headers)
assert first['id']==second['id']
call('POST','/appointments/'+a['id']+'/cancel',{},reception)
print('PASS: login, authorization, patient persistence, booking conflict, Kafka notification, stock invariant, invoice calculation, payment idempotency, cancellation')

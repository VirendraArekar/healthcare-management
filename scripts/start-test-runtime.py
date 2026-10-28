#!/usr/bin/env python3
"""Isolated functional integration harness; requires test databases, Kafka, Redis and Mailpit.
This harness is NOT production configuration and does not validate native Postgres concurrency.
"""
from pathlib import Path
import os,json,subprocess,time,urllib.request,sys,shutil
root=Path(__file__).resolve().parents[1]
state=Path(os.environ.get('HOSPITAL_TEST_RUNTIME',str(root.parent/'runtime'))).resolve()
state.mkdir(parents=True,exist_ok=True)
services=['auth','patient','staff','appointment','medical-record','lab','pharmacy','billing','notification','reporting']
values={}
for line in (root/'.env').read_text().splitlines():
 if line and not line.startswith('#') and '=' in line:
  key,value=line.split('=',1);values[key]=value
address=(state/'kafka-address.txt').read_text().strip()
app_base=int(os.environ.get('TEST_APP_PORT_BASE','18100'))
db_base=int(os.environ.get('TEST_DB_PORT_BASE','15432'))
gateway_port=int(os.environ.get('TEST_GATEWAY_PORT','18080'))
reference_base=int(os.environ.get('TEST_REFERENCE_PORT_BASE',str(app_base)))
urls={name.upper().replace('-','_')+'_URL':f'http://127.0.0.1:{reference_base+i}' for i,name in enumerate(services)}
for name in services:
 key=name.upper().replace('-','_')+'_URL'
 if os.environ.get('TEST_'+key):urls[key]=os.environ['TEST_'+key]
base={**os.environ,**urls,'SPRING_DATA_REDIS_DATABASE':'1','SPRING_FLYWAY_ENABLED':'false','SPRING_PROFILES_ACTIVE':'local','JWT_SECRET':values['JWT_SECRET'],'DEMO_PASSWORD':values['DEMO_PASSWORD'],'DEMO_ENABLED':'true','KAFKA_BOOTSTRAP_SERVERS':address,'REDIS_HOST':'127.0.0.1','SPRING_DATA_REDIS_PORT':'16379','MAIL_HOST':'127.0.0.1','MAIL_PORT':'11025','SERVER_ADDRESS':'127.0.0.1','OUTBOX_DELAY':'1500','SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE':'2','SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE':'0','SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT':'10000','SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_TIME_ZONE':'UTC'}
processes={}
for i,name in enumerate(services+['api-gateway']):
 if os.environ.get('TEST_ONLY_SERVICES') and name not in os.environ['TEST_ONLY_SERVICES'].split(','):continue
 artifact=name+'-service' if name!='api-gateway' else name
 port=app_base+i if name!='api-gateway' else gateway_port
 env={**base,'SERVER_PORT':str(port)}
 if name!='api-gateway':env.update({'DB_URL':f'jdbc:postgresql://127.0.0.1:{db_base+i}/postgres?sslmode=disable&gssEncMode=disable&prepareThreshold=0','DB_USER':'postgres','DB_PASSWORD':'postgres'})
 jar=root/'backend'/artifact/'target'/f'{artifact}-1.0.0.jar'
 if not jar.exists():raise SystemExit('Build application JARs first: mvn package')
 archive=state/'artifacts'/jar.name
 archive.parent.mkdir(parents=True,exist_ok=True)
 shutil.copy2(jar,archive)
 jar=archive
 log=open(state/f'{artifact}.log','w')
 process=subprocess.Popen(['java','-XX:ActiveProcessorCount=2','-Xms32m','-Xmx256m','-jar',str(jar)],env=env,stdout=log,stderr=subprocess.STDOUT,cwd=root)
 processes[name]={'pid':process.pid,'port':port}
 (state/'application-processes.json').write_text(json.dumps(processes,indent=2))
 for attempt in range(90):
  if process.poll() is not None:raise SystemExit(f'{artifact} stopped; inspect {state/artifact}.log')
  try:
   with urllib.request.urlopen(f'http://127.0.0.1:{port}/actuator/health/liveness',timeout=1) as r:
    if r.status==200:break
  except Exception:time.sleep(1)
 else:raise SystemExit(f'{artifact} startup timed out; inspect its log')
 print(f'{artifact}: healthy on {port}',flush=True)
print('HOSPITAL_APPLICATIONS_READY',flush=True)

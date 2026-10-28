import {readFile,readdir,writeFile,mkdir} from 'node:fs/promises';
import {createServer} from 'node:http';
import {randomUUID} from 'node:crypto';
import path from 'node:path';import {fileURLToPath} from 'node:url';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../..');
const runtime=path.resolve(process.env.HOSPITAL_TEST_RUNTIME??path.join(root,'../runtime'));const portBase=Number(process.env.TEST_DB_PORT_BASE??15432);const controlPort=Number(process.env.TEST_CONTROL_PORT??19090);await mkdir(runtime,{recursive:true});const controlToken=randomUUID();await writeFile(path.join(runtime,'database-control-token'),controlToken,{mode:0o600});
import {PGlite} from '@electric-sql/pglite';
import {btree_gist} from '@electric-sql/pglite/contrib/btree_gist';
import {PGLiteSocketServer} from '@electric-sql/pglite-socket';
const services=(process.env.TEST_SERVICES??'auth,patient,staff,appointment,medical-record,lab,pharmacy,billing,notification,reporting').split(',');
const running=[];
for(let index=0;index<services.length;index++){
 const service=services[index];const db=await PGlite.create({extensions:{btree_gist}});const port=portBase+index;
 const folder=path.join(root,'backend',service+'-service/src/main/resources/db/migration');const migrations=(await readdir(folder)).filter(f=>/^V[0-9]+__/.test(f)).sort((a,b)=>Number(a.match(/^V([0-9]+)/)[1])-Number(b.match(/^V([0-9]+)/)[1]));for(const migration of migrations)await db.exec(await readFile(path.join(folder,migration),'utf8'));
 const server=new PGLiteSocketServer({db,port,host:'127.0.0.1',maxConnections:8});await server.start();running.push({db,server,service});
 console.log(JSON.stringify({service,port,engine:(await db.query('select version()')).rows[0].version}));
}
const control=createServer(async(req,res)=>{if(req.headers.authorization!=='Bearer '+controlToken){res.writeHead(403);return res.end();}try{if(req.method==='POST'&&req.url==='/seed'){for(const entry of running){const file=path.join(root,'database/seed/V1__'+entry.service.replaceAll('-','_')+'.sql');try{await entry.db.exec(await readFile(file,'utf8'));}catch(error){if(error.code!=='ENOENT')throw error;}}res.setHeader('Content-Type','application/json');return res.end(JSON.stringify({seeded:running.map(r=>r.service)}));}if(req.method==='GET'&&req.url==='/verify'){const counts={};for(const entry of running){counts[entry.service]={outbox:(await entry.db.query('select count(*) from outbox')).rows[0].count};}res.setHeader('Content-Type','application/json');return res.end(JSON.stringify(counts));}res.writeHead(404);res.end();}catch(error){res.writeHead(500,{'Content-Type':'application/json'});res.end(JSON.stringify({detail:error.message}));}});await new Promise(resolve=>control.listen(controlPort,'127.0.0.1',resolve));console.log('HOSPITAL_DATABASES_READY');
async function stop(){control.close();for(const {db,server} of running){await server.stop();await db.close();}process.exit(0);}
process.on('SIGINT',stop);process.on('SIGTERM',stop);

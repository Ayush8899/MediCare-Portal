import React,{useEffect,useState} from 'react';
import api from '../api/axios';
export default function EnterpriseAdmin(){
 const [data,setData]=useState({hospitals:[],claims:[],labs:[],audits:[]});
 useEffect(()=>{Promise.all([api.get('/admin/enterprise/hospitals'),api.get('/admin/enterprise/insurance-claims'),api.get('/admin/enterprise/lab-orders'),api.get('/admin/enterprise/audit-logs')]).then(([h,c,l,a])=>setData({hospitals:h.data,claims:c.data,labs:l.data,audits:a.data})).catch(console.error)},[]);
 return <div><div style={{marginBottom:'2rem'}}><h1>Enterprise Hospital Operations</h1><p style={{color:'var(--text-muted)'}}>Hospital branches, compliance, laboratory, insurance and audit monitoring.</p></div><div className="grid-4"><div className="card"><h3>Hospitals</h3><b>{data.hospitals.length}</b></div><div className="card"><h3>Lab Orders</h3><b>{data.labs.length}</b></div><div className="card"><h3>Insurance Claims</h3><b>{data.claims.length}</b></div><div className="card"><h3>Audit Events</h3><b>{data.audits.length}</b></div></div><div className="card" style={{marginTop:'2rem'}}><h2>Enterprise Controls</h2><p>Hospital/branch management, consent tracking, laboratory workflows, insurance claims, billing records, audit logs and 2FA APIs are included as an extensible enterprise foundation.</p></div></div>
}

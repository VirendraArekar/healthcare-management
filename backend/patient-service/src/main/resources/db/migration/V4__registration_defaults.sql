UPDATE records SET data=jsonb_set(data,'{status}','"ACTIVE"'::jsonb),version=version+1 WHERE kind='patient' AND NOT data ? 'status';

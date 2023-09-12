CREATE
OR REPLACE FUNCTION truncate_tables() RETURNS void AS $$
DECLARE
statements CURSOR FOR
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
  and tablename not in (
                        'flyway_schema_history',
                        'spatial_ref_sys'
                       );

BEGIN
FOR stmt IN statements LOOP
        EXECUTE 'Truncate TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
END LOOP;
END;
$$
LANGUAGE plpgsql;

select truncate_tables();
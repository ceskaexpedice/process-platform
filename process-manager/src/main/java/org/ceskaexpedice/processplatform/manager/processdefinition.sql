
CREATE TABLE plugins (
                         plugin_id      VARCHAR(100) PRIMARY KEY,
                         description    TEXT,
                         main_class     VARCHAR(500),
                         payload_spec   JSONB NOT NULL  -- stores PayloadFieldSpecMap as JSON
);

CREATE TABLE profiles (
                          profile_id     VARCHAR(100) PRIMARY KEY,  -- meaningful ID used in UI
                          plugin_id      VARCHAR(100) NOT NULL REFERENCES plugins(plugin_id) ON DELETE CASCADE,
                          jvm_args       JSONB                      -- list of strings
);
INSERT INTO plugins (plugin_id, description, main_class, payload_spec)
VALUES (
           'plugin-abc',
           'Example Plugin',
           'com.example.Main',
           '{
             "inputPath": { "type": "string", "required": true },
             "enableFeature": { "type": "boolean", "required": false }
           }'
       );

INSERT INTO profiles (profile_id, plugin_id, jvm_args)
VALUES (
           'profile-fast-run',
           'plugin-abc',
           '["-Xmx1g", "-Denv=prod"]'
       );

processes Table
Column	        Type	    Description
id	            UUID (PK)	unique identifier
profile_id	    VARCHAR	    FK to plugin_profiles.profile_id
status	        ENUM	    e.g. PENDING, RUNNING, COMPLETED, FAILED
payload_json	JSONB	    runtime parameters (dynamic!)
scheduled_time	TIMESTAMP	scheduled time
started_at	    TIMESTAMP	when execution started
completed_at	TIMESTAMP	when execution completed

Example payload_json:
{
    "importDir": "/data/import",
    "addCollection": true
}


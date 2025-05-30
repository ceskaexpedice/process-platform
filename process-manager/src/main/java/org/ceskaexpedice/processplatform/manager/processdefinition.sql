CREATE TABLE process_definitions
(
    defid           VARCHAR(255) PRIMARY KEY,
    description     TEXT,
    main_class      TEXT,
    java_parameters TEXT,
    standard_os     TEXT,
    err_os          TEXT,
    secured_action  TEXT
);

plugin_profiles Table
Column	        Type	    Description
id	            UUID (PK)	unique identifier
plugin_id	    VARCHAR	    base plugin id (e.g. "import")
profile_id	    VARCHAR	    profile id (e.g. "import-cgi")
description	    VARCHAR	    optional human-readable desc (nullable)
static_params	JSONB	    static params from profile
jvm_args	    JSONB	    JVM args from profile
main_class	    VARCHAR	    the main class of plugin
created_at	    TIMESTAMP	record creation timestamp
updated_at	    TIMESTAMP	last update

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


CREATE TABLE pcp_profile
(
    profile_id TEXT PRIMARY KEY,
    description            TEXT,
    plugin_id  TEXT NOT NULL REFERENCES pcp_plugin (plugin_id) ON DELETE CASCADE,
    jvm_args   TEXT[]
);
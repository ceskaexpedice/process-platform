CREATE TABLE pcp_plugin
(
    plugin_id              TEXT PRIMARY KEY,
    description            TEXT,
    main_class             TEXT,
    payload_field_spec_map JSONB,
    scheduled_profiles     TEXT[]
);
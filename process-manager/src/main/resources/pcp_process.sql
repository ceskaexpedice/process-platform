CREATE TABLE pcp_process
(
    process_id     TEXT PRIMARY KEY,
    description    TEXT,
    profile_id     TEXT NOT NULL REFERENCES pcp_profile (profile_id) ON DELETE CASCADE,
    worker_id      TEXT REFERENCES pcp_node (node_id) ON DELETE CASCADE,
    pid            INTEGER,
    planned        TIMESTAMP,
    started        TIMESTAMP,
    finished       TIMESTAMP,
    status         INTEGER,
    payload        JSONB,
    batch_id       TEXT,
    batch_status   INTEGER,
    owner          TEXT
);

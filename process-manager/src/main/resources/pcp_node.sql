CREATE TABLE pcp_node
(
    node_id     TEXT PRIMARY KEY,
    description TEXT,
    type        TEXT,
    url         TEXT,
    tags        TEXT[]
);
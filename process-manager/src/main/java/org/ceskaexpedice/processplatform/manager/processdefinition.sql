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
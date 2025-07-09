INSERT INTO pcp_plugin (plugin_id,
                        description,
                        main_class,
                        payload_field_spec_map,
                        scheduled_profiles)
VALUES ('testPlugin1',
        'Testing plugin 1',
        'org.ceskaexpedice.processplatform.testplugin1.TestPlugin1',
        '{
          "name": {
            "type": "string",
            "required": true
          },
          "surname": {
            "type": "string",
            "required": true
          }
        }'::jsonb,
        ARRAY['testPlugin2']);
INSERT INTO pcp_plugin (plugin_id,
                        description,
                        main_class,
                        payload_field_spec_map,
                        scheduled_profiles)
  VALUES ('testPlugin2',
        'Testing plugin 2',
        'org.ceskaexpedice.processplatform.testplugin2.TestPlugin2',
        NULL,
        ARRAY['testPlugin3']);

INSERT INTO pcp_profile (profile_id,
                         description,
                         plugin_id,
                         jvm_args)
  VALUES ('testPlugin1-big',
          'Big profile',
        'testPlugin1',
        ARRAY ['-Xmx32g', '-Xms1g']);
INSERT INTO pcp_profile (profile_id,
                         description,
                         plugin_id,
                         jvm_args)
  VALUES ('testPlugin1-small',
          'Small profile',
        'testPlugin1',
        ARRAY ['-Xmx1g', '-Xms1g']);
INSERT INTO pcp_profile (profile_id,
                         description,
                         plugin_id,
                         jvm_args)
  VALUES ('testPlugin2-default',
          'Default profile',
        'testPlugin2',
        ARRAY ['-Xms1g']);
UPDATE tenant_with_uuid 
SET    configuration_with_uuid_id = (SELECT uuid 
                                     FROM   configuration_with_uuid 
                                     WHERE  id = configuration_id); 

UPDATE entity_with_uuid 
SET    tenant_with_uuid_id = (SELECT uuid 
                              FROM   tenant_with_uuid 
                              WHERE  id = tenant_id); 

UPDATE entity_with_uuid AS child 
SET    parent_with_uuid_id = (SELECT uuid 
                              FROM   entity_with_uuid AS parent 
                              WHERE  parent.id = child.parent_id);

UPDATE translation_small_with_uuid
SET    translation_id_with_uuid = (SELECT uuid
                                   FROM   translation_with_uuid
                                   WHERE  id = translation_id);

UPDATE translation_medium_with_uuid
SET    translation_id_with_uuid = (SELECT uuid
                                   FROM   translation_with_uuid
                                   WHERE  id = translation_id);
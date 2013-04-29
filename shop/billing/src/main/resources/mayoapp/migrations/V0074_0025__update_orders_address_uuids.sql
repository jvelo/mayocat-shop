UPDATE purchase_order_with_uuid
SET    delivery_address_id_with_uuid = (SELECT address_id_with_uuid
                                        FROM   address_with_uuid
                                        WHERE  address_id = delivery_address_id);

UPDATE purchase_order_with_uuid
SET    billing_address_id_with_uuid = (SELECT address_id_with_uuid
                                       FROM   address_with_uuid
                                       WHERE  address_id = billing_address_id);
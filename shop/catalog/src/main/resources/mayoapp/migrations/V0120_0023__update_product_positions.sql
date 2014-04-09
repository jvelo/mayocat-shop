---
--- Invert the position order of the products
---

CREATE FUNCTION update_positions() RETURNS void AS $$
DECLARE
    tenant RECORD;
    maxPos smallint;
BEGIN
    -- Iterate over each tenant
    FOR tenant IN (
        SELECT e.id
        FROM entity AS e
        JOIN tenant AS t ON t.entity_id = e.id
    ) LOOP
        -- For each tenant, save the maximum position of the products
        SELECT MAX(p.position) INTO maxPos
        FROM product AS p
        JOIN entity AS e ON p.entity_id = e.id
        WHERE e.tenant_id = tenant.id;

        -- Update the position of each product
        UPDATE product AS p
        SET position = maxPos - position + 1
        FROM entity AS e
        WHERE p.entity_id = e.id AND e.tenant_id = tenant.id;
    END LOOP;
END;
$$ LANGUAGE plpgsql;
SELECT update_positions();
DROP FUNCTION update_positions();

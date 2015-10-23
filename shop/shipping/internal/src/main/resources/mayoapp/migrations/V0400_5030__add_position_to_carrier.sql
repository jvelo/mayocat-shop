--
-- Adds a `position` column to the carrier table to make it possible to order carriers within a
-- tenant.
-- Let it nullable : by default and for already existing carriers, no order (thus position) is
-- defined, and they it will be set only when user orders carriers through API / back-office.
--

ALTER TABLE carrier ADD position smallint;
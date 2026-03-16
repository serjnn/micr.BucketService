ALTER TABLE bucket_item ADD CONSTRAINT unique_bucket_product UNIQUE (bucket_id, product_id);

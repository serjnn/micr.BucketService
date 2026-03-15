CREATE TABLE bucket_item (
    id SERIAL PRIMARY KEY,
    bucket_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL
);

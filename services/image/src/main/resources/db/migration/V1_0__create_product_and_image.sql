CREATE TABLE product (
    id UUID PRIMARY KEY
);

CREATE TABLE image (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);
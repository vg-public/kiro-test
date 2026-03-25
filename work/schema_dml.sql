-- ============================================================
--  ShopWire — Sample DML (INSERT)
--  Matches the 12 products shown in the HTML wireframe (app.js)
--  Run AFTER schema.sql
--  Rollback: schema_rollback.sql
-- ============================================================

BEGIN;

-- ============================================================
--  USERS  (1 admin/seller + 3 buyers)
-- ============================================================

INSERT INTO SW_USER (user_id, email, password_hash, first_name, last_name, phone, is_active, is_verified) VALUES
  ('00000000-0000-0000-0000-000000000001', 'seller@shopwire.dev',  '$2b$12$sellerHashPlaceholder111',  'Shop',  'Wire',   '555-000-0001', TRUE, TRUE),
  ('00000000-0000-0000-0000-000000000002', 'alice@example.com',    '$2b$12$aliceHashPlaceholder1111',  'Alice', 'Martin', '555-000-0002', TRUE, TRUE),
  ('00000000-0000-0000-0000-000000000003', 'bob@example.com',      '$2b$12$bobHashPlaceholder111111',  'Bob',   'Chen',   '555-000-0003', TRUE, TRUE),
  ('00000000-0000-0000-0000-000000000004', 'carol@example.com',    '$2b$12$carolHashPlaceholder11111', 'Carol', 'Davis',  '555-000-0004', TRUE, FALSE);

-- ============================================================
--  USER ADDRESSES
-- ============================================================

INSERT INTO SW_USER_ADDRESS (address_id, user_id, label, full_name, line1, city, state, postal_code, country, is_default) VALUES
  ('00000000-0000-0000-0001-000000000001', '00000000-0000-0000-0000-000000000002', 'Home', 'Alice Martin', '123 Maple St',   'Seattle',     'WA', '98101', 'US', TRUE),
  ('00000000-0000-0000-0001-000000000002', '00000000-0000-0000-0000-000000000002', 'Work', 'Alice Martin', '456 Pine Ave',   'Seattle',     'WA', '98102', 'US', FALSE),
  ('00000000-0000-0000-0001-000000000003', '00000000-0000-0000-0000-000000000003', 'Home', 'Bob Chen',     '789 Oak Blvd',   'Austin',      'TX', '73301', 'US', TRUE),
  ('00000000-0000-0000-0001-000000000004', '00000000-0000-0000-0000-000000000004', 'Home', 'Carol Davis',  '321 Elm Street', 'New York',    'NY', '10001', 'US', TRUE);

-- ============================================================
--  CATEGORIES  (parent → child tree)
-- ============================================================

INSERT INTO SW_CATEGORY (category_id, parent_id, name, slug, sort_order) VALUES
  (1,  NULL, 'Electronics',    'electronics',    1),
  (2,  NULL, 'Clothing',       'clothing',       2),
  (3,  NULL, 'Books',          'books',          3),
  (4,  NULL, 'Home & Garden',  'home-garden',    4),
  (5,  NULL, 'Sports',         'sports',         5),
  (6,  NULL, 'Health',         'health',         6),
  (7,  1,    'Laptops',        'laptops',        1),
  (8,  1,    'Smartphones',    'smartphones',    2),
  (9,  1,    'Headphones',     'headphones',     3),
  (10, 1,    'Keyboards',      'keyboards',      4),
  (11, 1,    'Gaming',         'gaming',         5),
  (12, 1,    'Cameras',        'cameras',        6),
  (13, 2,    'Footwear',       'footwear',       1),
  (14, 4,    'Office',         'office',         1),
  (15, 4,    'Candles',        'candles',        2),
  (16, 5,    'Fitness',        'fitness',        1),
  (17, 6,    'Skincare',       'skincare',       1);

-- Sync SERIAL sequence after explicit ID inserts
SELECT setval('sw_category_category_id_seq', (SELECT MAX(category_id) FROM SW_CATEGORY));

-- ============================================================
--  BRANDS
-- ============================================================

INSERT INTO SW_BRAND (brand_id, name, slug) VALUES
  (1,  'TechPro',     'techpro'),
  (2,  'MobileX',     'mobilex'),
  (3,  'SoundWave',   'soundwave'),
  (4,  'StrideFit',   'stridefit'),
  (5,  'PageTurner',  'pageturner'),
  (6,  'ErgoDesk',    'ergodesk'),
  (7,  'KeyMaster',   'keymaster'),
  (8,  'PlayEdge',    'playedge'),
  (9,  'GlowLab',     'glowlab'),
  (10, 'IronForge',   'ironforge'),
  (11, 'Lenscraft',   'lenscraft'),
  (12, 'AromaHome',   'aromahome');

SELECT setval('sw_brand_brand_id_seq', (SELECT MAX(brand_id) FROM SW_BRAND));

-- ============================================================
--  PRODUCTS  (12 — matching wireframe app.js)
-- ============================================================

INSERT INTO SW_PRODUCT
  (product_id, category_id, brand_id, seller_id, title, slug, description, bullet_points,
   base_price, sale_price, is_prime, is_featured, badge, avg_rating, review_count)
VALUES
  (
    '10000000-0000-0000-0000-000000000001', 7, 1,
    '00000000-0000-0000-0000-000000000001',
    'Laptop Pro 15" — Intel i7, 16GB RAM, 512GB SSD',
    'laptop-pro-15-i7-16gb-512ssd',
    'High-performance 15-inch laptop powered by Intel Core i7. Ideal for professionals and creators.',
    ARRAY['Intel Core i7 processor','16GB DDR5 RAM','512GB NVMe SSD','15.6" FHD IPS display','Backlit keyboard'],
    1199.99, 899.99, TRUE, TRUE, 'Best Seller', 4.00, 2341
  ),
  (
    '10000000-0000-0000-0000-000000000002', 8, 2,
    '00000000-0000-0000-0000-000000000001',
    'Smartphone X12 — 6.7" AMOLED, 128GB, 5G Ready',
    'smartphone-x12-67-amoled-128gb-5g',
    'Next-generation smartphone with a stunning AMOLED display and blazing 5G connectivity.',
    ARRAY['6.7" AMOLED display','128GB internal storage','5G connectivity','50MP triple camera','5000mAh battery'],
    649.00, 499.00, TRUE, FALSE, 'Deal', 4.00, 5820
  ),
  (
    '10000000-0000-0000-0000-000000000003', 9, 3,
    '00000000-0000-0000-0000-000000000001',
    'Wireless Noise-Cancelling Headphones — 30hr Battery',
    'wireless-nc-headphones-30hr',
    'Premium over-ear headphones with active noise cancellation and 30-hour battery life.',
    ARRAY['Active noise cancellation','30-hour battery life','Bluetooth 5.2','Foldable design','USB-C fast charging'],
    129.99, 79.99, TRUE, FALSE, NULL, 5.00, 9102
  ),
  (
    '10000000-0000-0000-0000-000000000004', 13, 4,
    '00000000-0000-0000-0000-000000000001',
    'Running Shoes — Lightweight Mesh, Men''s Size 8–13',
    'running-shoes-lightweight-mesh-mens',
    'Breathable lightweight running shoes engineered for comfort on long runs.',
    ARRAY['Lightweight mesh upper','Cushioned midsole','Non-slip rubber outsole','Available sizes 8–13','Machine washable'],
    89.95, 54.95, TRUE, FALSE, NULL, 4.00, 1234
  ),
  (
    '10000000-0000-0000-0000-000000000005', 3, 5,
    '00000000-0000-0000-0000-000000000001',
    'Clean Code: A Handbook of Agile Software Craftsmanship',
    'clean-code-handbook-agile-craftsmanship',
    'Robert C. Martin''s definitive guide to writing clean, maintainable code.',
    ARRAY['464 pages','Paperback & Kindle editions','Covers refactoring and best practices','Widely used in CS curricula','Includes real-world examples'],
    39.99, 29.99, FALSE, FALSE, 'Best Seller', 5.00, 7654
  ),
  (
    '10000000-0000-0000-0000-000000000006', 14, 6,
    '00000000-0000-0000-0000-000000000001',
    'Ergonomic Office Chair — Lumbar Support, Adjustable Arms',
    'ergonomic-office-chair-lumbar-adjustable',
    'Professional ergonomic chair designed for all-day comfort with full lumbar and arm adjustment.',
    ARRAY['Adjustable lumbar support','Height-adjustable armrests','Breathable mesh back','360° swivel base','Supports up to 300 lbs'],
    349.00, 249.00, TRUE, TRUE, 'Deal', 4.00, 876
  ),
  (
    '10000000-0000-0000-0000-000000000007', 10, 7,
    '00000000-0000-0000-0000-000000000001',
    'Mechanical Keyboard — RGB Backlit, TKL Layout, Blue Switches',
    'mechanical-keyboard-rgb-tkl-blue-switches',
    'Tenkeyless mechanical keyboard with tactile blue switches and per-key RGB lighting.',
    ARRAY['TKL compact layout','Blue tactile switches','Per-key RGB backlight','Detachable USB-C cable','N-key rollover'],
    99.99, 69.99, TRUE, FALSE, NULL, 4.00, 3210
  ),
  (
    '10000000-0000-0000-0000-000000000008', 11, 8,
    '00000000-0000-0000-0000-000000000001',
    'Gaming Controller — Wireless, Compatible with PC & Console',
    'gaming-controller-wireless-pc-console',
    'Ergonomic wireless gaming controller with low-latency 2.4GHz connection.',
    ARRAY['2.4GHz wireless','Compatible with PC and major consoles','20-hour battery','Vibration feedback','Programmable buttons'],
    59.99, 44.99, TRUE, FALSE, 'Deal', 4.00, 4500
  ),
  (
    '10000000-0000-0000-0000-000000000009', 17, 9,
    '00000000-0000-0000-0000-000000000001',
    'Vitamin C Serum — 20% Concentration, 1 fl oz',
    'vitamin-c-serum-20pct-1floz',
    'Brightening Vitamin C serum with 20% L-ascorbic acid for radiant, even-toned skin.',
    ARRAY['20% L-ascorbic acid','Brightens and evens skin tone','Reduces fine lines','Fragrance-free formula','1 fl oz / 30ml'],
    24.99, 18.99, TRUE, FALSE, 'Best Seller', 4.00, 12300
  ),
  (
    '10000000-0000-0000-0000-000000000010', 16, 10,
    '00000000-0000-0000-0000-000000000001',
    'Adjustable Dumbbell Set — 5–52.5 lbs, Space Saving',
    'adjustable-dumbbell-set-5-52-5lbs',
    'Replaces 15 sets of weights. Dial-select system adjusts from 5 to 52.5 lbs in seconds.',
    ARRAY['Adjusts 5–52.5 lbs per dumbbell','Replaces 15 sets','Dial-select mechanism','Compact storage tray','Commercial-grade steel'],
    299.00, 189.00, TRUE, TRUE, NULL, 5.00, 2100
  ),
  (
    '10000000-0000-0000-0000-000000000011', 12, 11,
    '00000000-0000-0000-0000-000000000001',
    'Mirrorless Camera — 24MP, 4K Video, Kit Lens Included',
    'mirrorless-camera-24mp-4k-kit-lens',
    'Compact mirrorless camera with 24MP sensor, 4K video recording, and 18-55mm kit lens.',
    ARRAY['24MP APS-C sensor','4K 30fps video','18-55mm kit lens included','In-body image stabilisation','Wi-Fi & Bluetooth'],
    999.00, 749.00, FALSE, FALSE, NULL, 4.00, 654
  ),
  (
    '10000000-0000-0000-0000-000000000012', 15, 12,
    '00000000-0000-0000-0000-000000000001',
    'Scented Candle Set — 6 Pack, Soy Wax, 40hr Burn Time',
    'scented-candle-set-6pack-soy-40hr',
    'Hand-poured soy wax candles in six seasonal fragrances. Each burns up to 40 hours.',
    ARRAY['6 candles per set','100% soy wax','40-hour burn time per candle','Lead-free cotton wicks','Seasonal fragrance variety'],
    34.99, 24.99, TRUE, FALSE, 'Best Seller', 4.00, 3300
  );

-- ============================================================
--  PRODUCT IMAGES  (1 primary placeholder per product)
-- ============================================================

INSERT INTO SW_PRODUCT_IMAGE (product_id, url, alt_text, sort_order, is_primary) VALUES
  ('10000000-0000-0000-0000-000000000001', '/images/laptop-pro-15-main.jpg',        'Laptop Pro 15 front view',              0, TRUE),
  ('10000000-0000-0000-0000-000000000002', '/images/smartphone-x12-main.jpg',       'Smartphone X12 front view',             0, TRUE),
  ('10000000-0000-0000-0000-000000000003', '/images/headphones-nc-main.jpg',        'Noise-cancelling headphones',           0, TRUE),
  ('10000000-0000-0000-0000-000000000004', '/images/running-shoes-main.jpg',        'Running shoes side view',               0, TRUE),
  ('10000000-0000-0000-0000-000000000005', '/images/clean-code-book-main.jpg',      'Clean Code book cover',                 0, TRUE),
  ('10000000-0000-0000-0000-000000000006', '/images/office-chair-main.jpg',         'Ergonomic office chair',                0, TRUE),
  ('10000000-0000-0000-0000-000000000007', '/images/mechanical-keyboard-main.jpg',  'Mechanical keyboard top view',          0, TRUE),
  ('10000000-0000-0000-0000-000000000008', '/images/gaming-controller-main.jpg',    'Gaming controller front view',          0, TRUE),
  ('10000000-0000-0000-0000-000000000009', '/images/vitamin-c-serum-main.jpg',      'Vitamin C serum bottle',                0, TRUE),
  ('10000000-0000-0000-0000-000000000010', '/images/dumbbell-set-main.jpg',         'Adjustable dumbbell set',               0, TRUE),
  ('10000000-0000-0000-0000-000000000011', '/images/mirrorless-camera-main.jpg',    'Mirrorless camera with kit lens',       0, TRUE),
  ('10000000-0000-0000-0000-000000000012', '/images/candle-set-main.jpg',           'Scented candle set of 6',               0, TRUE);


-- ============================================================
--  PRODUCT VARIANTS  (1 default variant per product)
--  UUIDs: 2xxxxxxx series
-- ============================================================

INSERT INTO SW_PRODUCT_VARIANT (variant_id, product_id, sku, title, price, stock_qty) VALUES
  ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'SKU-LAP-001', 'Default',          899.99,  45),
  ('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'SKU-PHN-001', 'Default',          499.00, 120),
  ('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 'SKU-HPH-001', 'Black',             79.99,  88),
  ('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003', 'SKU-HPH-002', 'White',             79.99,  34),
  ('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004', 'SKU-SHO-008', 'Size 8',            54.95,  20),
  ('20000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000004', 'SKU-SHO-010', 'Size 10',           54.95,  35),
  ('20000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000004', 'SKU-SHO-012', 'Size 12',           54.95,  15),
  ('20000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000005', 'SKU-BOK-001', 'Paperback',         29.99, 200),
  ('20000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000005', 'SKU-BOK-002', 'Kindle Edition',    29.99, 999),
  ('20000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000006', 'SKU-CHR-001', 'Black Mesh',       249.00,  18),
  ('20000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000006', 'SKU-CHR-002', 'Grey Mesh',        249.00,  12),
  ('20000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000007', 'SKU-KBD-001', 'Default',           69.99,  60),
  ('20000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000008', 'SKU-CTL-001', 'Black',             44.99,  75),
  ('20000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000008', 'SKU-CTL-002', 'White',             44.99,  40),
  ('20000000-0000-0000-0000-000000000015', '10000000-0000-0000-0000-000000000009', 'SKU-SRM-001', 'Default',           18.99, 300),
  ('20000000-0000-0000-0000-000000000016', '10000000-0000-0000-0000-000000000010', 'SKU-DBL-001', 'Default',          189.00,  22),
  ('20000000-0000-0000-0000-000000000017', '10000000-0000-0000-0000-000000000011', 'SKU-CAM-001', 'Body + Kit Lens',  749.00,   9),
  ('20000000-0000-0000-0000-000000000018', '10000000-0000-0000-0000-000000000012', 'SKU-CDL-001', 'Mixed Scents',      24.99, 150);

-- ============================================================
--  PRODUCT ATTRIBUTES
-- ============================================================

INSERT INTO SW_PRODUCT_ATTRIBUTE (attribute_id, name) VALUES
  (1, 'Color'),
  (2, 'Size'),
  (3, 'Format'),
  (4, 'Configuration');

SELECT setval('sw_product_attribute_attribute_id_seq', (SELECT MAX(attribute_id) FROM SW_PRODUCT_ATTRIBUTE));

-- ============================================================
--  VARIANT ATTRIBUTE VALUES
-- ============================================================

INSERT INTO SW_VARIANT_ATTRIBUTE_VALUE (variant_id, attribute_id, value) VALUES
  -- Headphones: Color
  ('20000000-0000-0000-0000-000000000003', 1, 'Black'),
  ('20000000-0000-0000-0000-000000000004', 1, 'White'),
  -- Running Shoes: Size
  ('20000000-0000-0000-0000-000000000005', 2, '8'),
  ('20000000-0000-0000-0000-000000000006', 2, '10'),
  ('20000000-0000-0000-0000-000000000007', 2, '12'),
  -- Book: Format
  ('20000000-0000-0000-0000-000000000008', 3, 'Paperback'),
  ('20000000-0000-0000-0000-000000000009', 3, 'Kindle Edition'),
  -- Office Chair: Color
  ('20000000-0000-0000-0000-000000000010', 1, 'Black'),
  ('20000000-0000-0000-0000-000000000011', 1, 'Grey'),
  -- Gaming Controller: Color
  ('20000000-0000-0000-0000-000000000013', 1, 'Black'),
  ('20000000-0000-0000-0000-000000000014', 1, 'White'),
  -- Camera: Configuration
  ('20000000-0000-0000-0000-000000000017', 4, 'Body + Kit Lens');

-- ============================================================
--  INVENTORY
-- ============================================================

INSERT INTO SW_INVENTORY (variant_id, qty_available, qty_reserved, reorder_level) VALUES
  ('20000000-0000-0000-0000-000000000001',  45,  2, 10),
  ('20000000-0000-0000-0000-000000000002', 120,  5, 20),
  ('20000000-0000-0000-0000-000000000003',  88,  3, 15),
  ('20000000-0000-0000-0000-000000000004',  34,  1, 10),
  ('20000000-0000-0000-0000-000000000005',  20,  0,  5),
  ('20000000-0000-0000-0000-000000000006',  35,  2,  5),
  ('20000000-0000-0000-0000-000000000007',  15,  0,  5),
  ('20000000-0000-0000-0000-000000000008', 200,  0, 30),
  ('20000000-0000-0000-0000-000000000009', 999,  0, 50),
  ('20000000-0000-0000-0000-000000000010',  18,  1,  5),
  ('20000000-0000-0000-0000-000000000011',  12,  0,  5),
  ('20000000-0000-0000-0000-000000000012',  60,  4, 10),
  ('20000000-0000-0000-0000-000000000013',  75,  3, 10),
  ('20000000-0000-0000-0000-000000000014',  40,  1, 10),
  ('20000000-0000-0000-0000-000000000015', 300,  8, 50),
  ('20000000-0000-0000-0000-000000000016',  22,  0,  5),
  ('20000000-0000-0000-0000-000000000017',   9,  1,  3),
  ('20000000-0000-0000-0000-000000000018', 150,  6, 25);

-- ============================================================
--  REVIEWS  (2–3 per product, from sample buyers)
-- ============================================================

INSERT INTO SW_REVIEW (product_id, user_id, rating, title, body, is_verified, helpful_count) VALUES
  ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 4, 'Great laptop for the price', 'Fast, reliable, and the display is crisp. Battery could be better.', TRUE, 42),
  ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 4, 'Solid work machine', 'Handles everything I throw at it. Runs cool under load.', TRUE, 18),
  ('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', 4, 'Best phone I have owned', 'Camera is incredible. 5G speeds are noticeably faster.', TRUE, 95),
  ('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003', 5, 'Life-changing on flights', 'Noise cancellation is top tier. Comfortable for hours.', TRUE, 210),
  ('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004', 5, 'Worth every penny', 'Sound quality is exceptional. Battery lasts all day.', FALSE, 77),
  ('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002', 4, 'Very comfortable', 'Light and breathable. Great for long runs.', TRUE, 31),
  ('10000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000003', 5, 'Must-read for every developer', 'Changed how I think about writing code. Highly recommended.', TRUE, 340),
  ('10000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000002', 4, 'Back pain gone', 'Lumbar support is excellent. Assembly took 30 minutes.', TRUE, 88),
  ('10000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000003', 4, 'Satisfying to type on', 'Blue switches are clicky and tactile. RGB looks great.', TRUE, 55),
  ('10000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000004', 4, 'Works great on PC', 'Low latency, comfortable grip. Battery lasts a long time.', FALSE, 29),
  ('10000000-0000-0000-0000-000000000009', '00000000-0000-0000-0000-000000000002', 4, 'Skin looks brighter', 'Noticed a difference in two weeks. No irritation.', TRUE, 120),
  ('10000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000003', 5, 'Replaced my entire rack', 'Dial system is smooth. Saves so much space.', TRUE, 67),
  ('10000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000004', 4, 'Great starter mirrorless', 'Image quality is excellent. Kit lens is surprisingly good.', FALSE, 44),
  ('10000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000002', 4, 'Beautiful gift set', 'All six scents are lovely. Burns evenly and cleanly.', TRUE, 38);

-- ============================================================
--  CARTS  (Alice has an active cart)
-- ============================================================

INSERT INTO SW_CART (cart_id, user_id) VALUES
  ('30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002');

-- Guest cart
INSERT INTO SW_CART (cart_id, session_id) VALUES
  ('30000000-0000-0000-0000-000000000002', 'guest-session-abc123');

INSERT INTO SW_CART_ITEM (cart_id, variant_id, quantity) VALUES
  ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000003', 1),  -- Headphones (Black)
  ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000015', 2),  -- Vitamin C Serum x2
  ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000013', 1);  -- Gaming Controller (guest)

-- ============================================================
--  ORDERS  (Bob has 1 completed order)
-- ============================================================

INSERT INTO SW_ORDER
  (order_id, user_id, shipping_address, status, subtotal, shipping_cost, tax_amount, total_amount)
VALUES (
  '40000000-0000-0000-0000-000000000001',
  '00000000-0000-0000-0000-000000000003',
  '{"full_name":"Bob Chen","line1":"789 Oak Blvd","city":"Austin","state":"TX","postal_code":"73301","country":"US"}',
  'delivered',
  549.98, 0.00, 49.50, 599.48
);

INSERT INTO SW_ORDER_ITEM (order_id, variant_id, product_title, variant_title, unit_price, quantity, line_total) VALUES
  ('40000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001',
   'Laptop Pro 15" — Intel i7, 16GB RAM, 512GB SSD', 'Default', 899.99, 1, 899.99),
  ('40000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000008',
   'Clean Code: A Handbook of Agile Software Craftsmanship', 'Paperback', 29.99, 1, 29.99);

-- ============================================================
--  PAYMENT
-- ============================================================

INSERT INTO SW_PAYMENT (order_id, amount, status, provider, provider_ref, paid_at) VALUES
  ('40000000-0000-0000-0000-000000000001', 599.48, 'captured', 'stripe', 'ch_sample_stripe_ref_001', NOW() - INTERVAL '5 days');

-- ============================================================
--  SHIPMENT
-- ============================================================

INSERT INTO SW_SHIPMENT (order_id, carrier, tracking_number, shipped_at, estimated_at, delivered_at) VALUES
  ('40000000-0000-0000-0000-000000000001', 'UPS', '1Z999AA10123456784',
   NOW() - INTERVAL '4 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day');

-- ============================================================
--  COUPONS
-- ============================================================

INSERT INTO SW_COUPON (code, discount_type, discount_value, min_order_value, max_uses, valid_until) VALUES
  ('WELCOME10',  'percentage',   10.00,  0.00, NULL, NOW() + INTERVAL '1 year'),
  ('SAVE20',     'fixed_amount', 20.00, 50.00, 500,  NOW() + INTERVAL '6 months'),
  ('FREESHIP',   'free_shipping', 1.00,  0.00, NULL, NOW() + INTERVAL '3 months');

-- ============================================================
--  WISHLIST
-- ============================================================

INSERT INTO SW_WISHLIST (user_id, variant_id) VALUES
  ('00000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001'),  -- Alice wants the Laptop
  ('00000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000016'),  -- Alice wants Dumbbells
  ('00000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000017');  -- Bob wants the Camera

-- ============================================================
--  SEARCH LOG  (sample queries matching wireframe categories)
-- ============================================================

INSERT INTO SW_SEARCH_LOG (user_id, session_id, query, result_count) VALUES
  ('00000000-0000-0000-0000-000000000002', NULL,                  'laptop',       1),
  ('00000000-0000-0000-0000-000000000003', NULL,                  'headphones',   1),
  (NULL,                                  'guest-session-abc123', 'gaming',       2),
  ('00000000-0000-0000-0000-000000000002', NULL,                  'electronics',  7),
  (NULL,                                  'guest-session-abc123', 'shoes',        1),
  ('00000000-0000-0000-0000-000000000004', NULL,                  'books',        1),
  (NULL,                                  'guest-session-abc123', 'camera',       1),
  ('00000000-0000-0000-0000-000000000003', NULL,                  'clean code',   1);

COMMIT;

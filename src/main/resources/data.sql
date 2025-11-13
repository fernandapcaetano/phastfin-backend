INSERT INTO "user" (active ,created_at , external_id, updated_at, email)
VALUES (TRUE, NOW(), '3fa85f64-5717-4562-b3fc-2c963f66afa6', NOW(), 'usuario@example.com')
ON CONFLICT (external_id) DO NOTHING;

INSERT INTO organization (active, created_at, external_id, updated_at, code, image_path, "name")
VALUES (TRUE, NOW(), '9a7b1f21-8a4b-4c58-9b32-8b7e73e1a2d4', NOW(), 'ORG001', '/images/org001_logo.png', 'PhastFin Bank')
ON CONFLICT (external_id) DO NOTHING;
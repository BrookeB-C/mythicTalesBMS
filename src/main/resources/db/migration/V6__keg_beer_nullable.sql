-- Make beer_id nullable in keg table
ALTER TABLE keg ALTER COLUMN beer_id DROP NOT NULL;

-- SQL script to update image references in the database
-- Run this AFTER downloading the images using the Python or PowerShell script

-- Update travel plan images with new travel images
-- This will randomly assign travel images to existing travel plans

-- First, let's update the existing travel plans with new travel images
UPDATE `travel_plans` SET 
    `images` = CASE `id`
        WHEN 3 THEN '["travel_image_01.jpg", "travel_image_02.jpg"]'
        WHEN 4 THEN '["travel_image_03.jpg", "travel_image_04.jpg"]'
        WHEN 5 THEN '["travel_image_05.jpg"]'
        WHEN 7 THEN '["travel_image_06.jpg", "travel_image_07.jpg", "travel_image_08.jpg"]'
        WHEN 8 THEN '["travel_image_09.jpg"]'
        ELSE `images`
    END
WHERE `id` IN (3, 4, 5, 7, 8);

-- Update the new travel plans (IDs 9-58) with new travel images
UPDATE `travel_plans` SET 
    `images` = CASE 
        WHEN `id` = 9 THEN '["travel_image_10.jpg", "travel_image_11.jpg"]'
        WHEN `id` = 10 THEN '["travel_image_12.jpg", "travel_image_13.jpg"]'
        WHEN `id` = 11 THEN '["travel_image_14.jpg"]'
        WHEN `id` = 12 THEN '["travel_image_15.jpg", "travel_image_16.jpg"]'
        WHEN `id` = 13 THEN '["travel_image_17.jpg", "travel_image_18.jpg"]'
        WHEN `id` = 14 THEN '["travel_image_19.jpg", "travel_image_20.jpg"]'
        WHEN `id` = 15 THEN '["travel_image_21.jpg", "travel_image_22.jpg"]'
        WHEN `id` = 16 THEN '["travel_image_23.jpg", "travel_image_24.jpg"]'
        WHEN `id` = 17 THEN '["travel_image_25.jpg", "travel_image_26.jpg"]'
        WHEN `id` = 18 THEN '["travel_image_27.jpg", "travel_image_28.jpg"]'
        WHEN `id` = 19 THEN '["travel_image_29.jpg", "travel_image_30.jpg"]'
        WHEN `id` = 20 THEN '["travel_image_31.jpg", "travel_image_32.jpg"]'
        WHEN `id` = 21 THEN '["travel_image_33.jpg", "travel_image_34.jpg"]'
        WHEN `id` = 22 THEN '["travel_image_35.jpg", "travel_image_36.jpg"]'
        WHEN `id` = 23 THEN '["travel_image_37.jpg", "travel_image_38.jpg"]'
        WHEN `id` = 24 THEN '["travel_image_39.jpg", "travel_image_40.jpg"]'
        WHEN `id` = 25 THEN '["travel_image_41.jpg", "travel_image_42.jpg"]'
        WHEN `id` = 26 THEN '["travel_image_43.jpg", "travel_image_44.jpg"]'
        WHEN `id` = 27 THEN '["travel_image_45.jpg", "travel_image_46.jpg"]'
        WHEN `id` = 28 THEN '["travel_image_47.jpg", "travel_image_48.jpg"]'
        WHEN `id` = 29 THEN '["travel_image_49.jpg", "travel_image_50.jpg"]'
        ELSE `images`
    END
WHERE `id` BETWEEN 9 AND 29;

-- Update remaining travel plans with some travel images (reusing for variety)
UPDATE `travel_plans` SET 
    `images` = CASE 
        WHEN `id` = 30 THEN '["travel_image_01.jpg", "travel_image_15.jpg"]'
        WHEN `id` = 31 THEN '["travel_image_02.jpg", "travel_image_16.jpg"]'
        WHEN `id` = 32 THEN '["travel_image_03.jpg", "travel_image_17.jpg"]'
        WHEN `id` = 33 THEN '["travel_image_04.jpg", "travel_image_18.jpg"]'
        WHEN `id` = 34 THEN '["travel_image_05.jpg", "travel_image_19.jpg"]'
        WHEN `id` = 35 THEN '["travel_image_06.jpg", "travel_image_20.jpg"]'
        WHEN `id` = 36 THEN '["travel_image_07.jpg", "travel_image_21.jpg"]'
        WHEN `id` = 37 THEN '["travel_image_08.jpg", "travel_image_22.jpg"]'
        WHEN `id` = 38 THEN '["travel_image_09.jpg", "travel_image_23.jpg"]'
        WHEN `id` = 39 THEN '["travel_image_10.jpg", "travel_image_24.jpg"]'
        WHEN `id` = 40 THEN '["travel_image_11.jpg", "travel_image_25.jpg"]'
        WHEN `id` = 41 THEN '["travel_image_12.jpg", "travel_image_26.jpg"]'
        WHEN `id` = 42 THEN '["travel_image_13.jpg", "travel_image_27.jpg"]'
        WHEN `id` = 43 THEN '["travel_image_14.jpg", "travel_image_28.jpg"]'
        WHEN `id` = 44 THEN '["travel_image_15.jpg", "travel_image_29.jpg"]'
        WHEN `id` = 45 THEN '["travel_image_16.jpg", "travel_image_30.jpg"]'
        WHEN `id` = 46 THEN '["travel_image_17.jpg", "travel_image_31.jpg"]'
        WHEN `id` = 47 THEN '["travel_image_18.jpg", "travel_image_32.jpg"]'
        WHEN `id` = 48 THEN '["travel_image_19.jpg", "travel_image_33.jpg"]'
        WHEN `id` = 49 THEN '["travel_image_20.jpg", "travel_image_34.jpg"]'
        WHEN `id` = 50 THEN '["travel_image_21.jpg", "travel_image_35.jpg"]'
        WHEN `id` = 51 THEN '["travel_image_22.jpg", "travel_image_36.jpg"]'
        WHEN `id` = 52 THEN '["travel_image_23.jpg", "travel_image_37.jpg"]'
        WHEN `id` = 53 THEN '["travel_image_24.jpg", "travel_image_38.jpg"]'
        WHEN `id` = 54 THEN '["travel_image_25.jpg", "travel_image_39.jpg"]'
        WHEN `id` = 55 THEN '["travel_image_26.jpg", "travel_image_40.jpg"]'
        WHEN `id` = 56 THEN '["travel_image_27.jpg", "travel_image_41.jpg"]'
        WHEN `id` = 57 THEN '["travel_image_28.jpg", "travel_image_42.jpg"]'
        WHEN `id` = 58 THEN '["travel_image_29.jpg", "travel_image_43.jpg"]'
        ELSE `images`
    END
WHERE `id` BETWEEN 30 AND 58;

-- Update user profile pictures with new profile images
-- Update existing users (IDs 1-9) with new profile pictures
UPDATE `users` SET 
    `profile_picture` = CASE `id`
        WHEN 1 THEN 'profile_001.jpg'
        WHEN 2 THEN 'profile_002.jpg'
        WHEN 3 THEN 'profile_003.jpg'
        WHEN 4 THEN 'profile_004.jpg'
        WHEN 5 THEN 'profile_005.jpg'
        WHEN 6 THEN 'profile_006.jpg'
        WHEN 7 THEN 'profile_007.jpg'
        WHEN 8 THEN 'profile_008.jpg'
        WHEN 9 THEN 'profile_009.jpg'
        ELSE `profile_picture`
    END
WHERE `id` BETWEEN 1 AND 9;

-- Update new users (IDs 10-109) with new profile pictures
UPDATE `users` SET 
    `profile_picture` = CONCAT('profile_', LPAD(`id`, 3, '0'), '.jpg')
WHERE `id` BETWEEN 10 AND 109;

-- Verify the updates
SELECT 'Travel Plans Updated' as Status, COUNT(*) as Count FROM `travel_plans` WHERE `images` LIKE '%travel_image_%';
SELECT 'User Profiles Updated' as Status, COUNT(*) as Count FROM `users` WHERE `profile_picture` LIKE 'profile_%';

-- Display sample results
SELECT `id`, `title`, `images` FROM `travel_plans` WHERE `id` BETWEEN 9 AND 15;
SELECT `id`, `email`, `first_name`, `profile_picture` FROM `users` WHERE `id` BETWEEN 10 AND 15;

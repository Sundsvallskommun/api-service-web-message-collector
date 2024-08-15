ALTER TABLE `message` ADD COLUMN `municipality_id` VARCHAR(4) NULL;

UPDATE `message` SET `municipality_id` = '2281';

ALTER TABLE `message` MODIFY `municipality_id` VARCHAR(4) NOT NULL;

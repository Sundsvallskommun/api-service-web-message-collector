ALTER TABLE `execution_information` ADD COLUMN `municipality_id` VARCHAR(4) NULL;

UPDATE `execution_information` SET `municipality_id` = '2281';

ALTER TABLE `execution_information` MODIFY `municipality_id` VARCHAR(4) NOT NULL;

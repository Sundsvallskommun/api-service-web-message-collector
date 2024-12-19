-- Ensure no duplication of messages
ALTER TABLE message
ADD CONSTRAINT unique_message_constraint UNIQUE (message_id, external_case_id, instance, family_id);

ALTER TABLE message
ADD COLUMN IF NOT EXISTS status ENUM('COMPLETE', 'PROCESSING', 'DELETED', 'FAILED_ATTACHMENTS'),
ADD COLUMN IF NOT EXISTS status_timestamp DATETIME(6);

-- Migrate
-- Since there is no protection against race condition (getting messages that after this update would have had status PROCESSING)
-- or soft delete, all messages are assumed to be complete
UPDATE message
SET status = 'COMPLETE',
    status_timestamp = NOW(6)
WHERE status IS NULL;

ALTER TABLE message
MODIFY COLUMN status ENUM('COMPLETE', 'PROCESSING', 'DELETED', 'FAILED_ATTACHMENTS') NOT NULL,
MODIFY COLUMN status_timestamp DATETIME(6) NOT NULL;

-- Create index
CREATE INDEX IF NOT EXISTS idx_status ON message(status);

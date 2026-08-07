-- V8__create_staff_table.sql
-- Staff / Press ID Card module for MapMyTimes News Organization
-- Table stores all press reporters, camera crew, editors, HR staff and their ID card details
-- ID Number format: {STATE}-{RTO}-{INITIALS}-{DDMM}-{YY}-{6DIGITSEQ}
--   e.g. MP-28-PM-0708-26-000001
-- NO DUMMY DATA inserted (per project constraints)

CREATE TABLE IF NOT EXISTS staff (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE,
    id_number VARCHAR(50) NOT NULL UNIQUE,
    state_code VARCHAR(5) NOT NULL,
    rto_code VARCHAR(5) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(150),
    last_name VARCHAR(150),
    designation VARCHAR(255),
    department VARCHAR(50) NOT NULL,
    photo_url TEXT,
    signature_url TEXT,
    qr_code_url TEXT,
    personal_email VARCHAR(255),
    work_email VARCHAR(255),
    mobile_private VARCHAR(20),
    work_mobile VARCHAR(20),
    emergency_contact_name VARCHAR(255),
    emergency_number VARCHAR(20),
    blood_group VARCHAR(10),
    date_of_birth DATE,
    address TEXT,
    city VARCHAR(150),
    district VARCHAR(150),
    state VARCHAR(150),
    pin_code VARCHAR(20),
    issue_date DATE,
    valid_till DATE,
    last_renewed_date DATE,
    aadhaar_last4 VARCHAR(4),
    pan_last4 VARCHAR(4),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL',
    reporter_batch_id VARCHAR(100),
    notes TEXT,
    reissue_requested BOOLEAN DEFAULT FALSE,
    reissue_reason TEXT,
    reissue_requested_at TIMESTAMP WITHOUT TIME ZONE,
    sequence_number BIGINT,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_staff_id_number ON staff(id_number);
CREATE INDEX IF NOT EXISTS idx_staff_user_id ON staff(user_id);
CREATE INDEX IF NOT EXISTS idx_staff_department ON staff(department);
CREATE INDEX IF NOT EXISTS idx_staff_status ON staff(status);
CREATE INDEX IF NOT EXISTS idx_staff_city ON staff(city);
CREATE INDEX IF NOT EXISTS idx_staff_state ON staff(state);
CREATE INDEX IF NOT EXISTS idx_staff_state_rto_code ON staff(state_code, rto_code);
CREATE INDEX IF NOT EXISTS idx_staff_issue_date ON staff(issue_date);
CREATE INDEX IF NOT EXISTS idx_staff_valid_till ON staff(valid_till);
CREATE INDEX IF NOT EXISTS idx_staff_full_name_search ON staff(full_name);
CREATE INDEX IF NOT EXISTS idx_staff_status_valid ON staff(status, valid_till);
CREATE INDEX IF NOT EXISTS idx_staff_seq_counter ON staff(state_code, rto_code, sequence_number);

-- Postgres trigger to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION staff_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_staff_updated_at ON staff;
CREATE TRIGGER trg_staff_updated_at
    BEFORE UPDATE ON staff
    FOR EACH ROW
    EXECUTE FUNCTION staff_set_updated_at();

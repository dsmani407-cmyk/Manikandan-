-- ====================================================================
-- SMART GROUP CRM - CENTRAL SUPABASE POSTGRESQL SCHEMA & RLS POLICIES
-- ====================================================================
-- Multi-Device, Multi-User Cloud Architecture with Row Level Security
-- ====================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. ENUMS
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('superadmin', 'distributor');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 3. PROFILES TABLE (Linked to auth.users)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'distributor', -- 'superadmin' or 'distributor'
    team_id TEXT,
    team_name TEXT,
    agent_code TEXT,
    phone TEXT,
    city TEXT DEFAULT 'Coimbatore',
    avatar TEXT,
    tier TEXT DEFAULT 'Gold Master',
    status TEXT DEFAULT 'ACTIVE',
    current_login_at TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. TEAMS TABLE
CREATE TABLE IF NOT EXISTS public.teams (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    lead_name TEXT,
    lead_email TEXT,
    lead_phone TEXT,
    target_monthly_revenue NUMERIC DEFAULT 500000.0,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. LEADS TABLE
CREATE TABLE IF NOT EXISTS public.leads (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT,
    city TEXT DEFAULT 'Coimbatore',
    course_or_program TEXT DEFAULT 'Professional Growth & Entrepreneurship',
    status TEXT NOT NULL DEFAULT 'NEW', -- 'NEW', 'FOLLOW_UP', 'CALL_BACK', 'DISQUALIFIED', 'CONVERTED'
    distributor_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    distributor_name TEXT NOT NULL,
    team_id TEXT,
    notes TEXT DEFAULT '',
    last_call_date TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. CONFIRMED GUESTS TABLE (Schedules / Customers)
CREATE TABLE IF NOT EXISTS public.confirmed_guests (
    id TEXT PRIMARY KEY,
    lead_id TEXT,
    guest_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    team_id TEXT,
    assigned_telecaller_name TEXT,
    visit_date_time TEXT NOT NULL,
    assigned_counsellor TEXT DEFAULT 'Senior Counsellor',
    status TEXT NOT NULL DEFAULT 'SCHEDULED', -- 'SCHEDULED', 'ARRIVED', 'IN_COUNSELLING', 'COMPLETED', 'RESCHEDULED'
    location_or_room TEXT DEFAULT 'Counselling Hall A',
    notes TEXT DEFAULT '',
    distributor_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 7. COUNSELLING LOGS TABLE
CREATE TABLE IF NOT EXISTS public.counselling_logs (
    id TEXT PRIMARY KEY,
    candidate_name TEXT NOT NULL,
    candidate_phone TEXT NOT NULL,
    counsellor_name TEXT NOT NULL,
    hall TEXT NOT NULL DEFAULT 'Counselling Hall A',
    outcome TEXT NOT NULL DEFAULT 'INTERESTED', -- 'INTERESTED', 'FOLLOW_UP', 'CLOSED'
    key_discussion TEXT DEFAULT '',
    team_id TEXT,
    team_name TEXT,
    date_time TEXT,
    recorded_by_member_name TEXT,
    distributor_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. DAILY TASKS TABLE
CREATE TABLE IF NOT EXISTS public.daily_tasks (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT DEFAULT '',
    category TEXT DEFAULT 'Calling',
    priority TEXT DEFAULT 'High',
    due_time TEXT DEFAULT '06:00 PM',
    status TEXT NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'IN_PROGRESS', 'COMPLETED'
    is_completed BOOLEAN DEFAULT FALSE,
    is_closed BOOLEAN DEFAULT FALSE,
    closing_remarks TEXT,
    team_id TEXT,
    assigned_to_member_id TEXT,
    assigned_to_member_name TEXT,
    distributor_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 9. SALES TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS public.sales_transactions (
    id TEXT PRIMARY KEY,
    candidate_name TEXT NOT NULL,
    candidate_phone TEXT NOT NULL,
    seniority_amount NUMERIC NOT NULL,
    payment_mode TEXT NOT NULL DEFAULT 'UPI',
    transaction_ref TEXT NOT NULL,
    receipt_number TEXT NOT NULL,
    approval_status TEXT NOT NULL DEFAULT 'APPROVED', -- 'PENDING', 'APPROVED'
    team_id TEXT,
    team_name TEXT,
    agent_name TEXT,
    date TEXT,
    closure_proof_image_url TEXT,
    storage_bucket TEXT DEFAULT 'sales-proofs',
    distributor_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 10. DAILY CLOSING RECORDS TABLE
CREATE TABLE IF NOT EXISTS public.daily_closings (
    id TEXT PRIMARY KEY,
    closing_date TEXT NOT NULL,
    closed_by TEXT NOT NULL,
    total_tasks INTEGER DEFAULT 0,
    completed_count INTEGER DEFAULT 0,
    incomplete_count INTEGER DEFAULT 0,
    closing_remarks TEXT,
    distributor_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 11. CELEBRATIONS TABLE
CREATE TABLE IF NOT EXISTS public.celebrations (
    id TEXT PRIMARY KEY,
    team_id TEXT,
    agent_name TEXT NOT NULL,
    team_name TEXT NOT NULL,
    candidate_name TEXT NOT NULL,
    closed_amount NUMERIC NOT NULL,
    date TEXT,
    badge_title TEXT DEFAULT 'Star Closer',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ====================================================================
-- ROW LEVEL SECURITY (RLS) HELPER FUNCTIONS & POLICIES
-- ====================================================================

-- Helper function: Check if current authenticated user is SuperAdmin
CREATE OR REPLACE FUNCTION public.is_super_admin()
RETURNS BOOLEAN AS $$
BEGIN
  RETURN EXISTS (
    SELECT 1 FROM public.profiles
    WHERE id = auth.uid() AND (role = 'superadmin' OR role = 'SuperAdmin')
  );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Enable RLS on all tables
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.teams ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.leads ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.confirmed_guests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.counselling_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.daily_tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.sales_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.daily_closings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.celebrations ENABLE ROW LEVEL SECURITY;

-- --------------------------------------------------------------------
-- PROFILES POLICIES
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Profiles read access" ON public.profiles;
CREATE POLICY "Profiles read access" ON public.profiles
    FOR SELECT TO authenticated
    USING (id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Profiles update access" ON public.profiles;
CREATE POLICY "Profiles update access" ON public.profiles
    FOR UPDATE TO authenticated
    USING (id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Profiles insert access" ON public.profiles;
CREATE POLICY "Profiles insert access" ON public.profiles
    FOR INSERT TO authenticated
    WITH CHECK (id = auth.uid() OR public.is_super_admin());

-- --------------------------------------------------------------------
-- TEAMS POLICIES (All authenticated can view; only admin can modify)
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Teams read access" ON public.teams;
CREATE POLICY "Teams read access" ON public.teams
    FOR SELECT TO authenticated
    USING (true);

DROP POLICY IF EXISTS "Teams write access" ON public.teams;
CREATE POLICY "Teams write access" ON public.teams
    FOR ALL TO authenticated
    USING (public.is_super_admin());

-- --------------------------------------------------------------------
-- LEADS POLICIES
-- Normal distributor: view/create/edit/delete only their own records
-- Super admin: view/create/edit/delete all distributors' records
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Leads select policy" ON public.leads;
CREATE POLICY "Leads select policy" ON public.leads
    FOR SELECT TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Leads insert policy" ON public.leads;
CREATE POLICY "Leads insert policy" ON public.leads
    FOR INSERT TO authenticated
    WITH CHECK (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Leads update policy" ON public.leads;
CREATE POLICY "Leads update policy" ON public.leads
    FOR UPDATE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Leads delete policy" ON public.leads;
CREATE POLICY "Leads delete policy" ON public.leads
    FOR DELETE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

-- --------------------------------------------------------------------
-- CONFIRMED GUESTS / CUSTOMERS / SCHEDULE POLICIES
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Guests select policy" ON public.confirmed_guests;
CREATE POLICY "Guests select policy" ON public.confirmed_guests
    FOR SELECT TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Guests insert policy" ON public.confirmed_guests;
CREATE POLICY "Guests insert policy" ON public.confirmed_guests
    FOR INSERT TO authenticated
    WITH CHECK (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Guests update policy" ON public.confirmed_guests;
CREATE POLICY "Guests update policy" ON public.confirmed_guests
    FOR UPDATE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Guests delete policy" ON public.confirmed_guests;
CREATE POLICY "Guests delete policy" ON public.confirmed_guests
    FOR DELETE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

-- --------------------------------------------------------------------
-- COUNSELLING LOGS POLICIES
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Counselling select policy" ON public.counselling_logs;
CREATE POLICY "Counselling select policy" ON public.counselling_logs
    FOR SELECT TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Counselling insert policy" ON public.counselling_logs;
CREATE POLICY "Counselling insert policy" ON public.counselling_logs
    FOR INSERT TO authenticated
    WITH CHECK (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Counselling update policy" ON public.counselling_logs;
CREATE POLICY "Counselling update policy" ON public.counselling_logs
    FOR UPDATE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

-- --------------------------------------------------------------------
-- DAILY TASKS POLICIES
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Tasks select policy" ON public.daily_tasks;
CREATE POLICY "Tasks select policy" ON public.daily_tasks
    FOR SELECT TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Tasks insert policy" ON public.daily_tasks;
CREATE POLICY "Tasks insert policy" ON public.daily_tasks
    FOR INSERT TO authenticated
    WITH CHECK (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Tasks update policy" ON public.daily_tasks;
CREATE POLICY "Tasks update policy" ON public.daily_tasks
    FOR UPDATE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Tasks delete policy" ON public.daily_tasks;
CREATE POLICY "Tasks delete policy" ON public.daily_tasks
    FOR DELETE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

-- --------------------------------------------------------------------
-- SALES TRANSACTIONS & REVENUE POLICIES
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Sales select policy" ON public.sales_transactions;
CREATE POLICY "Sales select policy" ON public.sales_transactions
    FOR SELECT TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Sales insert policy" ON public.sales_transactions;
CREATE POLICY "Sales insert policy" ON public.sales_transactions
    FOR INSERT TO authenticated
    WITH CHECK (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Sales update policy" ON public.sales_transactions;
CREATE POLICY "Sales update policy" ON public.sales_transactions
    FOR UPDATE TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

-- --------------------------------------------------------------------
-- DAILY CLOSINGS POLICIES
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Closings select policy" ON public.daily_closings;
CREATE POLICY "Closings select policy" ON public.daily_closings
    FOR SELECT TO authenticated
    USING (distributor_id = auth.uid() OR public.is_super_admin());

DROP POLICY IF EXISTS "Closings insert policy" ON public.daily_closings;
CREATE POLICY "Closings insert policy" ON public.daily_closings
    FOR INSERT TO authenticated
    WITH CHECK (distributor_id = auth.uid() OR public.is_super_admin());

-- --------------------------------------------------------------------
-- CELEBRATIONS POLICIES (All authenticated can view)
-- --------------------------------------------------------------------
DROP POLICY IF EXISTS "Celebrations select policy" ON public.celebrations;
CREATE POLICY "Celebrations select policy" ON public.celebrations
    FOR SELECT TO authenticated
    USING (true);

DROP POLICY IF EXISTS "Celebrations insert policy" ON public.celebrations;
CREATE POLICY "Celebrations insert policy" ON public.celebrations
    FOR INSERT TO authenticated
    WITH CHECK (true);

-- ====================================================================
-- AUTOMATIC PROFILE CREATION TRIGGER ON SIGNUP
-- ====================================================================
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (
    id,
    email,
    name,
    role,
    agent_code,
    phone,
    city,
    team_id,
    team_name
  )
  VALUES (
    NEW.id,
    NEW.email,
    COALESCE(NEW.raw_user_meta_data->>'name', split_part(NEW.email, '@', 1)),
    COALESCE(NEW.raw_user_meta_data->>'role', 'distributor'),
    COALESCE(NEW.raw_user_meta_data->>'agent_code', 'TRZ-' || UPPER(SUBSTRING(NEW.id::text, 1, 4))),
    COALESCE(NEW.raw_user_meta_data->>'phone', ''),
    COALESCE(NEW.raw_user_meta_data->>'city', 'Coimbatore'),
    COALESCE(NEW.raw_user_meta_data->>'team_id', 'team-1'),
    COALESCE(NEW.raw_user_meta_data->>'team_name', 'Alpha Warriors')
  )
  ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- ====================================================================
-- REALTIME SUBSCRIPTIONS
-- Enable Supabase Realtime broadcast for live updates on Admin Dashboard
-- ====================================================================
ALTER PUBLICATION supabase_realtime ADD TABLE public.leads;
ALTER PUBLICATION supabase_realtime ADD TABLE public.daily_tasks;
ALTER PUBLICATION supabase_realtime ADD TABLE public.confirmed_guests;
ALTER PUBLICATION supabase_realtime ADD TABLE public.counselling_logs;
ALTER PUBLICATION supabase_realtime ADD TABLE public.sales_transactions;
ALTER PUBLICATION supabase_realtime ADD TABLE public.celebrations;

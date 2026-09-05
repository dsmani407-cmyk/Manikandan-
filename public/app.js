// Smart Group Command Center - Mobile-First PWA Logic
// Optimized for Google Pixel 11 & Android Chrome

(function() {
  'use strict';

  // Initial State Seed Data
  const DEFAULT_STATE = {
    currentRole: 'Distributor', // 'SuperAdmin', 'Team Leader', 'Distributor', 'Counsellor', 'Telecaller'
    currentUserName: 'Priya Sharma',
    currentTeam: 'Alpha Warriors',
    theme: 'dark',
    activeTab: 'dashboard',
    isOffline: false,
    lastSyncTime: 'Just now',
    
    // Teams
    teams: [
      { id: 'team-1', name: 'Alpha Warriors', leader: 'Rajesh Kumar', distributorsCount: 18, totalRevenue: 1480000, target: 2000000 },
      { id: 'team-2', name: 'Zenith Eagles', leader: 'Suresh Verma', distributorsCount: 14, totalRevenue: 1120000, target: 1500000 },
      { id: 'team-3', name: 'Apex Champions', leader: 'Kavitha Reddy', distributorsCount: 22, totalRevenue: 1950000, target: 2500000 }
    ],

    // Leads Pipeline
    leads: [
      { id: 'lead-1', name: 'Ramesh Patel', phone: '+91 98765 43210', city: 'Coimbatore', program: 'Premium Directorship', status: 'NEW', telecaller: 'Kavitha', notes: 'Interested in retail distribution franchise' },
      { id: 'lead-2', name: 'Ananya Sharma', phone: '+91 98111 22334', city: 'Chennai', program: 'Executive Associate', status: 'CONTACTED', telecaller: 'Kavitha', notes: 'Scheduled phone briefing for tomorrow' },
      { id: 'lead-3', name: 'Vignesh Swaminathan', phone: '+91 97444 55667', city: 'Madurai', program: 'Senior Associate', status: 'FOLLOW_UP', telecaller: 'Pooja', notes: 'Requested fee structure breakdown' },
      { id: 'lead-4', name: 'Meenakshi Sundaram', phone: '+91 94433 11223', city: 'Salem', program: 'Master Franchisee', status: 'CONVERTED', telecaller: 'Kavitha', notes: 'Paid initial token advance at center' },
      { id: 'lead-5', name: 'Karthik Raj', phone: '+91 99555 66778', city: 'Trichy', program: 'Seniority Package A', status: 'CONVERTED', telecaller: 'Pooja', notes: 'Completed counselling session with Arun' },
      { id: 'lead-6', name: 'Sneha Kulkarni', phone: '+91 98222 33445', city: 'Bengaluru', program: 'Associate Partner', status: 'NEW', telecaller: 'Kavitha', notes: 'Callback requested after 6 PM' },
      { id: 'lead-7', name: 'Divya Nair', phone: '+91 97111 88990', city: 'Kochi', program: 'Direct Dealer', status: 'CONTACTED', telecaller: 'Pooja', notes: 'Invited to Tranz India business seminar' },
      { id: 'lead-8', name: 'Mohammed Faizal', phone: '+91 96000 44332', city: 'Tirunelveli', program: 'Executive Associate', status: 'FOLLOW_UP', telecaller: 'Kavitha', notes: 'Following up on identity documents' }
    ],

    // Confirmed Guests / Scheduled Visits
    visits: [
      { id: 'vis-1', guestName: 'Ramesh Patel', phone: '+91 98765 43210', hall: 'Hall A (Ground Floor)', counsellor: 'Arun Prakash', time: 'Today, 10:30 AM', status: 'SCHEDULED', remarks: 'Franchise overview' },
      { id: 'vis-2', guestName: 'Vignesh Swaminathan', phone: '+91 97444 55667', hall: 'Hall B (First Floor)', counsellor: 'Deepak Raj', time: 'Today, 02:00 PM', status: 'IN_PROGRESS', remarks: 'Fee structure counselling' },
      { id: 'vis-3', guestName: 'Meenakshi Sundaram', phone: '+91 94433 11223', hall: 'Executive Suite 1', counsellor: 'Arun Prakash', time: 'Yesterday, 04:30 PM', status: 'CONVERTED', remarks: 'Seniority closure verified' },
      { id: 'vis-4', guestName: 'Mohammed Faizal', phone: '+91 96000 44332', hall: 'Hall A (Ground Floor)', counsellor: 'Deepak Raj', time: 'Tomorrow, 11:00 AM', status: 'SCHEDULED', remarks: 'Document verification' }
    ],

    // Daily Tasks & Distributor Checklist
    tasks: [
      { id: 'task-1', title: 'Complete 15 telecalling follow-up calls', priority: 'HIGH', dueTime: '12:00 PM', completed: true, category: 'Field Calls' },
      { id: 'task-2', title: 'Collect Ramesh Patel KYC & Aadhaar copy', priority: 'HIGH', dueTime: '02:30 PM', completed: false, category: 'Documentation' },
      { id: 'task-3', title: 'Attend Hall B counselling briefing with Arun', priority: 'MEDIUM', dueTime: '04:00 PM', completed: false, category: 'Counselling' },
      { id: 'task-4', title: 'Submit daily closing report & deposit cash slips', priority: 'HIGH', dueTime: '07:00 PM', completed: false, category: 'Closing' },
      { id: 'task-5', title: 'Follow up with 5 unconfirmed leads from Madurai', priority: 'NORMAL', dueTime: '08:30 PM', completed: true, category: 'Outreach' }
    ],

    // Seniority & Sales Transactions
    sales: [
      { id: 'tx-101', candidateName: 'Meenakshi Sundaram', phone: '+91 94433 11223', amount: 50000, mode: 'UPI', ref: 'UPI-984210984', receipt: 'REC-2026-881', agent: 'Priya Sharma', team: 'Alpha Warriors', date: '05 Sep 2026', status: 'APPROVED' },
      { id: 'tx-102', candidateName: 'Karthik Raj', phone: '+91 99555 66778', amount: 35000, mode: 'NEFT', ref: 'NEFT-887410291', receipt: 'REC-2026-882', agent: 'Priya Sharma', team: 'Alpha Warriors', date: '05 Sep 2026', status: 'APPROVED' },
      { id: 'tx-103', candidateName: 'Sanjay Krishnan', phone: '+91 98888 22114', amount: 25000, mode: 'Cash', ref: 'CASH-REC-001', receipt: 'REC-2026-883', agent: 'Rajesh Kumar', team: 'Alpha Warriors', date: '05 Sep 2026', status: 'PENDING' },
      { id: 'tx-104', candidateName: 'Deepa Venkat', phone: '+91 97777 44331', amount: 50000, mode: 'UPI', ref: 'UPI-774411223', receipt: 'REC-2026-884', agent: 'Suresh Verma', team: 'Zenith Eagles', date: '04 Sep 2026', status: 'APPROVED' },
      { id: 'tx-105', candidateName: 'Manoj Kumar', phone: '+91 96666 33221', amount: 35000, mode: 'UPI', ref: 'UPI-663322114', receipt: 'REC-2026-885', agent: 'Kavitha Reddy', team: 'Apex Champions', date: '04 Sep 2026', status: 'APPROVED' },
      { id: 'tx-106', candidateName: 'Anita Balan', phone: '+91 95555 11223', amount: 25000, mode: 'NEFT', ref: 'NEFT-998877665', receipt: 'REC-2026-886', agent: 'Priya Sharma', team: 'Alpha Warriors', date: '03 Sep 2026', status: 'APPROVED' }
    ],

    // Notifications
    notifications: [
      { id: 'notif-1', title: 'Seniority Payment Approved', text: 'Meenakshi Sundaram (₹50,000) verified by SuperAdmin', time: '10m ago', read: false },
      { id: 'notif-2', title: 'New Confirmed Visit', text: 'Ramesh Patel scheduled in Hall A at 10:30 AM', time: '45m ago', read: false },
      { id: 'notif-3', title: 'Daily Target Reminder', text: 'Alpha Warriors need ₹5.2 Lakhs to reach monthly milestone', time: '2h ago', read: true }
    ],

    // Day Closing Records
    closingRecords: [
      { date: '04 Sep 2026', closedBy: 'Priya Sharma', tasksDone: 5, totalTasks: 5, remarks: 'All candidate documents collected and uploaded', verifiedOtp: '8912' }
    ]
  };

  // State Management with LocalStorage persistence
  const STORAGE_KEY = 'smartgroup_pwa_state_v1';
  let state = loadState();

  function loadState() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        return { ...DEFAULT_STATE, ...JSON.parse(saved) };
      }
    } catch (e) {
      console.warn('Could not parse saved state, using default:', e);
    }
    return JSON.parse(JSON.stringify(DEFAULT_STATE));
  }

  function saveState() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (e) {
      console.warn('Failed to save state to localStorage:', e);
    }
  }

  // DOM References
  const mainViewport = document.getElementById('main-viewport');
  const navButtons = document.querySelectorAll('.bottom-nav .nav-item');
  const btnRoleSelector = document.getElementById('btn-role-selector');
  const headerRoleLabel = document.getElementById('header-role-label');
  const btnThemeToggle = document.getElementById('btn-theme-toggle');
  const themeIconSun = document.getElementById('theme-icon-sun');
  const themeIconMoon = document.getElementById('theme-icon-moon');
  const btnNotifications = document.getElementById('btn-notifications');
  const headerNotifBadge = document.getElementById('header-notif-badge');
  const navLeadsBadge = document.getElementById('nav-leads-badge');
  
  // Cache Bar Elements
  const cacheBar = document.getElementById('cache-bar');
  const cacheStatusText = document.getElementById('cache-status-text');
  const cacheStatusBadge = document.getElementById('cache-status-badge');
  const cacheMetricsText = document.getElementById('cache-metrics-text');
  const btnToggleOffline = document.getElementById('btn-toggle-offline');
  const lblOfflineToggle = document.getElementById('lbl-offline-toggle');
  const btnSyncCache = document.getElementById('btn-sync-cache');

  // Modal Container
  const modalContainer = document.getElementById('modal-container');
  const modalSheetContent = document.getElementById('modal-sheet-content');
  const toastEl = document.getElementById('toast');

  // Toast Helper
  let toastTimer;
  function showToast(msg) {
    if (!toastEl) return;
    toastEl.textContent = msg;
    toastEl.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => {
      toastEl.classList.remove('show');
    }, 2800);
  }

  // Format INR Currency
  function formatINR(amount) {
    return '₹' + Number(amount).toLocaleString('en-IN');
  }

  // Initialize UI & Event Handlers
  function init() {
    // Apply Theme
    applyTheme(state.theme);

    // Update Role in Header
    updateRoleUI();

    // Update Cache Bar UI
    updateCacheBarUI();

    // Set Navigation Listeners
    navButtons.forEach(btn => {
      btn.addEventListener('click', () => {
        const tab = btn.getAttribute('data-tab');
        switchTab(tab);
      });
    });

    // Theme Toggle
    btnThemeToggle.addEventListener('click', () => {
      const newTheme = state.theme === 'dark' ? 'light' : 'dark';
      applyTheme(newTheme);
      showToast(`Switched to ${newTheme === 'dark' ? 'Field Sales Dark' : 'Office Light'} Mode`);
    });

    // Role Switcher
    btnRoleSelector.addEventListener('click', () => {
      openRoleModal();
    });

    // Notifications Drawer
    btnNotifications.addEventListener('click', () => {
      openNotificationsModal();
    });

    // Offline Simulation Toggle
    btnToggleOffline.addEventListener('click', () => {
      state.isOffline = !state.isOffline;
      saveState();
      updateCacheBarUI();
      if (state.isOffline) {
        showToast('Offline Mode Active: Serving from Room Local SQLite Cache');
      } else {
        showToast('Online Mode Restored: Live Connected to Tranz India Server');
      }
    });

    // Sync Cache Button
    btnSyncCache.addEventListener('click', () => {
      const now = new Date();
      state.lastSyncTime = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      saveState();
      updateCacheBarUI();
      showToast('Cache Synchronized! All records verified for offline field calls.');
    });

    // Close modal when clicking on overlay background
    modalContainer.addEventListener('click', (e) => {
      if (e.target === modalContainer) {
        closeModal();
      }
    });

    // Render Active Tab
    renderTab(state.activeTab);

    // Dismiss Splash Screen
    setTimeout(() => {
      const splash = document.getElementById('splash-screen');
      if (splash) splash.classList.add('hidden');
    }, 500);
  }

  function applyTheme(theme) {
    state.theme = theme;
    document.documentElement.setAttribute('data-theme', theme);
    if (theme === 'light') {
      themeIconSun.style.display = 'none';
      themeIconMoon.style.display = 'block';
    } else {
      themeIconSun.style.display = 'block';
      themeIconMoon.style.display = 'none';
    }
    saveState();
  }

  function updateRoleUI() {
    headerRoleLabel.textContent = state.currentRole;
    const unread = state.notifications.filter(n => !n.read).length;
    headerNotifBadge.textContent = unread;
    headerNotifBadge.style.display = unread > 0 ? 'flex' : 'none';

    const newLeads = state.leads.filter(l => l.status === 'NEW').length;
    navLeadsBadge.textContent = newLeads;
    navLeadsBadge.style.display = newLeads > 0 ? 'flex' : 'none';
  }

  function updateCacheBarUI() {
    if (state.isOffline) {
      cacheBar.classList.add('is-offline');
      cacheStatusText.textContent = 'Field Call Offline Mode';
      cacheStatusBadge.textContent = 'OFFLINE';
      lblOfflineToggle.textContent = 'Go Online';
    } else {
      cacheBar.classList.remove('is-offline');
      cacheStatusText.textContent = 'Room Cache Active';
      cacheStatusBadge.textContent = 'CACHED';
      lblOfflineToggle.textContent = 'Simulate Offline';
    }
    cacheMetricsText.textContent = `Room SQLite: ${state.leads.length} leads • ${state.sales.length} sales cached (${state.lastSyncTime})`;
  }

  function switchTab(tabName) {
    state.activeTab = tabName;
    navButtons.forEach(btn => {
      btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
    });
    renderTab(tabName);
    saveState();
  }

  // Screen Rendering Router
  function renderTab(tab) {
    switch (tab) {
      case 'dashboard':
        renderDashboard();
        break;
      case 'leads':
        renderLeads();
        break;
      case 'visits':
        renderVisits();
        break;
      case 'tasks':
        renderTasks();
        break;
      case 'sales':
        renderSales();
        break;
      case 'teams':
        renderTeams();
        break;
      default:
        renderDashboard();
    }
  }

  // TAB 1: COMMAND DASHBOARD
  function renderDashboard() {
    const totalRevenue = state.sales.reduce((acc, s) => acc + s.amount, 0);
    const convertedLeads = state.leads.filter(l => l.status === 'CONVERTED').length;
    const pendingTasks = state.tasks.filter(t => !t.completed).length;
    const scheduledVisits = state.visits.filter(v => v.status === 'SCHEDULED' || v.status === 'IN_PROGRESS').length;

    let html = `
      <!-- Welcome Role Header Card -->
      <div class="card" style="background: linear-gradient(135deg, rgba(79, 70, 229, 0.25), rgba(15, 23, 42, 0.9)); border-color: #6366f1;">
        <div style="display:flex; justify-content:space-between; align-items:flex-start;">
          <div>
            <div style="font-size:12px; color:#818cf8; font-weight:700;">COMMAND CENTER</div>
            <div style="font-size:18px; font-weight:900; color:#ffffff; margin-top:2px;">Hello, ${state.currentUserName}</div>
            <div style="font-size:12px; color:#cbd5e1; margin-top:2px;">Team: <strong>${state.currentTeam}</strong> • Role: <strong>${state.currentRole}</strong></div>
          </div>
          <span class="badge" style="background:#4f46e5; color:white;">ACTIVE</span>
        </div>
      </div>

      <!-- Real-Time Celebrations Ticker -->
      <div class="ticker-card">
        <div class="ticker-icon">🏆</div>
        <div class="ticker-content">
          <div class="ticker-title">LIVE SENIORITY CLOSURE</div>
          <div class="ticker-text"><strong>Meenakshi Sundaram</strong> closed ₹50,000 Seniority package with <strong>Alpha Warriors</strong>!</div>
        </div>
      </div>

      <!-- KPI Grid -->
      <div class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-label">TOTAL SENIORITY REVENUE</div>
          <div class="kpi-value" style="color:#10b981;">${formatINR(totalRevenue)}</div>
          <div class="kpi-trend trend-up">↑ 14.8% vs last week</div>
        </div>

        <div class="kpi-card">
          <div class="kpi-label">CONVERTED CANDIDATES</div>
          <div class="kpi-value">${convertedLeads}</div>
          <div class="kpi-trend trend-up">★ High conversion</div>
        </div>

        <div class="kpi-card">
          <div class="kpi-label">SCHEDULED VISITS</div>
          <div class="kpi-value" style="color:#38bdf8;">${scheduledVisits}</div>
          <div class="kpi-trend">Counselling Hall A/B</div>
        </div>

        <div class="kpi-card">
          <div class="kpi-label">PENDING FIELD TASKS</div>
          <div class="kpi-value" style="color:#f59e0b;">${pendingTasks}</div>
          <div class="kpi-trend trend-amber">Daily closing needed</div>
        </div>
      </div>

      <!-- Quick Actions Strip -->
      <div class="form-label" style="margin-top:4px;">QUICK FIELD ACTIONS</div>
      <div class="quick-action-strip">
        <button id="act-new-lead" class="quick-action-btn primary">
          <span>+ Add Lead</span>
        </button>
        <button id="act-new-sale" class="quick-action-btn">
          <span>₹ Record Sale</span>
        </button>
        <button id="act-close-day" class="quick-action-btn" style="background:rgba(245, 158, 11, 0.15); color:#f59e0b; border-color:#f59e0b;">
          <span>✓ Close Day (OTP)</span>
        </button>
        <button id="act-view-visits" class="quick-action-btn">
          <span>🏛 Hall Visits</span>
        </button>
      </div>

      <!-- Recent Closures Summary -->
      <div class="card">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <span style="font-size:13.5px; font-weight:800;">Recent Closures & Verification</span>
          <button id="act-view-all-sales" style="background:transparent; border:none; color:var(--primary-light); font-size:12px; font-weight:700; cursor:pointer;">View All →</button>
        </div>
        <div style="display:flex; flex-direction:column; gap:8px; margin-top:4px;">
          ${state.sales.slice(0, 3).map(s => `
            <div style="display:flex; justify-content:space-between; align-items:center; padding:8px 0; border-bottom:1px solid var(--border-color-subtle);">
              <div>
                <div style="font-size:13px; font-weight:700;">${s.candidateName}</div>
                <div style="font-size:11px; color:var(--text-muted);">${s.mode} • ${s.receipt} • ${s.date}</div>
              </div>
              <div style="text-align:right;">
                <div style="font-size:13px; font-weight:800; color:#10b981;">${formatINR(s.amount)}</div>
                <span class="badge ${s.status === 'APPROVED' ? 'badge-converted' : 'badge-scheduled'}">${s.status}</span>
              </div>
            </div>
          `).join('')}
        </div>
      </div>
    `;

    mainViewport.innerHTML = html;

    // Attach Dashboard Event Listeners
    document.getElementById('act-new-lead').addEventListener('click', openAddLeadModal);
    document.getElementById('act-new-sale').addEventListener('click', openRecordSaleModal);
    document.getElementById('act-close-day').addEventListener('click', openDayClosingModal);
    document.getElementById('act-view-visits').addEventListener('click', () => switchTab('visits'));
    document.getElementById('act-view-all-sales').addEventListener('click', () => switchTab('sales'));
  }

  // TAB 2: TELECALLING & LEADS PIPELINE
  let leadFilter = 'ALL';
  let leadSearch = '';

  function renderLeads() {
    let filtered = state.leads.filter(l => {
      const matchFilter = leadFilter === 'ALL' || l.status === leadFilter;
      const matchSearch = leadSearch === '' || 
        l.name.toLowerCase().includes(leadSearch.toLowerCase()) ||
        l.phone.includes(leadSearch) ||
        l.city.toLowerCase().includes(leadSearch.toLowerCase());
      return matchFilter && matchSearch;
    });

    let html = `
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div>
          <div style="font-size:17px; font-weight:900;">Telecalling Pipeline</div>
          <div style="font-size:11.5px; color:var(--text-muted);">${state.leads.length} total prospects in SQLite cache</div>
        </div>
        <button id="btn-add-lead-top" class="btn-primary" style="width:auto; height:36px; padding:0 14px; font-size:12px;">+ Add Lead</button>
      </div>

      <!-- Search Input -->
      <div style="position:relative;">
        <input id="input-lead-search" type="text" class="form-input" placeholder="Search by name, phone, or city..." value="${leadSearch}">
      </div>

      <!-- Filter Chips -->
      <div class="quick-action-strip">
        ${['ALL', 'NEW', 'CONTACTED', 'FOLLOW_UP', 'CONVERTED', 'LOST'].map(status => `
          <button class="quick-action-btn filter-chip ${leadFilter === status ? 'primary' : ''}" data-filter="${status}">
            ${status.replace('_', ' ')}
          </button>
        `).join('')}
      </div>

      <!-- Lead Cards List -->
      <div class="list-container">
        ${filtered.length === 0 ? `
          <div class="card" style="text-align:center; padding:30px 10px; color:var(--text-muted);">
            No leads found matching your criteria.
          </div>
        ` : filtered.map(lead => `
          <div class="lead-item" data-lead-id="${lead.id}">
            <div class="lead-header">
              <div>
                <div class="lead-name">${lead.name}</div>
                <div class="lead-phone">📞 ${lead.phone} • 📍 ${lead.city}</div>
                <div class="lead-program">${lead.program}</div>
              </div>
              <span class="badge badge-${lead.status.toLowerCase().replace('_', '')}">${lead.status}</span>
            </div>

            <div style="font-size:11.5px; color:var(--text-muted); background:var(--bg-input); padding:6px 10px; border-radius:8px;">
              📝 ${lead.notes || 'No remarks recorded.'}
            </div>

            <div class="lead-footer">
              <span>Telecaller: <strong>${lead.telecaller}</strong></span>
              <div class="lead-actions-row">
                <a href="tel:${lead.phone.replace(/[^0-9+]/g, '')}" class="btn-action-icon" title="Call">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                  </svg>
                </a>
                <a href="https://wa.me/${lead.phone.replace(/[^0-9]/g, '')}" target="_blank" class="btn-action-icon" title="WhatsApp" style="color:#25d366;">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12.04 2c-5.46 0-9.91 4.45-9.91 9.91 0 1.75.46 3.45 1.32 4.95L2.05 22l5.25-1.38c1.45.79 3.08 1.21 4.74 1.21 5.46 0 9.91-4.45 9.91-9.91 0-2.65-1.03-5.14-2.9-7.01A9.816 9.816 0 0 0 12.04 2z"/>
                  </svg>
                </a>
                <button class="btn-small act-convert-visit" data-lead-id="${lead.id}" style="background:var(--primary); color:white; border-color:var(--primary-light);">
                  Schedule Visit
                </button>
              </div>
            </div>
          </div>
        `).join('')}
      </div>
    `;

    mainViewport.innerHTML = html;

    // Search event
    const searchInput = document.getElementById('input-lead-search');
    searchInput.addEventListener('input', (e) => {
      leadSearch = e.target.value;
      renderLeads();
    });

    // Filter Chips
    document.querySelectorAll('.filter-chip').forEach(btn => {
      btn.addEventListener('click', () => {
        leadFilter = btn.getAttribute('data-filter');
        renderLeads();
      });
    });

    // Add Lead Button
    document.getElementById('btn-add-lead-top').addEventListener('click', openAddLeadModal);

    // Schedule Visit buttons
    document.querySelectorAll('.act-convert-visit').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const leadId = btn.getAttribute('data-lead-id');
        openConvertLeadModal(leadId);
      });
    });

    // Lead item detail click
    document.querySelectorAll('.lead-item').forEach(item => {
      item.addEventListener('click', () => {
        const leadId = item.getAttribute('data-lead-id');
        openLeadDetailModal(leadId);
      });
    });
  }

  // TAB 3: SCHEDULED VISITS & COUNSELLING HALL
  function renderVisits() {
    let html = `
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div>
          <div style="font-size:17px; font-weight:900;">Counselling Hall Visits</div>
          <div style="font-size:11.5px; color:var(--text-muted);">Confirmed guest allocations and centre timings</div>
        </div>
      </div>

      <div class="list-container">
        ${state.visits.map(vis => `
          <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:flex-start;">
              <div>
                <div style="font-size:15px; font-weight:800;">${vis.guestName}</div>
                <div style="font-size:12px; color:var(--text-muted);">📞 ${vis.phone}</div>
              </div>
              <span class="badge ${vis.status === 'CONVERTED' ? 'badge-converted' : vis.status === 'IN_PROGRESS' ? 'badge-contacted' : 'badge-scheduled'}">${vis.status}</span>
            </div>

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px; margin-top:4px;">
              <div style="background:var(--bg-input); padding:8px; border-radius:8px;">
                <div style="font-size:10px; color:var(--text-muted); font-weight:700;">LOCATION</div>
                <div style="font-size:12px; font-weight:700;">${vis.hall}</div>
              </div>
              <div style="background:var(--bg-input); padding:8px; border-radius:8px;">
                <div style="font-size:10px; color:var(--text-muted); font-weight:700;">COUNSELLOR</div>
                <div style="font-size:12px; font-weight:700;">${vis.counsellor}</div>
              </div>
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-color-subtle); padding-top:8px;">
              <span style="font-size:11.5px; color:#fbbf24; font-weight:700;">⏰ ${vis.time}</span>
              <div style="display:flex; gap:6px;">
                <button class="btn-small act-mark-converted" data-visit-id="${vis.id}" style="color:#10b981;">Mark Converted</button>
              </div>
            </div>
          </div>
        `).join('')}
      </div>
    `;

    mainViewport.innerHTML = html;

    document.querySelectorAll('.act-mark-converted').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-visit-id');
        const v = state.visits.find(x => x.id === id);
        if (v) {
          v.status = 'CONVERTED';
          saveState();
          showToast(`Marked ${v.guestName} as Converted! Ready for Seniority recording.`);
          renderVisits();
        }
      });
    });
  }

  // TAB 4: DAILY TASKS & DISTRIBUTOR CLOSING
  function renderTasks() {
    const completedCount = state.tasks.filter(t => t.completed).length;

    let html = `
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div>
          <div style="font-size:17px; font-weight:900;">Daily Distributor Checklist</div>
          <div style="font-size:11.5px; color:var(--text-muted);">${completedCount}/${state.tasks.length} tasks completed today</div>
        </div>
        <button id="btn-add-task-top" class="btn-small">+ Add Task</button>
      </div>

      <!-- Closing Workflow Action Banner -->
      <div class="card" style="background:linear-gradient(90deg, #1e1b4b, #312e81); border-color:#6366f1;">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-size:13.5px; font-weight:800; color:white;">End-of-Day Task Closing</div>
            <div style="font-size:11px; color:#c7d2fe;">Submit field report with OTP verification</div>
          </div>
          <button id="btn-open-closing" class="install-btn" style="background:#fbbf24; color:#0f172a;">Close Day</button>
        </div>
      </div>

      <!-- Task Items -->
      <div class="list-container">
        ${state.tasks.map(task => `
          <div class="card" style="padding:12px; border-left:4px solid ${task.priority === 'HIGH' ? 'var(--rose)' : task.priority === 'MEDIUM' ? 'var(--amber)' : 'var(--emerald)'};">
            <div style="display:flex; align-items:flex-start; gap:12px;">
              <input type="checkbox" class="task-checkbox" data-task-id="${task.id}" ${task.completed ? 'checked' : ''} style="width:20px; height:20px; margin-top:2px; cursor:pointer;">
              <div style="flex:1;">
                <div style="font-size:13.5px; font-weight:700; text-decoration:${task.completed ? 'line-through' : 'none'}; color:${task.completed ? 'var(--text-dim)' : 'var(--text-main)'};">
                  ${task.title}
                </div>
                <div style="display:flex; gap:8px; margin-top:4px; font-size:11px; color:var(--text-muted);">
                  <span>⏰ ${task.dueTime}</span>
                  <span>•</span>
                  <span>📁 ${task.category}</span>
                  <span>•</span>
                  <span style="color:${task.priority === 'HIGH' ? 'var(--rose)' : task.priority === 'MEDIUM' ? 'var(--amber)' : 'var(--emerald)'}; font-weight:700;">${task.priority}</span>
                </div>
              </div>
            </div>
          </div>
        `).join('')}
      </div>

      <!-- Previous Closings Log -->
      <div class="card" style="margin-top:10px;">
        <div style="font-size:13px; font-weight:800;">Recent Daily Closing Records</div>
        ${state.closingRecords.map(rec => `
          <div style="display:flex; justify-content:space-between; align-items:center; padding:6px 0; border-bottom:1px solid var(--border-color-subtle); font-size:11.5px;">
            <div>
              <span style="font-weight:700;">${rec.date}</span> — ${rec.remarks}
            </div>
            <span class="badge badge-converted">OTP: ${rec.verifiedOtp}</span>
          </div>
        `).join('')}
      </div>
    `;

    mainViewport.innerHTML = html;

    // Checkbox events
    document.querySelectorAll('.task-checkbox').forEach(chk => {
      chk.addEventListener('change', () => {
        const id = chk.getAttribute('data-task-id');
        const t = state.tasks.find(x => x.id === id);
        if (t) {
          t.completed = chk.checked;
          saveState();
          showToast(`Task ${chk.checked ? 'completed!' : 'marked pending'}`);
          renderTasks();
        }
      });
    });

    document.getElementById('btn-add-task-top').addEventListener('click', openAddTaskModal);
    document.getElementById('btn-open-closing').addEventListener('click', openDayClosingModal);
  }

  // TAB 5: SENIORITY & SALES TRACKER
  function renderSales() {
    const isSuperAdmin = state.currentRole === 'SuperAdmin';
    const totalAmount = state.sales.reduce((acc, s) => acc + s.amount, 0);

    let html = `
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div>
          <div style="font-size:17px; font-weight:900;">Seniority & Sales Tracker</div>
          <div style="font-size:11.5px; color:var(--text-muted);">${formatINR(totalAmount)} verified across all teams</div>
        </div>
        <button id="btn-record-sale-top" class="btn-primary" style="width:auto; height:36px; padding:0 14px; font-size:12px;">+ Record Sale</button>
      </div>

      <div class="list-container">
        ${state.sales.map(s => `
          <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:flex-start;">
              <div>
                <div style="font-size:15px; font-weight:800;">${s.candidateName}</div>
                <div style="font-size:12px; color:var(--text-muted);">📞 ${s.phone} • Team: <strong>${s.team}</strong></div>
                <div style="font-size:11px; color:var(--primary-light); margin-top:2px;">Distributor: ${s.agent}</div>
              </div>
              <div style="text-align:right;">
                <div style="font-size:16px; font-weight:900; color:#10b981;">${formatINR(s.amount)}</div>
                <span class="badge ${s.status === 'APPROVED' ? 'badge-converted' : 'badge-scheduled'}">${s.status}</span>
              </div>
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; background:var(--bg-input); padding:8px 10px; border-radius:8px; font-size:11px; color:var(--text-muted);">
              <span>Mode: <strong>${s.mode}</strong> (${s.ref})</span>
              <span>Receipt: <strong>${s.receipt}</strong></span>
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-color-subtle); padding-top:8px;">
              <span style="font-size:11px; color:var(--text-muted);">Date: ${s.date}</span>
              <div style="display:flex; gap:6px;">
                <button class="btn-small act-view-proof" data-tx-id="${s.id}">View Proof</button>
                ${isSuperAdmin && s.status === 'PENDING' ? `
                  <button class="btn-small act-approve-sale" data-tx-id="${s.id}" style="background:#10b981; color:white; border-color:#10b981;">Approve</button>
                ` : ''}
              </div>
            </div>
          </div>
        `).join('')}
      </div>
    `;

    mainViewport.innerHTML = html;

    document.getElementById('btn-record-sale-top').addEventListener('click', openRecordSaleModal);

    document.querySelectorAll('.act-view-proof').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-tx-id');
        openProofModal(id);
      });
    });

    document.querySelectorAll('.act-approve-sale').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-tx-id');
        const s = state.sales.find(x => x.id === id);
        if (s) {
          s.status = 'APPROVED';
          saveState();
          showToast(`Approved Seniority transaction for ${s.candidateName}!`);
          renderSales();
        }
      });
    });
  }

  // TAB 6: TEAMS & AUDIT HIERARCHY
  function renderTeams() {
    let html = `
      <div>
        <div style="font-size:17px; font-weight:900;">Smart Group Teams</div>
        <div style="font-size:11.5px; color:var(--text-muted);">Dealer Hierarchy & Performance Benchmarks</div>
      </div>

      <div class="list-container">
        ${state.teams.map(team => {
          const progress = Math.min(100, Math.round((team.totalRevenue / team.target) * 100));
          return `
            <div class="card">
              <div style="display:flex; justify-content:space-between; align-items:flex-start;">
                <div>
                  <div style="font-size:16px; font-weight:900;">${team.name}</div>
                  <div style="font-size:12px; color:var(--text-muted);">Leader: <strong>${team.leader}</strong> • ${team.distributorsCount} Active Distributors</div>
                </div>
                <span class="badge" style="background:var(--primary-900); color:var(--primary-light);">${progress}% Target</span>
              </div>

              <!-- Progress Bar -->
              <div style="margin-top:6px;">
                <div style="display:flex; justify-content:space-between; font-size:11px; margin-bottom:4px;">
                  <span style="color:var(--text-muted);">Current: <strong style="color:#10b981;">${formatINR(team.totalRevenue)}</strong></span>
                  <span style="color:var(--text-muted);">Target: ${formatINR(team.target)}</span>
                </div>
                <div style="width:100%; height:6px; background:var(--bg-input); border-radius:3px; overflow:hidden;">
                  <div style="width:${progress}%; height:100%; background:linear-gradient(90deg, #4f46e5, #10b981); border-radius:3px;"></div>
                </div>
              </div>
            </div>
          `;
        }).join('')}
      </div>

      <!-- App Diagnostics Card -->
      <div class="card" style="margin-top:10px;">
        <div style="font-size:13.5px; font-weight:800;">Device & PWA Diagnostics</div>
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px; font-size:11px; color:var(--text-muted); margin-top:4px;">
          <div>Device Profile: <strong>Google Pixel 11</strong></div>
          <div>Display Mode: <strong>Standalone</strong></div>
          <div>Storage Engine: <strong>Room / Local Cache</strong></div>
          <div>Dealer Network: <strong>Tranz India</strong></div>
        </div>
      </div>
    `;

    mainViewport.innerHTML = html;
  }

  // MODALS

  function openModal(contentHtml) {
    modalSheetContent.innerHTML = `
      <div class="sheet-handle"></div>
      ${contentHtml}
    `;
    modalContainer.classList.add('open');
  }

  function closeModal() {
    modalContainer.classList.remove('open');
  }

  // 1. Role Switcher Modal
  function openRoleModal() {
    const roles = ['Distributor', 'Team Leader', 'Counsellor', 'Telecaller', 'SuperAdmin'];
    let html = `
      <div class="sheet-title">Switch Active Role</div>
      <div style="font-size:12px; color:var(--text-muted);">Preview access privileges and team scopes:</div>
      <div style="display:flex; flex-direction:column; gap:8px; margin-top:6px;">
        ${roles.map(r => `
          <button class="card btn-role-choice" data-role="${r}" style="cursor:pointer; text-align:left; border-color:${state.currentRole === r ? 'var(--primary)' : 'var(--border-color)'}; background:${state.currentRole === r ? 'var(--primary-900)' : 'var(--bg-surface-elevated)'};">
            <div style="font-size:14px; font-weight:800; color:${state.currentRole === r ? 'var(--primary-light)' : 'var(--text-main)'};">${r}</div>
            <div style="font-size:11px; color:var(--text-muted); margin-top:2px;">
              ${r === 'Distributor' ? 'Daily field visits, lead contacts, and closing reports' :
                r === 'SuperAdmin' ? 'Global approvals, financial auditing, and team allocations' :
                r === 'Team Leader' ? 'Team metrics, member follow-up, and daily supervision' :
                r === 'Counsellor' ? 'Counselling hall briefings and conversion documentation' :
                'Lead pipeline telecalling and visit scheduling'}
            </div>
          </button>
        `).join('')}
      </div>
      <button id="btn-close-modal" class="btn-secondary" style="margin-top:8px;">Cancel</button>
    `;

    openModal(html);

    document.querySelectorAll('.btn-role-choice').forEach(btn => {
      btn.addEventListener('click', () => {
        const role = btn.getAttribute('data-role');
        state.currentRole = role;
        if (role === 'Distributor') state.currentUserName = 'Priya Sharma';
        else if (role === 'Team Leader') state.currentUserName = 'Rajesh Kumar';
        else if (role === 'Counsellor') state.currentUserName = 'Arun Prakash';
        else if (role === 'Telecaller') state.currentUserName = 'Kavitha';
        else if (role === 'SuperAdmin') state.currentUserName = 'Command SuperAdmin';

        saveState();
        updateRoleUI();
        closeModal();
        showToast(`Role switched to ${role}`);
        renderTab(state.activeTab);
      });
    });

    document.getElementById('btn-close-modal').addEventListener('click', closeModal);
  }

  // 2. Add New Lead Modal
  function openAddLeadModal() {
    let html = `
      <div class="sheet-title">Add New Candidate / Lead</div>
      <div class="form-group">
        <label class="form-label">Full Name</label>
        <input id="new-lead-name" type="text" class="form-input" placeholder="e.g. Anandha Krishnan">
      </div>
      <div class="form-group">
        <label class="form-label">Phone Number</label>
        <input id="new-lead-phone" type="tel" class="form-input" placeholder="e.g. +91 98400 12345">
      </div>
      <div class="form-group">
        <label class="form-label">City / Town</label>
        <input id="new-lead-city" type="text" class="form-input" placeholder="e.g. Coimbatore">
      </div>
      <div class="form-group">
        <label class="form-label">Interested Program</label>
        <select id="new-lead-prog" class="form-input">
          <option value="Premium Directorship">Premium Directorship</option>
          <option value="Senior Associate">Senior Associate</option>
          <option value="Executive Associate">Executive Associate</option>
          <option value="Seniority Package A">Seniority Package A (₹25,000)</option>
          <option value="Seniority Package B">Seniority Package B (₹50,000)</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Field Notes / Remarks</label>
        <input id="new-lead-notes" type="text" class="form-input" placeholder="Initial discussion summary">
      </div>
      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-lead" class="btn-primary">Save to SQLite Cache</button>
        <button id="btn-cancel-lead" class="btn-secondary" style="width:100px;">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-save-lead').addEventListener('click', () => {
      const name = document.getElementById('new-lead-name').value.trim();
      const phone = document.getElementById('new-lead-phone').value.trim();
      const city = document.getElementById('new-lead-city').value.trim() || 'Tamil Nadu';
      const program = document.getElementById('new-lead-prog').value;
      const notes = document.getElementById('new-lead-notes').value.trim();

      if (!name || !phone) {
        showToast('Please enter candidate name and phone number');
        return;
      }

      const newLead = {
        id: 'lead-' + Date.now(),
        name,
        phone,
        city,
        program,
        status: 'NEW',
        telecaller: state.currentUserName,
        notes: notes || 'Direct field entry via Pixel 11 PWA'
      };

      state.leads.unshift(newLead);
      saveState();
      updateRoleUI();
      updateCacheBarUI();
      closeModal();
      showToast(`Lead for ${name} added to offline cache!`);
      if (state.activeTab === 'leads' || state.activeTab === 'dashboard') {
        renderTab(state.activeTab);
      }
    });

    document.getElementById('btn-cancel-lead').addEventListener('click', closeModal);
  }

  // 3. Convert Lead to Confirmed Guest Visit Modal
  function openConvertLeadModal(leadId) {
    const lead = state.leads.find(l => l.id === leadId);
    if (!lead) return;

    let html = `
      <div class="sheet-title">Schedule Counselling Hall Visit</div>
      <div style="font-size:12px; color:var(--text-muted);">Candidate: <strong>${lead.name}</strong> (${lead.phone})</div>
      <div class="form-group">
        <label class="form-label">Counselling Hall Room</label>
        <select id="visit-hall" class="form-input">
          <option value="Hall A (Ground Floor)">Hall A (Ground Floor)</option>
          <option value="Hall B (First Floor)">Hall B (First Floor)</option>
          <option value="Executive Suite 1">Executive Suite 1</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Assigned Senior Counsellor</label>
        <select id="visit-counsellor" class="form-input">
          <option value="Arun Prakash">Arun Prakash</option>
          <option value="Deepak Raj">Deepak Raj</option>
          <option value="Rajesh Kumar">Rajesh Kumar</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Scheduled Visit Time</label>
        <input id="visit-time" type="text" class="form-input" value="Today, 03:30 PM">
      </div>
      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-confirm-visit" class="btn-primary">Confirm & Allocate Room</button>
        <button id="btn-cancel-visit" class="btn-secondary" style="width:100px;">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-confirm-visit').addEventListener('click', () => {
      const hall = document.getElementById('visit-hall').value;
      const counsellor = document.getElementById('visit-counsellor').value;
      const time = document.getElementById('visit-time').value.trim() || 'Today, 03:30 PM';

      lead.status = 'FOLLOW_UP';

      state.visits.unshift({
        id: 'vis-' + Date.now(),
        guestName: lead.name,
        phone: lead.phone,
        hall,
        counsellor,
        time,
        status: 'SCHEDULED',
        remarks: 'Allocated from telecalling pipeline'
      });

      saveState();
      updateCacheBarUI();
      closeModal();
      showToast(`Scheduled visit for ${lead.name} in ${hall}`);
      renderTab(state.activeTab);
    });

    document.getElementById('btn-cancel-visit').addEventListener('click', closeModal);
  }

  // 4. Record Seniority Sale Modal
  function openRecordSaleModal() {
    let html = `
      <div class="sheet-title">Record Seniority Closure</div>
      <div class="form-group">
        <label class="form-label">Candidate Name</label>
        <input id="sale-candidate" type="text" class="form-input" placeholder="e.g. Priya Sundaram">
      </div>
      <div class="form-group">
        <label class="form-label">Phone Number</label>
        <input id="sale-phone" type="tel" class="form-input" placeholder="e.g. +91 99444 11223">
      </div>
      <div class="form-group">
        <label class="form-label">Seniority Amount (₹)</label>
        <select id="sale-amount" class="form-input">
          <option value="25000">₹25,000 (Standard Seniority)</option>
          <option value="35000">₹35,000 (Executive Seniority)</option>
          <option value="50000">₹50,000 (Master Seniority)</option>
          <option value="75000">₹75,000 (Director Package)</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Payment Mode</label>
        <select id="sale-mode" class="form-input">
          <option value="UPI">UPI (Google Pay / PhonePe / Paytm)</option>
          <option value="NEFT">NEFT / Bank Transfer</option>
          <option value="Cash">Cash Deposit</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Transaction Reference / UTR Number</label>
        <input id="sale-ref" type="text" class="form-input" placeholder="e.g. UPI-9988221100">
      </div>
      <div class="form-group">
        <label class="form-label">Receipt Number</label>
        <input id="sale-receipt" type="text" class="form-input" value="REC-2026-${Math.floor(100 + Math.random() * 900)}">
      </div>
      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-sale" class="btn-primary">Record & Request Approval</button>
        <button id="btn-cancel-sale" class="btn-secondary" style="width:100px;">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-save-sale').addEventListener('click', () => {
      const candidateName = document.getElementById('sale-candidate').value.trim();
      const phone = document.getElementById('sale-phone').value.trim();
      const amount = Number(document.getElementById('sale-amount').value);
      const mode = document.getElementById('sale-mode').value;
      const ref = document.getElementById('sale-ref').value.trim() || 'REF-' + Date.now().toString().slice(-6);
      const receipt = document.getElementById('sale-receipt').value.trim();

      if (!candidateName) {
        showToast('Please enter candidate name');
        return;
      }

      const newTx = {
        id: 'tx-' + Date.now(),
        candidateName,
        phone: phone || '+91 98000 00000',
        amount,
        mode,
        ref,
        receipt,
        agent: state.currentUserName,
        team: state.currentTeam,
        date: new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }),
        status: state.currentRole === 'SuperAdmin' ? 'APPROVED' : 'PENDING'
      };

      state.sales.unshift(newTx);

      // Add live celebration
      state.notifications.unshift({
        id: 'notif-' + Date.now(),
        title: 'New Seniority Closure Recorded',
        text: `${candidateName} paid ${formatINR(amount)} (${state.currentTeam})`,
        time: 'Just now',
        read: false
      });

      saveState();
      updateRoleUI();
      updateCacheBarUI();
      closeModal();
      showToast(`Seniority of ${formatINR(amount)} recorded successfully!`);
      renderTab(state.activeTab);
    });

    document.getElementById('btn-cancel-sale').addEventListener('click', closeModal);
  }

  // 5. Day Task Closing Modal with OTP
  function openDayClosingModal() {
    const total = state.tasks.length;
    const completed = state.tasks.filter(t => t.completed).length;

    let html = `
      <div class="sheet-title">Daily Task Closing & Verification</div>
      <div style="font-size:12px; color:var(--text-muted);">
        Distributor: <strong>${state.currentUserName}</strong> • Progress: <strong>${completed}/${total} completed</strong>
      </div>
      <div class="form-group" style="margin-top:6px;">
        <label class="form-label">Distributor Field Remarks / Summary</label>
        <textarea id="closing-remarks" class="form-input" style="height:64px; padding:8px 12px; resize:none;" placeholder="Summary of today's field calls, candidate responses, and cash deposits..."></textarea>
      </div>
      <div class="form-group">
        <label class="form-label">Closing Verification Security OTP</label>
        <div style="display:flex; gap:8px;">
          <input id="closing-otp" type="number" class="form-input" placeholder="Enter 4-digit OTP" style="font-size:18px; letter-spacing:4px; text-align:center;">
          <button id="btn-get-otp" class="btn-small" style="width:120px;">Use Mock OTP (1234)</button>
        </div>
      </div>
      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-submit-closing" class="btn-primary" style="background:#f59e0b; color:#0f172a;">Verify & Submit Closing</button>
        <button id="btn-cancel-closing" class="btn-secondary" style="width:100px;">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-get-otp').addEventListener('click', () => {
      document.getElementById('closing-otp').value = '1234';
      showToast('Mock OTP 1234 auto-filled');
    });

    document.getElementById('btn-submit-closing').addEventListener('click', () => {
      const remarks = document.getElementById('closing-remarks').value.trim() || 'All tasks completed satisfactorily';
      const otp = document.getElementById('closing-otp').value.trim();

      if (!otp) {
        showToast('Please enter 4-digit verification OTP');
        return;
      }

      state.closingRecords.unshift({
        date: new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }),
        closedBy: state.currentUserName,
        tasksDone: completed,
        totalTasks: total,
        remarks,
        verifiedOtp: otp
      });

      saveState();
      closeModal();
      showToast('Daily closing successfully verified and stored!');
      renderTab(state.activeTab);
    });

    document.getElementById('btn-cancel-closing').addEventListener('click', closeModal);
  }

  // 6. Add Task Modal
  function openAddTaskModal() {
    let html = `
      <div class="sheet-title">Add Distributor Field Task</div>
      <div class="form-group">
        <label class="form-label">Task Title</label>
        <input id="new-task-title" type="text" class="form-input" placeholder="e.g. Verify KYC for Ramesh Patel">
      </div>
      <div class="form-group">
        <label class="form-label">Priority</label>
        <select id="new-task-prio" class="form-input">
          <option value="HIGH">HIGH Priority</option>
          <option value="MEDIUM">MEDIUM Priority</option>
          <option value="NORMAL">NORMAL Priority</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Target Time</label>
        <input id="new-task-time" type="text" class="form-input" value="05:00 PM">
      </div>
      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-task" class="btn-primary">Add Task</button>
        <button id="btn-cancel-task" class="btn-secondary" style="width:100px;">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-save-task').addEventListener('click', () => {
      const title = document.getElementById('new-task-title').value.trim();
      const priority = document.getElementById('new-task-prio').value;
      const dueTime = document.getElementById('new-task-time').value.trim() || '05:00 PM';

      if (!title) {
        showToast('Please enter task title');
        return;
      }

      state.tasks.unshift({
        id: 'task-' + Date.now(),
        title,
        priority,
        dueTime,
        completed: false,
        category: 'Field Action'
      });

      saveState();
      closeModal();
      showToast('New task added!');
      renderTasks();
    });

    document.getElementById('btn-cancel-task').addEventListener('click', closeModal);
  }

  // 7. Lead Detail Modal
  function openLeadDetailModal(leadId) {
    const lead = state.leads.find(l => l.id === leadId);
    if (!lead) return;

    let html = `
      <div class="sheet-title">${lead.name}</div>
      <div style="font-size:12px; color:var(--text-muted);">Candidate details & telecalling actions:</div>
      <div style="display:flex; flex-direction:column; gap:8px; margin-top:6px;">
        <div style="background:var(--bg-input); padding:10px; border-radius:10px;">
          <div><strong>Phone:</strong> ${lead.phone}</div>
          <div><strong>City:</strong> ${lead.city}</div>
          <div><strong>Program:</strong> ${lead.program}</div>
          <div><strong>Telecaller:</strong> ${lead.telecaller}</div>
          <div><strong>Status:</strong> <span class="badge badge-${lead.status.toLowerCase().replace('_', '')}">${lead.status}</span></div>
        </div>
      </div>
      <div class="form-group" style="margin-top:6px;">
        <label class="form-label">Update Status</label>
        <select id="lead-update-status" class="form-input">
          ${['NEW', 'CONTACTED', 'FOLLOW_UP', 'CONVERTED', 'LOST'].map(s => `
            <option value="${s}" ${lead.status === s ? 'selected' : ''}>${s.replace('_', ' ')}</option>
          `).join('')}
        </select>
      </div>
      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-lead-status" class="btn-primary">Update Status</button>
        <button id="btn-close-lead-detail" class="btn-secondary" style="width:100px;">Close</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-save-lead-status').addEventListener('click', () => {
      lead.status = document.getElementById('lead-update-status').value;
      saveState();
      closeModal();
      showToast(`Updated status for ${lead.name}`);
      renderLeads();
    });

    document.getElementById('btn-close-lead-detail').addEventListener('click', closeModal);
  }

  // 8. Proof Viewer Modal
  function openProofModal(txId) {
    const tx = state.sales.find(x => x.id === txId);
    if (!tx) return;

    let html = `
      <div class="sheet-title">Closure Proof Verification</div>
      <div style="font-size:12px; color:var(--text-muted);">${tx.candidateName} • ${formatINR(tx.amount)}</div>
      <div style="background:#1e1b4b; border:2px dashed #6366f1; border-radius:12px; padding:24px; text-align:center; margin-top:10px;">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" style="margin:0 auto 8px auto;">
          <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
          <circle cx="8.5" cy="8.5" r="1.5"></circle>
          <polyline points="21 15 16 10 5 21"></polyline>
        </svg>
        <div style="font-size:13px; font-weight:800; color:white;">Tranz India Payment Receipt</div>
        <div style="font-size:11px; color:#c7d2fe; margin-top:4px;">Receipt: ${tx.receipt} • Ref: ${tx.ref}</div>
        <div style="font-size:12px; color:#10b981; font-weight:800; margin-top:8px;">VERIFIED BANK CREDIT (${tx.mode})</div>
      </div>
      <button id="btn-close-proof" class="btn-secondary" style="margin-top:12px;">Close</button>
    `;

    openModal(html);
    document.getElementById('btn-close-proof').addEventListener('click', closeModal);
  }

  // 9. Notifications Modal
  function openNotificationsModal() {
    let html = `
      <div class="sheet-title">Smart Group Notifications</div>
      <div style="font-size:12px; color:var(--text-muted);">Real-time dealer broadcasts & security alerts:</div>
      <div style="display:flex; flex-direction:column; gap:8px; margin-top:6px; max-height:300px; overflow-y:auto;">
        ${state.notifications.map(n => `
          <div class="card" style="padding:10px; background:${n.read ? 'var(--bg-input)' : 'var(--bg-surface-elevated)'}; border-color:${n.read ? 'var(--border-color-subtle)' : 'var(--primary)'};">
            <div style="display:flex; justify-content:space-between;">
              <span style="font-size:13px; font-weight:800; color:${n.read ? 'var(--text-main)' : 'var(--primary-light)'};">${n.title}</span>
              <span style="font-size:10px; color:var(--text-dim);">${n.time}</span>
            </div>
            <div style="font-size:11.5px; color:var(--text-muted); margin-top:2px;">${n.text}</div>
          </div>
        `).join('')}
      </div>
      <button id="btn-mark-all-read" class="btn-primary" style="margin-top:8px;">Mark All as Read</button>
    `;

    openModal(html);

    document.getElementById('btn-mark-all-read').addEventListener('click', () => {
      state.notifications.forEach(n => n.read = true);
      saveState();
      updateRoleUI();
      closeModal();
      showToast('All notifications marked as read');
    });
  }

  // Start the application
  window.addEventListener('DOMContentLoaded', init);

})();

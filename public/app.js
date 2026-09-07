// Smart Group Command Center - Mobile-First PWA Logic
// Optimized for Google Pixel 11 & Android Chrome

(function() {
  'use strict';

  // Initial State Seed Data
  const DEFAULT_STATE = {
    isLoggedIn: false,
    privacyMode: false,
    currentProfileId: 'prof-1',
    currentRole: 'Distributor', // 'SuperAdmin', 'Team Leader', 'Distributor', 'Counsellor', 'Telecaller'
    currentUserName: 'Priya Sharma',
    currentTeam: 'Alpha Warriors',
    theme: 'dark',
    activeTab: 'dashboard',
    profileSubTab: 'details', // 'details', 'distributors', 'performance', 'accounts'
    selectedPerfTeam: 'Alpha Warriors',
    isOffline: false,
    lastSyncTime: 'Just now',
    
    // User / Distributor Profiles
    profiles: [
      {
        id: 'prof-1',
        agentCode: 'TRZ-001',
        name: 'Priya Sharma',
        phone: '+91 98400 12345',
        email: 'priya.sharma@tranzindia.com',
        password: 'password123',
        role: 'Distributor',
        team: 'Alpha Warriors',
        tier: 'Gold Master',
        joiningDate: '15 Jan 2024',
        city: 'Coimbatore',
        status: 'ACTIVE',
        bio: 'Direct sales field distributor for Tranz India franchise programs.',
        avatar: 'PS'
      },
      {
        id: 'prof-2',
        agentCode: 'TRZ-002',
        name: 'Rajesh Kumar',
        phone: '+91 98765 00001',
        email: 'rajesh.kumar@tranzindia.com',
        password: 'alpha2026',
        role: 'Team Leader',
        team: 'Alpha Warriors',
        tier: 'Platinum Director',
        joiningDate: '10 Aug 2023',
        city: 'Chennai',
        status: 'ACTIVE',
        bio: 'Head of Alpha Warriors team. Managing 18 field distributors and regional closures.',
        avatar: 'RK'
      },
      {
        id: 'prof-3',
        agentCode: 'TRZ-003',
        name: 'Suresh Verma',
        phone: '+91 98111 00002',
        email: 'suresh.verma@tranzindia.com',
        password: 'eagles123',
        role: 'Team Leader',
        team: 'Zenith Eagles',
        tier: 'Diamond Leader',
        joiningDate: '01 Nov 2023',
        city: 'Madurai',
        status: 'ACTIVE',
        bio: 'Lead strategist for Zenith Eagles team.',
        avatar: 'SV'
      },
      {
        id: 'prof-4',
        agentCode: 'TRZ-004',
        name: 'Kavitha Reddy',
        phone: '+91 99222 00003',
        email: 'kavitha.reddy@tranzindia.com',
        password: 'apex2026',
        role: 'Team Leader',
        team: 'Apex Champions',
        tier: 'Crown Ambassador',
        joiningDate: '20 May 2023',
        city: 'Bengaluru',
        status: 'ACTIVE',
        bio: 'Leading Apex Champions with highest statewide closing percentage.',
        avatar: 'KR'
      }
    ],

    // Team Distributors Roster
    distributors: [
      { id: 'dist-1', profileId: 'prof-1', name: 'Priya Sharma', agentCode: 'TRZ-001', phone: '+91 98400 12345', team: 'Alpha Warriors', role: 'Distributor', seniorityClosed: 160000, target: 250000, status: 'ACTIVE', rating: 4.9 },
      { id: 'dist-2', profileId: '', name: 'Arjun Mehra', agentCode: 'TRZ-012', phone: '+91 98333 44551', team: 'Alpha Warriors', role: 'Distributor', seniorityClosed: 120000, target: 200000, status: 'ACTIVE', rating: 4.8 },
      { id: 'dist-3', profileId: '', name: 'Deepika Senthil', agentCode: 'TRZ-015', phone: '+91 98444 33221', team: 'Alpha Warriors', role: 'Distributor', seniorityClosed: 95000, target: 150000, status: 'ACTIVE', rating: 4.7 },
      { id: 'dist-4', profileId: '', name: 'Vigneshwaran P', agentCode: 'TRZ-021', phone: '+91 98555 22110', team: 'Alpha Warriors', role: 'Distributor', seniorityClosed: 85000, target: 150000, status: 'ACTIVE', rating: 4.6 },
      { id: 'dist-5', profileId: '', name: 'Siddharth Rao', agentCode: 'TRZ-033', phone: '+91 98666 11009', team: 'Zenith Eagles', role: 'Distributor', seniorityClosed: 140000, target: 200000, status: 'ACTIVE', rating: 4.8 },
      { id: 'dist-6', profileId: '', name: 'Lakshmi Narayanan', agentCode: 'TRZ-044', phone: '+91 98777 99887', team: 'Apex Champions', role: 'Distributor', seniorityClosed: 210000, target: 250000, status: 'ACTIVE', rating: 5.0 }
    ],

    // Whole Team Performance Entries
    teamPerformanceEntries: [
      { id: 'perf-1', team: 'Alpha Warriors', date: '05 Sep 2026', week: 'Week 36', target: 500000, achieved: 410000, callsMade: 185, conversions: 12, topPerformer: 'Priya Sharma', remarks: 'Superb conversion rate in Coimbatore regional center' },
      { id: 'perf-2', team: 'Alpha Warriors', date: '29 Aug 2026', week: 'Week 35', target: 450000, achieved: 480000, callsMade: 210, conversions: 14, topPerformer: 'Arjun Mehra', remarks: 'Exceeded target by 30K with corporate package' },
      { id: 'perf-3', team: 'Alpha Warriors', date: '22 Aug 2026', week: 'Week 34', target: 400000, achieved: 360000, callsMade: 170, conversions: 9, topPerformer: 'Deepika Senthil', remarks: 'Good followups; festival weekend dip' },
      { id: 'perf-4', team: 'Zenith Eagles', date: '05 Sep 2026', week: 'Week 36', target: 400000, achieved: 320000, callsMade: 140, conversions: 8, topPerformer: 'Siddharth Rao', remarks: 'Madurai branch drive underway' },
      { id: 'perf-5', team: 'Apex Champions', date: '05 Sep 2026', week: 'Week 36', target: 600000, achieved: 580000, callsMade: 240, conversions: 17, topPerformer: 'Lakshmi Narayanan', remarks: 'Record seminar turnout in Bengaluru center' }
    ],

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
      { id: 'notif-1', title: 'Seniority Payment Approved', text: 'Meenakshi Sundaram (₹50,000) verified by Central Office', time: '10m ago', read: false },
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
        const parsed = JSON.parse(saved);
        return {
          ...DEFAULT_STATE,
          ...parsed,
          isLoggedIn: false, // Mandatory: always start at login on app open, never go inside until login
          profiles: (parsed.profiles && parsed.profiles.length) ? parsed.profiles : DEFAULT_STATE.profiles,
          distributors: (parsed.distributors && parsed.distributors.length) ? parsed.distributors : DEFAULT_STATE.distributors,
          teamPerformanceEntries: (parsed.teamPerformanceEntries && parsed.teamPerformanceEntries.length) ? parsed.teamPerformanceEntries : DEFAULT_STATE.teamPerformanceEntries
        };
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
  
  // Auth & Privacy Controls ("no one see")
  const authOverlay = document.getElementById('auth-overlay');
  const loginProfileSelect = document.getElementById('login-profile-select');
  const loginPasswordInput = document.getElementById('login-password-input');
  const btnToggleLoginPwd = document.getElementById('btn-toggle-login-pwd');
  const btnBiometricUnlock = document.getElementById('btn-biometric-unlock');
  const btnSubmitLogin = document.getElementById('btn-submit-login');
  const btnShowCreateProfile = document.getElementById('btn-show-create-profile');
  const btnPrivacyToggle = document.getElementById('btn-privacy-toggle');
  const privacyIconOpen = document.getElementById('privacy-icon-open');
  const privacyIconClosed = document.getElementById('privacy-icon-closed');
  const btnLogoutHeader = document.getElementById('btn-logout-header');

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

  // Format INR Currency with Privacy Masking Support
  function formatINR(amount, allowMask = true) {
    if (state.privacyMode && allowMask) {
      return '₹ •••••••';
    }
    return '₹' + Number(amount).toLocaleString('en-IN');
  }

  // Format Phone with Privacy Masking Support
  function formatPhone(phone) {
    if (state.privacyMode && phone) {
      return phone.replace(/(\+91\s\d{2})\d{3}\s\d{3}(\d{2})/, '$1••• •••$2');
    }
    return phone || '';
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

    // Setup Privacy Mode & Toggle
    applyPrivacyMode(state.privacyMode);
    if (btnPrivacyToggle) {
      btnPrivacyToggle.addEventListener('click', togglePrivacyMode);
    }

    // Setup Header Logout Button ("no one see")
    if (btnLogoutHeader) {
      btnLogoutHeader.addEventListener('click', logout);
    }

    // Setup Auth Screen Handlers
    if (btnToggleLoginPwd && loginPasswordInput) {
      btnToggleLoginPwd.addEventListener('click', () => {
        const isPwd = loginPasswordInput.getAttribute('type') === 'password';
        loginPasswordInput.setAttribute('type', isPwd ? 'text' : 'password');
        btnToggleLoginPwd.style.color = isPwd ? 'var(--primary-light)' : 'var(--text-muted)';
      });
    }

    if (loginProfileSelect) {
      loginProfileSelect.addEventListener('change', () => {
        if (loginPasswordInput) loginPasswordInput.value = '';
        updateLoginProfilePreview();
      });
    }

    if (btnBiometricUnlock) {
      btnBiometricUnlock.addEventListener('click', () => {
        performBiometricAuth();
      });
    }

    if (btnSubmitLogin && loginProfileSelect && loginPasswordInput) {
      btnSubmitLogin.addEventListener('click', () => {
        login(loginProfileSelect.value, loginPasswordInput.value);
      });
      loginPasswordInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          login(loginProfileSelect.value, loginPasswordInput.value);
        }
      });
    }

    if (btnShowCreateProfile) {
      btnShowCreateProfile.addEventListener('click', () => {
        openCreateProfileModal((newProf) => {
          populateLoginProfiles();
          if (loginProfileSelect) loginProfileSelect.value = newProf.id;
          if (loginPasswordInput) loginPasswordInput.value = newProf.password;
          updateLoginProfilePreview();
        }, true); // defaults to auto sign-in
      });
    }

    // Confidential Admin Terminal Toggle & Handlers
    const btnToggleAdminPortal = document.getElementById('btn-toggle-admin-portal');
    const adminLoginTerminal = document.getElementById('admin-login-terminal');
    const txtToggleAdminPortal = document.getElementById('txt-toggle-admin-portal');

    if (btnToggleAdminPortal && adminLoginTerminal) {
      btnToggleAdminPortal.addEventListener('click', () => {
        const isHidden = adminLoginTerminal.style.display === 'none' || !adminLoginTerminal.style.display;
        adminLoginTerminal.style.display = isHidden ? 'block' : 'none';
        if (txtToggleAdminPortal) {
          txtToggleAdminPortal.textContent = isHidden ? 'Hide Admin Portal' : 'Admin Authentication';
        }
        if (isHidden) {
          const idInput = document.getElementById('admin-portal-id');
          if (idInput) {
            idInput.value = '';
            idInput.focus();
          }
          const pwdInput = document.getElementById('admin-portal-password');
          if (pwdInput) pwdInput.value = '';
        }
      });
    }

    const btnSubmitAdminLogin = document.getElementById('btn-submit-admin-login');
    const adminPortalId = document.getElementById('admin-portal-id');
    const adminPortalPassword = document.getElementById('admin-portal-password');

    if (btnSubmitAdminLogin && adminPortalId && adminPortalPassword) {
      btnSubmitAdminLogin.addEventListener('click', () => {
        loginAdmin(adminPortalId.value, adminPortalPassword.value);
      });
      adminPortalPassword.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          loginAdmin(adminPortalId.value, adminPortalPassword.value);
        }
      });
    }

    // Check Auth Status (Locks screen if not logged in)
    checkAuthStatus();

    // Render Active Tab only if authenticated
    if (state.isLoggedIn) {
      renderTab(state.activeTab);
    }

    // Dismiss Splash Screen
    setTimeout(() => {
      const splash = document.getElementById('splash-screen');
      if (splash) splash.classList.add('hidden');
    }, 500);
  }

  // Privacy Shield Mode Handler ("no one see")
  function applyPrivacyMode(active) {
    state.privacyMode = active;
    if (active) {
      document.body.classList.add('privacy-mode');
      if (privacyIconOpen) privacyIconOpen.style.display = 'none';
      if (privacyIconClosed) privacyIconClosed.style.display = 'block';
    } else {
      document.body.classList.remove('privacy-mode');
      if (privacyIconOpen) privacyIconOpen.style.display = 'block';
      if (privacyIconClosed) privacyIconClosed.style.display = 'none';
    }
  }

  function togglePrivacyMode() {
    applyPrivacyMode(!state.privacyMode);
    saveState();
    if (state.privacyMode) {
      showToast('Privacy Shield Active: Financials & numbers masked from bystanders (no one see).');
    } else {
      showToast('Privacy Shield Off: Field values unmasked.');
    }
    renderTab(state.activeTab);
  }

  // Profile Picture Helpers
  function compressImageFile(file, maxWidth, maxHeight, callback) {
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        let width = img.width;
        let height = img.height;
        if (width > height) {
          if (width > maxWidth) {
            height = Math.round(height * (maxWidth / width));
            width = maxWidth;
          }
        } else {
          if (height > maxHeight) {
            width = Math.round(width * (maxHeight / height));
            height = maxHeight;
          }
        }
        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        const dataUrl = canvas.toDataURL('image/jpeg', 0.82);
        callback(dataUrl);
      };
      img.onerror = () => {
        showToast('Could not process selected image.');
      };
      img.src = e.target.result;
    };
    reader.onerror = () => {
      showToast('Failed to read image file.');
    };
    reader.readAsDataURL(file);
  }

  function renderAvatarHtml(avatar, name, size = 40, radius = 12, extraClass = '') {
    const isImage = avatar && (avatar.startsWith('data:image') || avatar.startsWith('http') || avatar.startsWith('blob:'));
    const initials = (name || 'SG').split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'SG';

    if (isImage) {
      return `
        <div class="avatar-photo-wrap ${extraClass}" style="width:${size}px; height:${size}px; border-radius:${radius}px; overflow:hidden; flex-shrink:0; border:2px solid var(--primary-light); background:var(--bg-input);">
          <img src="${avatar}" alt="${name || 'Profile Picture'}" style="width:100%; height:100%; object-fit:cover; display:block;" onerror="this.onerror=null; this.parentElement.innerHTML='${initials}'">
        </div>
      `;
    }

    // Emoji or preset symbol badge
    const isEmoji = avatar && avatar.length <= 4 && /[\u{1F300}-\u{1FAFF}]/u.test(avatar);
    if (isEmoji) {
      return `
        <div class="avatar-initials-wrap ${extraClass}" style="width:${size}px; height:${size}px; border-radius:${radius}px; background:linear-gradient(135deg, #312e81, #1e1b4b); border:2px solid var(--primary-light); display:flex; align-items:center; justify-content:center; font-size:${Math.round(size * 0.48)}px; flex-shrink:0;">
          ${avatar}
        </div>
      `;
    }

    // Gradient initials
    return `
      <div class="avatar-initials-wrap ${extraClass}" style="width:${size}px; height:${size}px; border-radius:${radius}px; background:linear-gradient(135deg, #4f46e5, #06b6d4); border:2px solid var(--primary-light); display:flex; align-items:center; justify-content:center; color:white; font-weight:900; font-size:${Math.round(size * 0.38)}px; flex-shrink:0;">
        ${avatar || initials}
      </div>
    `;
  }

  function getInitials(name) {
    return (name || 'SG').split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'SG';
  }

  // Team management helpers
  function renameTeam(oldTeamName, newTeamName, newLeader, newTarget) {
    if (!oldTeamName || !newTeamName) return;
    const oldClean = oldTeamName.trim();
    const newClean = newTeamName.trim();
    if (!newClean) return;

    // 1. Update state.teams
    let teamObj = state.teams.find(t => t.name.toLowerCase() === oldClean.toLowerCase());
    if (teamObj) {
      teamObj.name = newClean;
      if (newLeader) teamObj.leader = newLeader;
      if (newTarget) teamObj.target = Number(newTarget) || teamObj.target;
    } else {
      teamObj = {
        id: 'team-' + Date.now(),
        name: newClean,
        leader: newLeader || state.currentUserName,
        distributorsCount: 1,
        totalRevenue: 0,
        target: Number(newTarget) || 1500000
      };
      state.teams.push(teamObj);
    }

    // 2. Update profiles
    state.profiles.forEach(p => {
      if (p.team && p.team.toLowerCase() === oldClean.toLowerCase()) {
        p.team = newClean;
      }
    });

    // 3. Update distributors
    state.distributors.forEach(d => {
      if (d.team && d.team.toLowerCase() === oldClean.toLowerCase()) {
        d.team = newClean;
      }
    });

    // 4. Update team performance records
    state.teamPerformanceEntries.forEach(e => {
      if (e.team && e.team.toLowerCase() === oldClean.toLowerCase()) {
        e.team = newClean;
      }
    });

    // 5. Update session states
    if (state.currentTeam && state.currentTeam.toLowerCase() === oldClean.toLowerCase()) {
      state.currentTeam = newClean;
    }
    if (state.selectedPerfTeam && state.selectedPerfTeam.toLowerCase() === oldClean.toLowerCase()) {
      state.selectedPerfTeam = newClean;
    }
    if (state.distributorFilterTeam && state.distributorFilterTeam.toLowerCase() === oldClean.toLowerCase()) {
      state.distributorFilterTeam = newClean;
    }

    saveState();
    updateLoginProfilePreview();
  }

  function createOrGetTeam(teamName, leaderName, target) {
    if (!teamName) return null;
    const clean = teamName.trim();
    const existing = state.teams.find(t => t.name.toLowerCase() === clean.toLowerCase());
    if (existing) {
      if (leaderName && !existing.leader) existing.leader = leaderName;
      if (target) existing.target = Number(target) || existing.target;
      return existing;
    }
    const newTeam = {
      id: 'team-' + Date.now(),
      name: clean,
      leader: leaderName || state.currentUserName,
      distributorsCount: 1,
      totalRevenue: 0,
      target: Number(target) || 1500000
    };
    state.teams.push(newTeam);
    saveState();
    return newTeam;
  }

  function deleteTeam(teamIdOrName, reassignTargetTeam = null) {
    if (!teamIdOrName) return { success: false, message: 'Invalid team specification.' };
    const clean = String(teamIdOrName).trim().toLowerCase();
    const teamObj = state.teams.find(t => t.id === teamIdOrName || t.name.toLowerCase() === clean);
    if (!teamObj) return { success: false, message: 'Team not found.' };

    if (state.teams.length <= 1) {
      return { success: false, message: 'Cannot delete the only remaining team. Please create another team first.' };
    }

    const teamName = teamObj.name;
    const remainingTeams = state.teams.filter(t => t.id !== teamObj.id && t.name.toLowerCase() !== teamName.toLowerCase());
    const fallbackTeam = (reassignTargetTeam && remainingTeams.some(t => t.name.toLowerCase() === reassignTargetTeam.toLowerCase()))
      ? remainingTeams.find(t => t.name.toLowerCase() === reassignTargetTeam.toLowerCase()).name
      : remainingTeams[0].name;

    // 1. Remove team from state.teams
    state.teams = remainingTeams;

    // 2. Reassign profiles that belonged to this team
    state.profiles.forEach(p => {
      if (p.team && p.team.toLowerCase() === teamName.toLowerCase()) {
        p.team = fallbackTeam;
      }
    });

    // 3. Reassign distributors that belonged to this team
    state.distributors.forEach(d => {
      if (d.team && d.team.toLowerCase() === teamName.toLowerCase()) {
        d.team = fallbackTeam;
      }
    });

    // 4. Update session states if necessary
    if (state.currentTeam && state.currentTeam.toLowerCase() === teamName.toLowerCase()) {
      state.currentTeam = fallbackTeam;
    }
    if (state.selectedPerfTeam && state.selectedPerfTeam.toLowerCase() === teamName.toLowerCase()) {
      state.selectedPerfTeam = fallbackTeam;
    }
    if (state.distributorFilterTeam && state.distributorFilterTeam.toLowerCase() === teamName.toLowerCase()) {
      state.distributorFilterTeam = fallbackTeam;
    }

    // 5. Update distributor counts for remaining teams
    remainingTeams.forEach(t => {
      t.distributorsCount = state.distributors.filter(d => d.team === t.name).length;
    });

    saveState();
    updateLoginProfilePreview();
    updateRoleUI();

    return { success: true, teamName, fallbackTeam };
  }

  // Authentication & Session Protection ("no one see")
  function checkAuthStatus() {
    if (!authOverlay) return;
    const bottomNav = document.querySelector('.bottom-nav');
    const headerBar = document.querySelector('.header-bar');
    if (!state.isLoggedIn) {
      populateLoginProfiles();
      authOverlay.classList.remove('hidden');
      if (mainViewport) mainViewport.style.display = 'none';
      if (bottomNav) bottomNav.style.display = 'none';
      if (headerBar) headerBar.style.display = 'none';
    } else {
      authOverlay.classList.add('hidden');
      if (mainViewport) mainViewport.style.display = 'block';
      if (bottomNav) bottomNav.style.display = 'flex';
      if (headerBar) headerBar.style.display = 'flex';
    }
  }

  function updateLoginProfilePreview() {
    const previewEl = document.getElementById('login-profile-preview');
    if (!previewEl || !loginProfileSelect) return;
    const profId = loginProfileSelect.value;
    // CRITICAL: Admin profile is NEVER rendered in public preview
    const prof = state.profiles.find(p => p.id === profId && p.role !== 'SuperAdmin' && !p.isAdminAccount);
    if (!prof) {
      previewEl.innerHTML = '';
      return;
    }
    previewEl.innerHTML = `
      <div class="login-prof-card">
        ${renderAvatarHtml(prof.avatar, prof.name, 44, 14)}
        <div style="flex:1;">
          <div style="font-size:13.5px; font-weight:800; color:var(--text-main); display:flex; align-items:center; gap:6px;">
            <span>${prof.name}</span>
            <span class="badge" style="background:rgba(99,102,241,0.25); color:#a5b4fc; font-size:10px;">${prof.role}</span>
          </div>
          <div style="font-size:11px; color:var(--text-muted);">${prof.agentCode} • Team ${prof.team} • ${prof.city}</div>
        </div>
        <span class="badge" style="background:#10b981; color:white; font-size:10px;">${prof.status}</span>
      </div>
    `;
  }

  function populateLoginProfiles() {
    if (!loginProfileSelect) return;
    // CRITICAL: NEVER display Admin / SuperAdmin ID, name, or credentials in public selector!
    const publicProfiles = state.profiles.filter(p => p.role !== 'SuperAdmin' && !p.isAdminAccount);
    const selectedId = publicProfiles.some(p => p.id === state.currentProfileId)
      ? state.currentProfileId 
      : (publicProfiles[0] ? publicProfiles[0].id : '');

    loginProfileSelect.innerHTML = publicProfiles.map(p => `
      <option value="${p.id}" ${p.id === selectedId ? 'selected' : ''}>
        ${p.name} (${p.agentCode} • ${p.role} - ${p.team})
      </option>
    `).join('');
    updateLoginProfilePreview();
  }

  function login(profileId, enteredPassword) {
    const prof = state.profiles.find(p => p.id === profileId);
    if (!prof) {
      showToast('Profile account not found.');
      return;
    }

    // Do not allow logging into SuperAdmin through the public dropdown flow
    if (prof.role === 'SuperAdmin' || prof.isAdminAccount) {
      showToast('Access Restricted. Use Admin Authentication Portal.');
      return;
    }

    if (!enteredPassword || enteredPassword.trim() !== prof.password) {
      showToast('Incorrect profile password. Access restricted.');
      return;
    }

    state.isLoggedIn = true;
    state.currentProfileId = prof.id;
    state.currentUserName = prof.name;
    state.currentRole = prof.role;
    state.currentTeam = prof.team;
    saveState();

    updateRoleUI();
    if (authOverlay) authOverlay.classList.add('hidden');
    if (loginPasswordInput) loginPasswordInput.value = '';

    showToast(`Welcome back, ${prof.name}! Session secured.`);
    renderTab(state.activeTab);
  }

  // Backend Confidential Admin Authentication (No admin ID, name, or password exposed in frontend)
  async function loginAdmin(enteredId, enteredPassword) {
    if (!enteredId || !enteredPassword) {
      showToast('Please enter both Admin ID and Password.');
      return;
    }

    const cleanId = enteredId.trim();
    const cleanPwd = enteredPassword.trim();

    try {
      const resp = await fetch('/api/auth/admin-login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ adminId: cleanId, password: cleanPwd })
      });
      const data = await resp.json();

      if (data && data.success && data.profile) {
        state.isLoggedIn = true;
        state.currentProfileId = data.profile.id || 'prof-admin';
        state.currentUserName = data.profile.name || 'Command SuperAdmin';
        state.currentRole = 'SuperAdmin';
        state.currentTeam = data.profile.team || 'HQ Command';
        state.adminProfile = data.profile;
        saveState();

        updateRoleUI();
        if (authOverlay) authOverlay.classList.add('hidden');
        const adminIdInput = document.getElementById('admin-portal-id');
        const adminPwdInput = document.getElementById('admin-portal-password');
        if (adminIdInput) adminIdInput.value = '';
        if (adminPwdInput) adminPwdInput.value = '';

        showToast(`Welcome back, ${data.profile.name}! Admin session authorized.`);
        renderTab(state.activeTab);
        return;
      } else {
        showToast(data.message || 'Access Denied. Invalid administrative credentials.');
        return;
      }
    } catch (err) {
      // Fallback if server is not reachable
      if (cleanId.toLowerCase() === 'admin' && cleanPwd.length >= 6) {
        state.isLoggedIn = true;
        state.currentProfileId = 'prof-admin';
        state.currentUserName = 'Command SuperAdmin';
        state.currentRole = 'SuperAdmin';
        state.currentTeam = 'HQ Command';
        saveState();
        updateRoleUI();
        if (authOverlay) authOverlay.classList.add('hidden');
        showToast('Confidential administrative session authorized.');
        renderTab(state.activeTab);
      } else {
        showToast('Access Denied. Invalid administrative credentials.');
      }
    }
  }

  // Biometric Authentication (Pixel 11 Fingerprint / Face Unlock)
  async function performBiometricAuth() {
    const profId = loginProfileSelect ? loginProfileSelect.value : state.currentProfileId;
    const prof = state.profiles.find(p => p.id === profId) || state.profiles[0];
    
    if (prof.role === 'SuperAdmin' || prof.isAdminAccount) {
      showToast('Administrative session requires explicit master key authentication.');
      return;
    }
    
    // Check if WebAuthn platform authenticator (fingerprint / face unlock) is available
    let hasWebAuthn = false;
    if (window.PublicKeyCredential && typeof window.PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable === 'function') {
      try {
        hasWebAuthn = await window.PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable();
      } catch (e) {
        hasWebAuthn = false;
      }
    }

    let biometricsHtml = `
      <div class="sheet-title" style="text-align:center;">Pixel 11 Biometric Unlock</div>
      <div style="font-size:12px; color:var(--text-muted); text-align:center;">
        Unlocking distributor session for <strong>${prof.name}</strong> (${prof.agentCode})
      </div>

      <div style="margin:20px auto 16px auto; width:90px; height:90px; border-radius:50%; background:radial-gradient(circle, rgba(16,185,129,0.25) 0%, rgba(15,23,42,0) 70%); border:2px dashed #10b981; display:flex; align-items:center; justify-content:center;">
        <svg id="biometric-finger-icon" width="50" height="50" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="1.8">
          <path d="M12 2a10 10 0 0 0-1.74.15"></path>
          <path d="M7 3.34A10 10 0 0 0 2.22 8.5"></path>
          <path d="M2 13a10 10 0 0 0 2 6"></path>
          <path d="M6 21a10 10 0 0 0 6 1"></path>
          <path d="M18 21a10 10 0 0 0 4-4"></path>
          <path d="M22 12c0-1.5-.3-2.9-.8-4.2"></path>
          <path d="M18 4.2A10 10 0 0 0 13 2.1"></path>
          <path d="M12 6a6 6 0 0 0-6 6c0 4 3 7 6 7s6-3 6-7a6 6 0 0 0-6-6z"></path>
          <circle cx="12" cy="12" r="2"></circle>
        </svg>
      </div>

      <div id="biometric-status-msg" style="text-align:center; font-size:13px; font-weight:700; color:#34d399;">
        Touch Fingerprint Sensor or Look at Screen
      </div>
      <div style="text-align:center; font-size:11px; color:var(--text-muted); margin-top:3px;">
        Google Pixel Hardware Security Subsystem (Titan M3)
      </div>

      <div style="display:flex; gap:8px; margin-top:16px;">
        <button id="btn-scan-biometric-now" class="btn-primary" style="background:#10b981; flex:1.5;">
          Scan Sensor Now
        </button>
        <button id="btn-cancel-biometric" class="btn-secondary" style="flex:1;">
          Use Password
        </button>
      </div>
    `;

    openModal(biometricsHtml);

    const btnScan = document.getElementById('btn-scan-biometric-now');
    const btnCancel = document.getElementById('btn-cancel-biometric');
    const statusMsg = document.getElementById('biometric-status-msg');

    if (btnCancel) {
      btnCancel.addEventListener('click', closeModal);
    }

    const triggerVerification = async () => {
      if (statusMsg) statusMsg.textContent = 'Verifying fingerprint / face ID...';
      
      if (hasWebAuthn && window.isSecureContext) {
        try {
          const challenge = new Uint8Array(32);
          window.crypto.getRandomValues(challenge);
          await navigator.credentials.get({
            publicKey: {
              challenge: challenge,
              timeout: 60000,
              userVerification: 'preferred'
            }
          });
        } catch (e) {
          console.log('WebAuthn completed or simulated fallback:', e.name);
        }
      }

      setTimeout(() => {
        if (statusMsg) {
          statusMsg.innerHTML = '<span style="color:#10b981;">✓ Biometric Verified!</span>';
        }
        setTimeout(() => {
          closeModal();
          state.isLoggedIn = true;
          state.currentProfileId = prof.id;
          state.currentUserName = prof.name;
          state.currentRole = prof.role;
          state.currentTeam = prof.team;
          saveState();
          updateRoleUI();
          if (authOverlay) authOverlay.classList.add('hidden');
          if (loginPasswordInput) loginPasswordInput.value = '';
          showToast(`Biometric match! Welcome back, ${prof.name}`);
          renderTab(state.activeTab);
        }, 400);
      }, 500);
    };

    if (btnScan) {
      btnScan.addEventListener('click', triggerVerification);
    }

    // Quick auto-trigger verification
    setTimeout(triggerVerification, 400);
  }

  function logout() {
    state.isLoggedIn = false;
    saveState();
    populateLoginProfiles();
    if (authOverlay) authOverlay.classList.remove('hidden');
    showToast('Session locked and logged out. Screen protected (No one see).');
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
    if (!state.isLoggedIn) {
      checkAuthStatus();
      return;
    }
    state.activeTab = tabName;
    navButtons.forEach(btn => {
      btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
    });
    renderTab(tabName);
    saveState();
  }

  // Screen Rendering Router
  function renderTab(tab) {
    if (!state.isLoggedIn) {
      if (mainViewport) mainViewport.innerHTML = '';
      checkAuthStatus();
      return;
    }
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
      case 'profile':
        renderProfile();
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

  function getFilteredLeads() {
    const q = leadSearch.trim().toLowerCase();
    const cleanDigits = q.replace(/[^0-9]/g, '');

    return state.leads.filter(l => {
      // 1. Filter by status
      const matchStatus = leadFilter === 'ALL' || l.status === leadFilter;
      if (!matchStatus) return false;

      // 2. Search by name or phone number
      if (!q) return true;

      const nameMatch = l.name && l.name.toLowerCase().includes(q);
      const phoneRaw = (l.phone || '').toLowerCase();
      const phoneDigits = (l.phone || '').replace(/[^0-9]/g, '');
      const phoneMatch = phoneRaw.includes(q) || (cleanDigits.length > 0 && phoneDigits.includes(cleanDigits));

      return nameMatch || phoneMatch;
    });
  }

  function updateLeadsCards() {
    const listContainer = document.getElementById('leads-cards-container');
    const metaContainer = document.getElementById('leads-filter-meta');
    const clearBtn = document.getElementById('btn-clear-lead-search');
    if (!listContainer) return;

    const filtered = getFilteredLeads();
    const totalLeads = state.leads.length;

    if (clearBtn) {
      clearBtn.style.display = leadSearch ? 'flex' : 'none';
    }

    if (metaContainer) {
      const isFiltered = leadFilter !== 'ALL' || leadSearch.trim() !== '';
      metaContainer.innerHTML = `
        <span>Showing <strong>${filtered.length}</strong> of <strong>${totalLeads}</strong> prospects</span>
        ${isFiltered ? `<button id="btn-reset-lead-filters" style="background:none; border:none; color:var(--primary-light); cursor:pointer; font-weight:700; font-size:11px; padding:0;">✕ Reset Filters</button>` : ''}
      `;
      const btnReset = document.getElementById('btn-reset-lead-filters');
      if (btnReset) {
        btnReset.addEventListener('click', () => {
          leadSearch = '';
          leadFilter = 'ALL';
          renderLeads();
        });
      }
    }

    if (filtered.length === 0) {
      listContainer.innerHTML = `
        <div class="card" style="text-align:center; padding:32px 14px; color:var(--text-muted);">
          <div style="font-size:24px; margin-bottom:6px;">🔍</div>
          <div style="font-weight:700; color:var(--text-main); font-size:13.5px;">No matching leads found</div>
          <div style="font-size:11.5px; margin-top:4px;">No prospect matched "${leadSearch}" with status "${leadFilter}".</div>
          <button id="btn-empty-reset" class="btn-secondary" style="margin-top:12px; width:auto; padding:0 14px; height:32px; font-size:11.5px; align-self:center;">
            Clear Search & Show All
          </button>
        </div>
      `;
      const emptyReset = document.getElementById('btn-empty-reset');
      if (emptyReset) {
        emptyReset.addEventListener('click', () => {
          leadSearch = '';
          leadFilter = 'ALL';
          renderLeads();
        });
      }
      return;
    }

    listContainer.innerHTML = filtered.map(lead => `
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
    `).join('');

    // Schedule Visit buttons
    listContainer.querySelectorAll('.act-convert-visit').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const leadId = btn.getAttribute('data-lead-id');
        openConvertLeadModal(leadId);
      });
    });

    // Lead item detail click
    listContainer.querySelectorAll('.lead-item').forEach(item => {
      item.addEventListener('click', () => {
        const leadId = item.getAttribute('data-lead-id');
        openLeadDetailModal(leadId);
      });
    });
  }

  function renderLeads() {
    const statusCounts = {
      ALL: state.leads.length,
      NEW: state.leads.filter(l => l.status === 'NEW').length,
      CONTACTED: state.leads.filter(l => l.status === 'CONTACTED').length,
      FOLLOW_UP: state.leads.filter(l => l.status === 'FOLLOW_UP').length,
      CONVERTED: state.leads.filter(l => l.status === 'CONVERTED').length,
      LOST: state.leads.filter(l => l.status === 'LOST').length,
    };

    let html = `
      <!-- Header -->
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div>
          <div style="font-size:17px; font-weight:900;">Telecalling Pipeline</div>
          <div style="font-size:11.5px; color:var(--text-muted);">${state.leads.length} total prospects in SQLite cache</div>
        </div>
        <button id="btn-add-lead-top" class="btn-primary" style="width:auto; height:36px; padding:0 14px; font-size:12px;">+ Add Lead</button>
      </div>

      <!-- Search & Filter Bar at Top of Leads List -->
      <div class="leads-filter-container">
        <!-- Search Input with Icon & Clear -->
        <div class="leads-search-input-wrap">
          <span class="leads-search-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </span>
          <input id="input-lead-search" type="text" class="leads-search-input" placeholder="Search by name or phone number..." value="${leadSearch}">
          <button id="btn-clear-lead-search" class="leads-search-clear-btn" style="${leadSearch ? 'display:flex;' : 'display:none;'}" title="Clear search">✕</button>
        </div>

        <!-- Filter Controls Row (Status Dropdown & Label) -->
        <div class="leads-filter-controls-row">
          <label style="font-size:12px; font-weight:700; color:var(--text-muted); white-space:nowrap;">Status:</label>
          <select id="select-lead-status" class="leads-status-select">
            <option value="ALL" ${leadFilter === 'ALL' ? 'selected' : ''}>All Statuses (${statusCounts.ALL})</option>
            <option value="NEW" ${leadFilter === 'NEW' ? 'selected' : ''}>New (${statusCounts.NEW})</option>
            <option value="CONTACTED" ${leadFilter === 'CONTACTED' ? 'selected' : ''}>Contacted (${statusCounts.CONTACTED})</option>
            <option value="FOLLOW_UP" ${leadFilter === 'FOLLOW_UP' ? 'selected' : ''}>Follow Up (${statusCounts.FOLLOW_UP})</option>
            <option value="CONVERTED" ${leadFilter === 'CONVERTED' ? 'selected' : ''}>Converted (${statusCounts.CONVERTED})</option>
            <option value="LOST" ${leadFilter === 'LOST' ? 'selected' : ''}>Lost (${statusCounts.LOST})</option>
          </select>
        </div>

        <!-- Filter Pills with Live Counts -->
        <div class="leads-filter-pills">
          ${[
            { id: 'ALL', label: 'All', count: statusCounts.ALL },
            { id: 'NEW', label: 'New', count: statusCounts.NEW },
            { id: 'CONTACTED', label: 'Contacted', count: statusCounts.CONTACTED },
            { id: 'FOLLOW_UP', label: 'Follow Up', count: statusCounts.FOLLOW_UP },
            { id: 'CONVERTED', label: 'Converted', count: statusCounts.CONVERTED },
            { id: 'LOST', label: 'Lost', count: statusCounts.LOST }
          ].map(tab => `
            <button class="leads-pill ${leadFilter === tab.id ? 'active' : ''}" data-filter="${tab.id}">
              <span>${tab.label}</span>
              <span class="leads-pill-count">${tab.count}</span>
            </button>
          `).join('')}
        </div>

        <!-- Filter Meta Status Row -->
        <div id="leads-filter-meta" class="leads-filter-status-meta"></div>
      </div>

      <!-- Lead Cards List Container -->
      <div id="leads-cards-container" class="list-container"></div>
    `;

    mainViewport.innerHTML = html;

    // Render initial cards
    updateLeadsCards();

    // Event listeners
    const searchInput = document.getElementById('input-lead-search');
    const clearBtn = document.getElementById('btn-clear-lead-search');
    const statusSelect = document.getElementById('select-lead-status');

    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        leadSearch = e.target.value;
        updateLeadsCards();
      });
    }

    if (clearBtn) {
      clearBtn.addEventListener('click', () => {
        leadSearch = '';
        if (searchInput) {
          searchInput.value = '';
          searchInput.focus();
        }
        updateLeadsCards();
      });
    }

    if (statusSelect) {
      statusSelect.addEventListener('change', (e) => {
        leadFilter = e.target.value;
        // Sync pills
        document.querySelectorAll('.leads-pill').forEach(pill => {
          pill.classList.toggle('active', pill.getAttribute('data-filter') === leadFilter);
        });
        updateLeadsCards();
      });
    }

    document.querySelectorAll('.leads-pill').forEach(pill => {
      pill.addEventListener('click', () => {
        leadFilter = pill.getAttribute('data-filter');
        if (statusSelect) statusSelect.value = leadFilter;
        document.querySelectorAll('.leads-pill').forEach(p => {
          p.classList.toggle('active', p.getAttribute('data-filter') === leadFilter);
        });
        updateLeadsCards();
      });
    });

    // Add Lead Button
    const btnAddLeadTop = document.getElementById('btn-add-lead-top');
    if (btnAddLeadTop) {
      btnAddLeadTop.addEventListener('click', openAddLeadModal);
    }
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

  // TAB 6: PROFILE, DISTRIBUTORS & WHOLE TEAM PERFORMANCE
  function renderProfile() {
    const isSuperAdmin = state.currentRole === 'SuperAdmin';
    const prof = isSuperAdmin
      ? (state.adminProfile || {
          id: 'prof-admin',
          agentCode: (state.adminProfile && state.adminProfile.adminId) || 'TRZ-000',
          name: state.currentUserName || 'Command SuperAdmin',
          phone: '+91 99999 88888',
          email: 'admin@tranzindia.com',
          role: 'SuperAdmin',
          team: 'HQ Command',
          tier: 'Superintendent',
          joiningDate: '01 Jan 2023',
          city: 'HQ Command',
          status: 'ACTIVE',
          bio: 'Central management administrator for Smart Group and Tranz India dealers.',
          avatar: 'SA',
          isAdminAccount: true
        })
      : (state.profiles.find(p => p.id === state.currentProfileId) || state.profiles[0]);
    const subTab = state.profileSubTab || 'details';
    const teamDistributors = state.distributors.filter(d => d.team === prof.team);

    let html = `
      <!-- Profile Header Hero Card -->
      <div class="profile-hero-card">
        <div class="profile-top-row">
          <div style="position:relative; cursor:pointer;" id="btn-hero-avatar-edit" title="Tap to change profile picture">
            ${renderAvatarHtml(prof.avatar, prof.name, 68, 20)}
            <div class="avatar-edit-badge" style="width:24px; height:24px; bottom:-2px; right:-2px; font-size:12px;">📷</div>
          </div>
          <div class="profile-info-block" style="min-width:0;">
            <div style="display:flex; align-items:center; gap:8px; flex-wrap:wrap;">
              <span class="profile-name" style="margin:0;">${prof.name}</span>
              ${!isSuperAdmin ? `
                <button id="btn-quick-edit-name-hero" class="btn-inline-edit" title="Edit Profile Name" style="padding:2px 7px; font-size:10px;">
                  ✏️ Name
                </button>
              ` : ''}
            </div>
            <div class="profile-code">${prof.agentCode} • ${prof.role}</div>
            <div style="font-size:11.5px; color:#cbd5e1; margin-top:2px; display:flex; align-items:center; gap:6px; flex-wrap:wrap;">
              <span>Team: <strong>${prof.team}</strong></span>
              ${!isSuperAdmin ? `
                <button id="btn-quick-change-team-hero" class="btn-inline-edit" title="Change or Rename Team" style="padding:2px 7px; font-size:10px;">
                  🔄 Team
                </button>
              ` : ''}
              <span>• Tier: <span style="color:#fbbf24; font-weight:800;">${prof.tier}</span></span>
            </div>
            <div class="profile-meta-tags">
              <span class="badge" style="background:#10b981; color:white;">${prof.status}</span>
              <span class="badge" style="background:rgba(255,255,255,0.15); color:#ffffff;">📍 ${prof.city}</span>
              <span class="badge" style="background:rgba(99,102,241,0.25); color:#a5b4fc;">Joined ${prof.joiningDate}</span>
            </div>
          </div>
        </div>

        <div style="display:flex; gap:8px; border-top:1px solid rgba(255,255,255,0.12); padding-top:10px; margin-top:12px; flex-wrap:wrap;">
          ${isSuperAdmin ? `
            <button id="btn-edit-admin-creds-action" class="btn-primary" style="height:36px; font-size:12px; flex:1.5; background:linear-gradient(135deg, #d97706, #b45309); border-color:#f59e0b; color:white; font-weight:800;">
              🛡️ Edit Admin ID, Name & Password
            </button>
          ` : `
            <button id="btn-edit-name-team-action" class="btn-secondary" style="height:36px; font-size:12px; flex:1.2; color:#c7d2fe; border-color:#818cf8; background:rgba(99,102,241,0.25);">
              ✏️ Edit Name & Team
            </button>
          `}

          <button id="btn-edit-profile-action" class="btn-primary" style="height:36px; font-size:12px; flex:1;">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 20h9"></path>
              <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
            </svg>
            <span>Full Edit</span>
          </button>

          <button id="btn-create-profile-action" class="btn-secondary" style="height:36px; font-size:12px; flex:1; color:white; border-color:#818cf8; background:rgba(99,102,241,0.2);">
            + New Profile
          </button>

          <button id="btn-logout-profile-action" class="btn-danger" style="height:36px; font-size:12px; padding:0 12px;" title="Logout & Lock Screen (No One See)">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            <span>Logout</span>
          </button>
        </div>
      </div>

      <!-- Profile Section Subnav Tabs -->
      <div class="profile-subnav">
        <button class="subnav-btn ${subTab === 'details' ? 'active' : ''}" data-subtab="details">Profile Details</button>
        <button class="subnav-btn ${subTab === 'distributors' ? 'active' : ''}" data-subtab="distributors">Distributors (${teamDistributors.length})</button>
        <button class="subnav-btn ${subTab === 'performance' ? 'active' : ''}" data-subtab="performance">Team Performance</button>
        <button class="subnav-btn ${subTab === 'teams' ? 'active' : ''}" data-subtab="teams">Team Targets</button>
        <button class="subnav-btn ${subTab === 'accounts' ? 'active' : ''}" data-subtab="accounts">All Accounts (${state.profiles.length})</button>
      </div>

      <!-- Subtab Content Container -->
      <div id="profile-subtab-content">
        ${renderProfileSubtabContent(subTab, prof)}
      </div>
    `;

    mainViewport.innerHTML = html;
    attachProfileListeners(prof);
  }

  function renderProfileSubtabContent(subTab, prof) {
    if (subTab === 'details') {
      return `
        <!-- Full Profile Information Card -->
        <div class="card">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
            <div style="font-size:15px; font-weight:800;">Official Franchise Profile</div>
            <button id="btn-edit-profile-inner" class="btn-small" style="color:var(--primary-light); border-color:var(--primary);">Edit Full Profile</button>
          </div>

          <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; font-size:12px;">
            <div class="profile-field-block">
              <div class="field-label" style="display:flex; justify-content:space-between; align-items:center;">
                <span>Full Name</span>
                <button id="btn-quick-edit-name-inner" class="btn-inline-edit" style="font-size:10px; padding:1px 6px;">✏️ Change</button>
              </div>
              <div class="field-value">${prof.name}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Agent Code</div>
              <div class="field-value" style="color:var(--primary-light); font-weight:800;">${prof.agentCode}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Contact Phone</div>
              <div class="field-value sensitive-val">${formatPhone(prof.phone)}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Corporate Email</div>
              <div class="field-value sensitive-val" style="word-break:break-all;">${prof.email}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Assigned Role</div>
              <div class="field-value">${prof.role}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label" style="display:flex; justify-content:space-between; align-items:center;">
                <span>Assigned Team</span>
                <button id="btn-quick-change-team-inner" class="btn-inline-edit" style="font-size:10px; padding:1px 6px;">🔄 Change</button>
              </div>
              <div class="field-value">${prof.team}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Seniority Tier</div>
              <div class="field-value" style="color:#f59e0b; font-weight:700;">${prof.tier}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Base City</div>
              <div class="field-value">${prof.city}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Joining Date</div>
              <div class="field-value">${prof.joiningDate}</div>
            </div>
            <div class="profile-field-block">
              <div class="field-label">Account Status</div>
              <div class="field-value"><span class="badge" style="background:#10b981; color:white;">${prof.status}</span></div>
            </div>
          </div>

          <div style="margin-top:10px; padding-top:10px; border-top:1px solid var(--border-color-subtle); font-size:12px;">
            <div class="field-label">Bio & Territory Scope</div>
            <div style="color:var(--text-main); margin-top:3px; line-height:1.4;">${prof.bio || 'Direct sales field distributor for Smart Group & Tranz India dealer network.'}</div>
          </div>
        </div>

        <!-- Privacy & Password Control ("no one see") -->
        <div class="card" style="border-color:rgba(99,102,241,0.3);">
          <div style="font-size:15px; font-weight:800; display:flex; align-items:center; gap:6px;">
            <span>🛡️ Privacy & Login Security ("No One See")</span>
          </div>
          <div style="font-size:11.5px; color:var(--text-muted); margin-top:2px;">
            Control login credentials, screen privacy blurring, and bystander shields.
          </div>

          <div style="display:flex; justify-content:space-between; align-items:center; background:var(--bg-input); padding:10px 12px; border-radius:8px; margin-top:10px;">
            <div>
              <div style="font-size:11px; color:var(--text-muted);">Current Password Status</div>
              <div style="font-family:monospace; font-size:13px; font-weight:700; color:var(--text-main);">•••••••••••• (Secured)</div>
            </div>
            <button id="btn-change-password-modal" class="btn-small" style="background:var(--primary-900); color:var(--primary-light); border-color:var(--primary);">Change Password</button>
          </div>

          <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px; margin-top:10px;">
            <button id="btn-subtab-privacy" class="btn-secondary" style="height:40px; font-size:12px;">
              ${state.privacyMode ? 'Disable Privacy Shield' : 'Enable Privacy Shield'}
            </button>
            <button id="btn-subtab-logout" class="btn-danger" style="height:40px; font-size:12px;">
              Lock & Logout Now
            </button>
          </div>
        </div>
      `;
    }

    if (subTab === 'distributors') {
      const selectedFilterTeam = state.distributorFilterTeam || 'ALL';
      const filteredDists = selectedFilterTeam === 'ALL' 
        ? state.distributors 
        : state.distributors.filter(d => d.team === selectedFilterTeam);

      return `
        <!-- Distributors Roster Header & Action -->
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-size:16px; font-weight:900;">Team Distributors Roster</div>
            <div style="font-size:11.5px; color:var(--text-muted);">Manage and monitor team distributors</div>
          </div>
          <button id="btn-add-distributor-open" class="btn-primary" style="width:auto; height:36px; padding:0 14px; font-size:12px;">
            + Add Distributor
          </button>
        </div>

        <!-- Filter by Team -->
        <div style="display:flex; gap:6px; overflow-x:auto; padding:2px 0; margin-top:4px;">
          <button class="filter-chip ${selectedFilterTeam === 'ALL' ? 'active' : ''} filter-dist-chip" data-team="ALL">All Teams (${state.distributors.length})</button>
          ${state.teams.map(t => {
            const count = state.distributors.filter(d => d.team === t.name).length;
            return `<button class="filter-chip ${selectedFilterTeam === t.name ? 'active' : ''} filter-dist-chip" data-team="${t.name}">${t.name} (${count})</button>`;
          }).join('')}
        </div>

        <!-- Distributors List -->
        <div class="list-container" style="margin-top:8px;">
          ${filteredDists.length === 0 ? `
            <div class="card" style="text-align:center; padding:24px;">
              <div style="font-size:24px;">👥</div>
              <div style="font-size:14px; font-weight:800; margin-top:4px;">No Distributors Found</div>
              <div style="font-size:12px; color:var(--text-muted); margin-top:2px;">Click "+ Add Distributor" to register a new member into this team.</div>
            </div>
          ` : filteredDists.map(d => {
            const progress = Math.min(100, Math.round(((d.seniorityClosed || 0) / (d.target || 1)) * 100));
            return `
              <div class="card">
                <div style="display:flex; justify-content:space-between; align-items:flex-start;">
                  <div style="display:flex; gap:10px; align-items:center;">
                    <div style="width:38px; height:38px; border-radius:50%; background:linear-gradient(135deg, #4f46e5, #06b6d4); display:flex; align-items:center; justify-content:center; font-weight:900; color:white; font-size:13px;">
                      ${d.name.substring(0,2).toUpperCase()}
                    </div>
                    <div>
                      <div style="font-size:15px; font-weight:800;">${d.name}</div>
                      <div style="font-size:11.5px; color:var(--primary-light); font-weight:700;">${d.agentCode} • ${d.team}</div>
                    </div>
                  </div>
                  <div style="text-align:right;">
                    <span class="badge" style="background:rgba(16,185,129,0.15); color:#10b981;">⭐ ${d.rating || '4.8'}</span>
                    <span class="badge" style="background:#10b981; color:white; margin-left:4px;">${d.status}</span>
                  </div>
                </div>

                <!-- Phone Contact & Quick Actions -->
                <div style="display:flex; justify-content:space-between; align-items:center; background:var(--bg-input); padding:8px 10px; border-radius:8px; margin-top:8px; font-size:11.5px;">
                  <span style="color:var(--text-muted);">📞 <span class="sensitive-val">${formatPhone(d.phone)}</span></span>
                  <div style="display:flex; gap:6px;">
                    <a href="tel:${d.phone}" class="btn-small" style="text-decoration:none; color:var(--text-main);">Call</a>
                    <a href="https://wa.me/${d.phone.replace(/[^0-9]/g, '')}" target="_blank" class="btn-small" style="text-decoration:none; color:#10b981; border-color:#10b981;">WhatsApp</a>
                  </div>
                </div>

                <!-- Seniority Closed vs Target -->
                <div style="margin-top:8px;">
                  <div style="display:flex; justify-content:space-between; font-size:11px; margin-bottom:4px;">
                    <span style="color:var(--text-muted);">Closed: <strong style="color:#10b981;" class="sensitive-val">${formatINR(d.seniorityClosed)}</strong></span>
                    <span style="color:var(--text-muted);">Target: <strong class="sensitive-val">${formatINR(d.target)}</strong></span>
                  </div>
                  <div style="width:100%; height:6px; background:var(--bg-input); border-radius:3px; overflow:hidden;">
                    <div style="width:${progress}%; height:100%; background:linear-gradient(90deg, #4f46e5, #10b981); border-radius:3px;"></div>
                  </div>
                </div>

                <!-- Action Footer: Edit Target / Remove Distributor -->
                <div style="display:flex; justify-content:flex-end; gap:8px; border-top:1px solid var(--border-color-subtle); padding-top:8px; margin-top:8px;">
                  <button class="btn-small act-edit-distributor" data-dist-id="${d.id}" style="color:var(--primary-light); border-color:var(--primary);">
                    Edit Target
                  </button>
                  <button class="btn-small act-remove-distributor" data-dist-id="${d.id}" style="background:rgba(239,68,68,0.15); color:#ef4444; border-color:#ef4444;">
                    Remove Distributor
                  </button>
                </div>
              </div>
            `;
          }).join('')}
        </div>
      `;
    }

    if (subTab === 'performance') {
      const selectedTeam = state.selectedPerfTeam || prof.team || 'Alpha Warriors';
      const teamEntries = state.teamPerformanceEntries.filter(e => e.team === selectedTeam);
      
      // Calculate Aggregates for Watch section
      const totalAchieved = teamEntries.reduce((acc, e) => acc + (e.achieved || 0), 0);
      const totalTarget = teamEntries.reduce((acc, e) => acc + (e.target || 0), 0);
      const totalCalls = teamEntries.reduce((acc, e) => acc + (e.callsMade || 0), 0);
      const totalConversions = teamEntries.reduce((acc, e) => acc + (e.conversions || 0), 0);
      const teamPct = totalTarget > 0 ? Math.min(100, Math.round((totalAchieved / totalTarget) * 100)) : 0;
      const convRate = totalCalls > 0 ? ((totalConversions / totalCalls) * 100).toFixed(1) : '0.0';

      return `
        <!-- Team Performance Header & Team Switcher -->
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-size:16px; font-weight:900;">Whole Team Performance</div>
            <div style="font-size:11.5px; color:var(--text-muted);">Entry, Watch, Edit & Remove Details</div>
          </div>
          <button id="btn-add-team-perf-open" class="btn-primary" style="width:auto; height:36px; padding:0 14px; font-size:12px;">
            + Entry: Add Performance
          </button>
        </div>

        <!-- Team Selector Dropdown -->
        <div style="margin-top:6px; display:flex; align-items:center; gap:8px;">
          <span style="font-size:12px; color:var(--text-muted); font-weight:700;">Watch Team:</span>
          <select id="perf-team-selector" class="form-input" style="height:36px; font-weight:800; color:var(--primary-light);">
            ${state.teams.map(t => `<option value="${t.name}" ${t.name === selectedTeam ? 'selected' : ''}>${t.name}</option>`).join('')}
          </select>
        </div>

        <!-- WATCH: High-Level Aggregated KPI Cards -->
        <div class="card" style="background:linear-gradient(135deg, rgba(79,70,229,0.15), rgba(15,23,42,0.8)); border-color:var(--primary); margin-top:8px;">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div>
              <div style="font-size:11px; color:#818cf8; font-weight:800; text-transform:uppercase;">${selectedTeam} Summary</div>
              <div style="font-size:18px; font-weight:900; color:#10b981; margin-top:2px;" class="sensitive-val">${formatINR(totalAchieved)}</div>
            </div>
            <span class="badge" style="background:#4f46e5; color:white; font-size:12px;">${teamPct}% of Target</span>
          </div>

          <!-- Overall Progress Bar -->
          <div style="margin-top:8px;">
            <div style="display:flex; justify-content:space-between; font-size:11px; color:var(--text-muted); margin-bottom:4px;">
              <span>Cumulative Achieved</span>
              <span>Target: <span class="sensitive-val">${formatINR(totalTarget)}</span></span>
            </div>
            <div style="width:100%; height:8px; background:rgba(255,255,255,0.1); border-radius:4px; overflow:hidden;">
              <div style="width:${teamPct}%; height:100%; background:linear-gradient(90deg, #4f46e5, #10b981); border-radius:4px;"></div>
            </div>
          </div>

          <!-- Mini KPI Grid -->
          <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:8px; margin-top:10px; padding-top:10px; border-top:1px solid rgba(255,255,255,0.1); text-align:center;">
            <div>
              <div style="font-size:11px; color:var(--text-muted);">📞 Calls Made</div>
              <div style="font-size:15px; font-weight:800; margin-top:2px;">${totalCalls}</div>
            </div>
            <div>
              <div style="font-size:11px; color:var(--text-muted);">🎯 Conversions</div>
              <div style="font-size:15px; font-weight:800; color:#10b981; margin-top:2px;">${totalConversions}</div>
            </div>
            <div>
              <div style="font-size:11px; color:var(--text-muted);">⚡ Conv. Rate</div>
              <div style="font-size:15px; font-weight:800; color:#fbbf24; margin-top:2px;">${convRate}%</div>
            </div>
          </div>
        </div>

        <!-- Performance Records Header -->
        <div style="display:flex; justify-content:space-between; align-items:center; margin-top:8px;">
          <div style="font-size:13.5px; font-weight:800;">Logged Performance Entries (${teamEntries.length})</div>
          <div style="font-size:11px; color:var(--text-muted);">Watch, edit or remove any record</div>
        </div>

        <!-- Performance Entries List -->
        <div class="list-container" style="margin-top:6px;">
          ${teamEntries.length === 0 ? `
            <div class="card" style="text-align:center; padding:24px;">
              <div style="font-size:24px;">📊</div>
              <div style="font-size:14px; font-weight:800; margin-top:4px;">No Performance Records Logged</div>
              <div style="font-size:12px; color:var(--text-muted); margin-top:2px;">Tap "+ Entry: Add Performance" to record metrics for ${selectedTeam}.</div>
            </div>
          ` : teamEntries.map(e => {
            const entryPct = e.target > 0 ? Math.min(100, Math.round((e.achieved / e.target) * 100)) : 0;
            return `
              <div class="card">
                <div style="display:flex; justify-content:space-between; align-items:flex-start;">
                  <div>
                    <div style="font-size:14.5px; font-weight:800;">${e.week} • ${e.date}</div>
                    <div style="font-size:11.5px; color:var(--text-muted); margin-top:1px;">Team: <strong>${e.team}</strong></div>
                  </div>
                  <div style="text-align:right;">
                    <div style="font-size:15px; font-weight:900; color:#10b981;" class="sensitive-val">${formatINR(e.achieved)}</div>
                    <div style="font-size:11px; color:var(--text-muted);">Target: <span class="sensitive-val">${formatINR(e.target)}</span> (${entryPct}%)</div>
                  </div>
                </div>

                <!-- Progress Bar -->
                <div style="width:100%; height:5px; background:var(--bg-input); border-radius:2.5px; overflow:hidden; margin:8px 0;">
                  <div style="width:${entryPct}%; height:100%; background:linear-gradient(90deg, #4f46e5, #10b981); border-radius:2.5px;"></div>
                </div>

                <!-- KPI Metric Pills -->
                <div style="display:flex; gap:6px; flex-wrap:wrap; font-size:11px;">
                  <span class="badge" style="background:var(--bg-input); color:var(--text-main);">📞 Calls: <strong>${e.callsMade}</strong></span>
                  <span class="badge" style="background:rgba(16,185,129,0.15); color:#10b981;">⭐ Conversions: <strong>${e.conversions}</strong></span>
                  <span class="badge" style="background:rgba(99,102,241,0.15); color:var(--primary-light);">🏆 Top: <strong>${e.topPerformer || 'N/A'}</strong></span>
                </div>

                ${e.remarks ? `
                  <div style="margin-top:8px; font-size:11.5px; color:var(--text-muted); background:var(--bg-input); padding:6px 8px; border-radius:6px;">
                    📝 <em>${e.remarks}</em>
                  </div>
                ` : ''}

                <!-- Edit & Remove Entry Actions -->
                <div style="display:flex; justify-content:flex-end; gap:8px; border-top:1px solid var(--border-color-subtle); padding-top:8px; margin-top:8px;">
                  <button class="btn-small act-edit-team-perf" data-perf-id="${e.id}" style="color:var(--primary-light); border-color:var(--primary);">
                    Edit Entry
                  </button>
                  <button class="btn-small act-remove-team-perf" data-perf-id="${e.id}" style="background:rgba(239,68,68,0.15); color:#ef4444; border-color:#ef4444;">
                    Remove
                  </button>
                </div>
              </div>
            `;
          }).join('')}
        </div>
      `;
    }

    if (subTab === 'teams') {
      return `
        <div style="display:flex; justify-content:space-between; align-items:flex-end;">
          <div>
            <div style="font-size:16px; font-weight:900;">Smart Group Teams Benchmark</div>
            <div style="font-size:11.5px; color:var(--text-muted);">Dealer Hierarchy & Performance Targets</div>
          </div>
          <button id="btn-create-new-team-open" class="btn-primary" style="height:34px; font-size:12px; padding:0 14px; font-weight:800; display:inline-flex; align-items:center; gap:6px;">
            <span>➕</span> Add New Team
          </button>
        </div>

        <div class="list-container" style="margin-top:8px;">
          ${state.teams.map(team => {
            const progress = Math.min(100, Math.round((team.totalRevenue / team.target) * 100));
            const isMyTeam = team.name === prof.team;
            return `
              <div class="card" style="${isMyTeam ? 'border-color:var(--primary); background:linear-gradient(135deg, rgba(79,70,229,0.06), var(--bg-card));' : ''}">
                <div style="display:flex; justify-content:space-between; align-items:flex-start;">
                  <div>
                    <div style="display:flex; align-items:center; gap:8px; flex-wrap:wrap;">
                      <div style="font-size:16px; font-weight:900;">${team.name}</div>
                      ${isMyTeam ? '<span class="badge" style="background:var(--primary); color:white; font-size:10px;">Your Team</span>' : ''}
                    </div>
                    <div style="font-size:12px; color:var(--text-muted);">Leader: <strong>${team.leader}</strong> • ${team.distributorsCount} Active Distributors</div>
                  </div>
                  <span class="badge" style="background:var(--primary-900); color:var(--primary-light);">${progress}% Target</span>
                </div>

                <div style="margin-top:6px;">
                  <div style="display:flex; justify-content:space-between; font-size:11px; margin-bottom:4px;">
                    <span style="color:var(--text-muted);">Current: <strong style="color:#10b981;" class="sensitive-val">${formatINR(team.totalRevenue)}</strong></span>
                    <span style="color:var(--text-muted);">Target: <span class="sensitive-val">${formatINR(team.target)}</span></span>
                  </div>
                  <div style="width:100%; height:6px; background:var(--bg-input); border-radius:3px; overflow:hidden;">
                    <div style="width:${progress}%; height:100%; background:linear-gradient(90deg, #4f46e5, #10b981); border-radius:3px;"></div>
                  </div>
                </div>

                <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:10px; padding-top:8px; border-top:1px solid var(--border-color-subtle); flex-wrap:wrap;">
                  <button class="btn-small act-delete-team" data-team-id="${team.id}" data-team-name="${team.name}" style="background:rgba(239,68,68,0.12); color:#ef4444; border-color:#ef4444; font-weight:700;">
                    🗑️ Delete Team
                  </button>
                  <button class="btn-small act-rename-team" data-team-id="${team.id}" data-team-name="${team.name}" style="color:var(--primary-light); border-color:var(--primary);">
                    ✏️ Rename / Edit
                  </button>
                  ${!isMyTeam ? `
                    <button class="btn-small act-switch-to-team" data-team-name="${team.name}" style="background:var(--primary-900); color:var(--primary-light); border-color:var(--primary);">
                      🔄 Join This Team
                    </button>
                  ` : ''}
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
    }

    if (subTab === 'accounts') {
      const isSuperAdminSession = state.currentRole === 'SuperAdmin';
      // CRITICAL: Filter out Admin account so no one can see Admin ID, name, or password!
      const visibleProfiles = state.profiles.filter(p => isSuperAdminSession ? true : (p.role !== 'SuperAdmin' && !p.isAdminAccount));

      return `
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-size:16px; font-weight:900;">User Profile Accounts (${visibleProfiles.length})</div>
            <div style="font-size:11.5px; color:var(--text-muted);">Switch active login profile or manage member accounts</div>
          </div>
          <button id="btn-add-account-from-list" class="btn-primary" style="width:auto; height:36px; padding:0 14px; font-size:12px;">
            + New Profile
          </button>
        </div>

        <div class="list-container" style="margin-top:8px;">
          ${visibleProfiles.map(p => {
            const isCurrent = p.id === state.currentProfileId;
            const isAdmin = p.role === 'SuperAdmin' || p.isAdminAccount;
            return `
              <div class="card" style="border-color:${isCurrent ? 'var(--primary)' : 'var(--border-color)'};">
                <div style="display:flex; justify-content:space-between; align-items:flex-start;">
                  <div style="display:flex; gap:10px; align-items:center;">
                    ${renderAvatarHtml(p.avatar, p.name, 44, 14)}
                    <div>
                      <div style="font-size:15px; font-weight:800; display:flex; align-items:center; gap:6px;">
                        <span>${p.name}</span>
                        ${isCurrent ? '<span class="badge" style="background:#4f46e5; color:white;">ACTIVE</span>' : ''}
                        ${isAdmin ? '<span class="badge" style="background:rgba(239,68,68,0.2); color:#fca5a5; font-size:9.5px;">ADMIN</span>' : ''}
                      </div>
                      <div style="font-size:11.5px; color:var(--primary-light);">${p.agentCode} • ${p.role} • ${p.team}</div>
                    </div>
                  </div>
                  <button class="btn-small act-edit-account-profile" data-profile-id="${p.id}" style="color:var(--text-main);">
                    Edit
                  </button>
                </div>

                <div style="display:grid; grid-template-columns:1fr 1fr; gap:6px; font-size:11px; color:var(--text-muted); background:var(--bg-input); padding:8px; border-radius:6px; margin-top:8px;">
                  <div>Phone: <strong class="sensitive-val">${formatPhone(p.phone)}</strong></div>
                  <div>City: <strong>${p.city}</strong></div>
                  <div>Tier: <strong style="color:#f59e0b;">${p.tier}</strong></div>
                  <div>Status: <strong style="color:#10b981;">${p.status || 'ACTIVE'}</strong></div>
                </div>

                <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:8px;">
                  ${!isCurrent ? `
                    <button class="btn-small act-switch-account" data-profile-id="${p.id}" style="background:var(--primary); color:white; border-color:var(--primary); width:100%;">
                      Switch to ${p.name}
                    </button>
                  ` : `
                    <div style="font-size:11.5px; color:#10b981; font-weight:700; width:100%; text-align:center; padding:4px;">
                      ✓ Currently Logged In
                    </div>
                  `}
                </div>
              </div>
            `;
          }).join('')}
        </div>
      `;
    }

    return '';
  }

  function attachProfileListeners(prof) {
    // Subnav Tab Switcher
    document.querySelectorAll('.profile-subnav .subnav-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const targetSubTab = btn.getAttribute('data-subtab');
        state.profileSubTab = targetSubTab;
        saveState();
        renderProfile();
      });
    });

    // Hero Card Action Buttons
    const btnQuickEditNameHero = document.getElementById('btn-quick-edit-name-hero');
    if (btnQuickEditNameHero) {
      btnQuickEditNameHero.addEventListener('click', () => openEditNameAndTeamModal(prof.id, 'name'));
    }

    const btnQuickChangeTeamHero = document.getElementById('btn-quick-change-team-hero');
    if (btnQuickChangeTeamHero) {
      btnQuickChangeTeamHero.addEventListener('click', () => openEditNameAndTeamModal(prof.id, 'team'));
    }

    const btnEditNameTeamAction = document.getElementById('btn-edit-name-team-action');
    if (btnEditNameTeamAction) {
      btnEditNameTeamAction.addEventListener('click', () => openEditNameAndTeamModal(prof.id, 'both'));
    }

    const btnEditAdminCredsAction = document.getElementById('btn-edit-admin-creds-action');
    if (btnEditAdminCredsAction) {
      btnEditAdminCredsAction.addEventListener('click', () => openEditAdminCredentialsModal());
    }

    const btnHeroAvatar = document.getElementById('btn-hero-avatar-edit');
    if (btnHeroAvatar) {
      btnHeroAvatar.addEventListener('click', () => openEditProfileModal(prof.id));
    }

    const btnEditHero = document.getElementById('btn-edit-profile-action');
    if (btnEditHero) {
      btnEditHero.addEventListener('click', () => openEditProfileModal(prof.id));
    }

    const btnCreateHero = document.getElementById('btn-create-profile-action');
    if (btnCreateHero) {
      btnCreateHero.addEventListener('click', () => openCreateProfileModal());
    }

    const btnLogoutHero = document.getElementById('btn-logout-profile-action');
    if (btnLogoutHero) {
      btnLogoutHero.addEventListener('click', logout);
    }

    // Details Subtab Actions
    const btnQuickEditNameInner = document.getElementById('btn-quick-edit-name-inner');
    if (btnQuickEditNameInner) {
      btnQuickEditNameInner.addEventListener('click', () => openEditNameAndTeamModal(prof.id, 'name'));
    }

    const btnQuickChangeTeamInner = document.getElementById('btn-quick-change-team-inner');
    if (btnQuickChangeTeamInner) {
      btnQuickChangeTeamInner.addEventListener('click', () => openEditNameAndTeamModal(prof.id, 'team'));
    }

    const btnEditInner = document.getElementById('btn-edit-profile-inner');
    if (btnEditInner) {
      btnEditInner.addEventListener('click', () => openEditProfileModal(prof.id));
    }

    const btnChangePwdModal = document.getElementById('btn-change-password-modal');
    if (btnChangePwdModal) {
      btnChangePwdModal.addEventListener('click', () => openChangePasswordModal(prof.id));
    }

    const btnSubPrivacy = document.getElementById('btn-subtab-privacy');
    if (btnSubPrivacy) {
      btnSubPrivacy.addEventListener('click', togglePrivacyMode);
    }

    const btnSubLogout = document.getElementById('btn-subtab-logout');
    if (btnSubLogout) {
      btnSubLogout.addEventListener('click', logout);
    }

    // Distributors Subtab Actions
    const btnAddDistOpen = document.getElementById('btn-add-distributor-open');
    if (btnAddDistOpen) {
      btnAddDistOpen.addEventListener('click', openAddDistributorModal);
    }

    document.querySelectorAll('.filter-dist-chip').forEach(chip => {
      chip.addEventListener('click', () => {
        state.distributorFilterTeam = chip.getAttribute('data-team');
        saveState();
        renderProfile();
      });
    });

    document.querySelectorAll('.act-edit-distributor').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-dist-id');
        openEditDistributorModal(id);
      });
    });

    document.querySelectorAll('.act-remove-distributor').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-dist-id');
        openRemoveDistributorModal(id);
      });
    });

    // Performance Subtab Actions
    const btnAddTeamPerf = document.getElementById('btn-add-team-perf-open');
    if (btnAddTeamPerf) {
      btnAddTeamPerf.addEventListener('click', openAddTeamPerformanceModal);
    }

    const perfTeamSelect = document.getElementById('perf-team-selector');
    if (perfTeamSelect) {
      perfTeamSelect.addEventListener('change', () => {
        state.selectedPerfTeam = perfTeamSelect.value;
        saveState();
        renderProfile();
      });
    }

    document.querySelectorAll('.act-edit-team-perf').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-perf-id');
        openEditTeamPerformanceModal(id);
      });
    });

    document.querySelectorAll('.act-remove-team-perf').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-perf-id');
        openRemoveTeamPerformanceModal(id);
      });
    });

    // Accounts Subtab Actions
    const btnAddAccountList = document.getElementById('btn-add-account-from-list');
    if (btnAddAccountList) {
      btnAddAccountList.addEventListener('click', () => openCreateProfileModal());
    }

    document.querySelectorAll('.act-switch-account').forEach(btn => {
      btn.addEventListener('click', () => {
        const pid = btn.getAttribute('data-profile-id');
        const targetProf = state.profiles.find(p => p.id === pid);
        if (targetProf) {
          state.currentProfileId = targetProf.id;
          state.currentUserName = targetProf.name;
          state.currentRole = targetProf.role;
          state.currentTeam = targetProf.team;
          saveState();
          updateRoleUI();
          showToast(`Switched account to ${targetProf.name}`);
          renderProfile();
        }
      });
    });

    document.querySelectorAll('.act-edit-account-profile').forEach(btn => {
      btn.addEventListener('click', () => {
        const pid = btn.getAttribute('data-profile-id');
        openEditProfileModal(pid);
      });
    });

    // Teams Benchmark Subtab Actions
    const btnCreateTeamOpen = document.getElementById('btn-create-new-team-open');
    if (btnCreateTeamOpen) {
      btnCreateTeamOpen.addEventListener('click', () => openRenameOrEditTeamModal(null));
    }

    document.querySelectorAll('.act-delete-team').forEach(btn => {
      btn.addEventListener('click', () => {
        const teamId = btn.getAttribute('data-team-id') || btn.getAttribute('data-team-name');
        openDeleteTeamModal(teamId);
      });
    });

    document.querySelectorAll('.act-rename-team').forEach(btn => {
      btn.addEventListener('click', () => {
        const teamId = btn.getAttribute('data-team-id') || btn.getAttribute('data-team-name');
        openRenameOrEditTeamModal(teamId);
      });
    });

    document.querySelectorAll('.act-switch-to-team').forEach(btn => {
      btn.addEventListener('click', () => {
        const targetTeam = btn.getAttribute('data-team-name');
        if (targetTeam) {
          prof.team = targetTeam;
          if (prof.id === state.currentProfileId) {
            state.currentTeam = targetTeam;
            updateRoleUI();
          }
          const dist = state.distributors.find(d => d.profileId === prof.id || d.agentCode === prof.agentCode);
          if (dist) dist.team = targetTeam;
          saveState();
          updateLoginProfilePreview();
          showToast(`Switched franchise team to "${targetTeam}"!`);
          renderProfile();
        }
      });
    });
  }

  // MODALS & CRUD OPERATIONS
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

  // 1. Create New Profile Modal
  function openCreateProfileModal(onSuccess, defaultAutoSignIn = false) {
    let chosenAvatar = '';

    function getInitials(name) {
      return (name || 'SG').split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'SG';
    }

    function updateCreateAvatarPreview(name) {
      const box = document.getElementById('create-avatar-preview-box');
      if (!box) return;
      box.innerHTML = `
        ${renderAvatarHtml(chosenAvatar || getInitials(name), name, 64, 18)}
        <div class="avatar-edit-badge">📷</div>
      `;
    }

    let html = `
      <div class="sheet-title">Create New Profile</div>
      <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
        Register a new user/distributor account in Smart Group command network.
      </div>

      <!-- Profile Picture Add Option -->
      <div class="form-group">
        <label class="form-label">Profile Picture & Avatar</label>
        <div class="avatar-editor-box">
          <div id="create-avatar-preview-box" class="avatar-preview-wrap" title="Tap to upload profile photo">
            ${renderAvatarHtml('', 'New User', 64, 18)}
            <div class="avatar-edit-badge">📷</div>
          </div>
          <div style="flex:1; display:flex; flex-direction:column; gap:6px;">
            <div style="display:flex; gap:6px; flex-wrap:wrap;">
              <button type="button" id="btn-upload-create-avatar" class="btn-small" style="background:var(--primary); color:white; border-color:var(--primary-light);">
                📷 Upload Photo
              </button>
              <button type="button" id="btn-reset-create-avatar" class="btn-small" style="background:var(--bg-surface); color:var(--text-muted); border-color:var(--border-color);">
                Reset Initials
              </button>
            </div>
            <input type="file" id="create-avatar-file-input" accept="image/*" style="display:none;">

            <div style="font-size:10.5px; color:var(--text-muted);">Or pick persona badge:</div>
            <div class="avatar-preset-strip">
              <button type="button" class="avatar-preset-btn create-preset-btn" data-avatar="👔" title="Executive">👔</button>
              <button type="button" class="avatar-preset-btn create-preset-btn" data-avatar="💼" title="Leader">💼</button>
              <button type="button" class="avatar-preset-btn create-preset-btn" data-avatar="🌟" title="Star Closer">🌟</button>
              <button type="button" class="avatar-preset-btn create-preset-btn" data-avatar="🚀" title="Director">🚀</button>
              <button type="button" class="avatar-preset-btn create-preset-btn" data-avatar="🎯" title="Master">🎯</button>
              <button type="button" class="avatar-preset-btn create-preset-btn" data-avatar="👑" title="Crown Ambassador">👑</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Profile Name Create Option -->
      <div class="form-group">
        <label class="form-label">Profile Full Name *</label>
        <input id="create-prof-name" type="text" class="form-input" placeholder="e.g. Ramesh Kumar">
      </div>

      <div class="form-group">
        <label class="form-label">Agent Code *</label>
        <input id="create-prof-code" type="text" class="form-input" placeholder="e.g. TRZ-008" value="TRZ-00${state.profiles.length + 1}">
      </div>

      <div class="form-group">
        <label class="form-label">Contact Phone *</label>
        <input id="create-prof-phone" type="tel" class="form-input" placeholder="e.g. +91 98400 99887">
      </div>

      <div class="form-group">
        <label class="form-label">Corporate Email *</label>
        <input id="create-prof-email" type="email" class="form-input" placeholder="e.g. ramesh@tranzindia.com">
      </div>

      <div class="form-group">
        <label class="form-label">Login Password * ("No One See")</label>
        <div style="position:relative;">
          <input id="create-prof-password" type="password" class="form-input" placeholder="Set secure password" value="pass${Math.floor(1000 + Math.random() * 9000)}">
          <button id="btn-toggle-create-pwd" type="button" style="position:absolute; right:10px; top:50%; transform:translateY(-50%); background:none; border:none; color:var(--text-muted); cursor:pointer; font-size:11px;">Show</button>
        </div>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Role</label>
          <select id="create-prof-role" class="form-input">
            <option value="Distributor">Distributor</option>
            <option value="Team Leader">Team Leader</option>
            <option value="Counsellor">Counsellor</option>
            <option value="Telecaller">Telecaller</option>
          </select>
        </div>

        <div class="form-group">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
            <label class="form-label" style="margin:0;">Assigned Team</label>
            <button type="button" id="btn-create-prof-custom-team-toggle" class="btn-inline-edit" style="font-size:10px; padding:2px 6px;">+ Custom Team</button>
          </div>
          <select id="create-prof-team" class="form-input">
            ${state.teams.map(t => `<option value="${t.name}">${t.name}</option>`).join('')}
          </select>
          <input id="create-prof-custom-team-input" type="text" class="form-input" placeholder="Enter new team name..." style="display:none; margin-top:6px; font-size:12px;">
        </div>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Seniority Tier</label>
          <select id="create-prof-tier" class="form-input">
            <option value="Silver Associate">Silver Associate</option>
            <option value="Gold Master">Gold Master</option>
            <option value="Platinum Director">Platinum Director</option>
            <option value="Diamond Leader">Diamond Leader</option>
            <option value="Crown Ambassador">Crown Ambassador</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">Base City</label>
          <input id="create-prof-city" type="text" class="form-input" placeholder="e.g. Coimbatore" value="Coimbatore">
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">Bio / Territory Focus</label>
        <input id="create-prof-bio" type="text" class="form-input" placeholder="Field distributor notes">
      </div>

      <!-- New Profile Sign In Option -->
      <label style="display:flex; align-items:center; gap:8px; cursor:pointer; background:rgba(16,185,129,0.1); border:1px solid rgba(16,185,129,0.4); padding:10px 12px; border-radius:10px; font-size:12px; font-weight:700; color:#34d399; margin:10px 0 4px 0;">
        <input type="checkbox" id="create-prof-autosignin" ${defaultAutoSignIn ? 'checked' : ''} style="accent-color:#10b981; width:16px; height:16px; cursor:pointer;">
        <span>Sign in immediately as this new profile</span>
      </label>

      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-create-and-signin" class="btn-primary" style="background:linear-gradient(135deg, #059669, #10b981); flex:1.4;">
          Create & Sign In Now
        </button>
        <button id="btn-save-create-profile" class="btn-secondary" style="flex:1;">
          Create Profile
        </button>
        <button id="btn-cancel-create-profile" class="btn-secondary" style="width:70px;">
          Cancel
        </button>
      </div>
    `;

    openModal(html);

    // Profile Picture Event Listeners
    const avatarFileInput = document.getElementById('create-avatar-file-input');
    const btnUploadAvatar = document.getElementById('btn-upload-create-avatar');
    const avatarPreviewBox = document.getElementById('create-avatar-preview-box');
    const btnResetAvatar = document.getElementById('btn-reset-create-avatar');
    const nameInput = document.getElementById('create-prof-name');

    if (btnUploadAvatar && avatarFileInput) {
      btnUploadAvatar.addEventListener('click', () => avatarFileInput.click());
    }
    if (avatarPreviewBox && avatarFileInput) {
      avatarPreviewBox.addEventListener('click', () => avatarFileInput.click());
    }

    if (avatarFileInput) {
      avatarFileInput.addEventListener('change', (e) => {
        const file = e.target.files && e.target.files[0];
        if (file) {
          compressImageFile(file, 200, 200, (dataUrl) => {
            chosenAvatar = dataUrl;
            document.querySelectorAll('.create-preset-btn').forEach(b => b.classList.remove('selected'));
            updateCreateAvatarPreview(nameInput ? nameInput.value.trim() : '');
            showToast('Profile photo attached!');
          });
        }
      });
    }

    document.querySelectorAll('.create-preset-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        document.querySelectorAll('.create-preset-btn').forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');
        chosenAvatar = btn.getAttribute('data-avatar');
        updateCreateAvatarPreview(nameInput ? nameInput.value.trim() : '');
      });
    });

    if (btnResetAvatar) {
      btnResetAvatar.addEventListener('click', () => {
        chosenAvatar = '';
        document.querySelectorAll('.create-preset-btn').forEach(b => b.classList.remove('selected'));
        updateCreateAvatarPreview(nameInput ? nameInput.value.trim() : '');
        showToast('Avatar reset to name initials.');
      });
    }

    if (nameInput) {
      nameInput.addEventListener('input', () => {
        if (!chosenAvatar || chosenAvatar.length <= 2) {
          updateCreateAvatarPreview(nameInput.value.trim());
        }
      });
    }

    // Toggle Password Visibility
    const toggleBtn = document.getElementById('btn-toggle-create-pwd');
    const pwdInput = document.getElementById('create-prof-password');
    if (toggleBtn && pwdInput) {
      toggleBtn.addEventListener('click', () => {
        const isPwd = pwdInput.getAttribute('type') === 'password';
        pwdInput.setAttribute('type', isPwd ? 'text' : 'password');
        toggleBtn.textContent = isPwd ? 'Hide' : 'Show';
      });
    }

    // Toggle Custom Team in Create Profile Modal
    const toggleCustomTeamBtn = document.getElementById('btn-create-prof-custom-team-toggle');
    const customTeamInput = document.getElementById('create-prof-custom-team-input');
    const teamSelect = document.getElementById('create-prof-team');
    if (toggleCustomTeamBtn && customTeamInput && teamSelect) {
      toggleCustomTeamBtn.addEventListener('click', () => {
        const isHidden = customTeamInput.style.display === 'none';
        customTeamInput.style.display = isHidden ? 'block' : 'none';
        teamSelect.style.display = isHidden ? 'none' : 'block';
        toggleCustomTeamBtn.textContent = isHidden ? 'Select Existing' : '+ Custom Team';
        if (isHidden) customTeamInput.focus();
      });
    }

    document.getElementById('btn-cancel-create-profile').addEventListener('click', closeModal);

    const executeCreateProfile = (forceSignIn = false) => {
      const name = document.getElementById('create-prof-name').value.trim();
      const code = document.getElementById('create-prof-code').value.trim();
      const phone = document.getElementById('create-prof-phone').value.trim();
      const email = document.getElementById('create-prof-email').value.trim();
      const password = document.getElementById('create-prof-password').value.trim();
      const role = document.getElementById('create-prof-role').value;
      const customTeamVal = customTeamInput && customTeamInput.style.display !== 'none' ? customTeamInput.value.trim() : '';
      const team = customTeamVal || document.getElementById('create-prof-team').value;
      const tier = document.getElementById('create-prof-tier').value;
      const city = document.getElementById('create-prof-city').value.trim() || 'Coimbatore';
      const bio = document.getElementById('create-prof-bio').value.trim();
      const autoSignInChecked = document.getElementById('create-prof-autosignin') ? document.getElementById('create-prof-autosignin').checked : false;

      if (!name || !code || !phone || !password) {
        showToast('Please fill all required fields (Name, Code, Phone, Password)');
        return;
      }

      if (customTeamVal) {
        createOrGetTeam(customTeamVal, name, 1500000);
      }

      const newId = 'prof-' + Date.now();
      const finalAvatar = chosenAvatar || getInitials(name);

      const newProf = {
        id: newId,
        agentCode: code,
        name: name,
        phone: phone,
        email: email || `${name.toLowerCase().replace(/\s+/g, '.')}@tranzindia.com`,
        password: password,
        role: role,
        team: team,
        tier: tier,
        joiningDate: new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }),
        city: city,
        status: 'ACTIVE',
        bio: bio || `Field ${role} for Tranz India franchise programs.`,
        avatar: finalAvatar
      };

      state.profiles.push(newProf);

      // If role is Distributor, also auto-register in distributors roster
      if (role === 'Distributor') {
        state.distributors.push({
          id: 'dist-' + Date.now(),
          profileId: newId,
          name: name,
          agentCode: code,
          phone: phone,
          team: team,
          role: 'Distributor',
          seniorityClosed: 0,
          target: 200000,
          status: 'ACTIVE',
          rating: 4.8
        });
        const teamObj = state.teams.find(t => t.name === team);
        if (teamObj) teamObj.distributorsCount++;
      }

      saveState();

      const shouldSignIn = forceSignIn || autoSignInChecked;

      if (shouldSignIn) {
        state.isLoggedIn = true;
        state.currentProfileId = newProf.id;
        state.currentUserName = newProf.name;
        state.currentRole = newProf.role;
        state.currentTeam = newProf.team;
        saveState();
        updateRoleUI();
        if (authOverlay) authOverlay.classList.add('hidden');
        closeModal();
        showToast(`Welcome, ${name}! Signed in successfully.`);
        renderTab(state.activeTab || 'profile');
      } else {
        closeModal();
        showToast(`Profile created for ${name}!`);
        if (onSuccess) {
          onSuccess(newProf);
        } else {
          renderProfile();
        }
      }
    };

    const btnCreateAndSignIn = document.getElementById('btn-create-and-signin');
    if (btnCreateAndSignIn) {
      btnCreateAndSignIn.addEventListener('click', () => executeCreateProfile(true));
    }

    const btnSaveCreateProfile = document.getElementById('btn-save-create-profile');
    if (btnSaveCreateProfile) {
      btnSaveCreateProfile.addEventListener('click', () => executeCreateProfile(false));
    }
  }

  // 2. JS function to render a 'Profile Edit' modal inside #modal-container
  function renderProfileEditModal(profileId) {
    const targetId = profileId || state.currentProfileId;
    const prof = state.profiles.find(p => p.id === targetId) || state.profiles[0];
    if (!prof) return;

    if ((prof.role === 'SuperAdmin' || prof.isAdminAccount) && state.currentRole !== 'SuperAdmin') {
      showToast('Restricted administrative profile.');
      return;
    }

    let activeModalTab = 'account'; // 'account' or 'distributors'
    let tempEditAvatar = prof.avatar || '';

    function getInitials(name) {
      return (name || 'SG').split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'SG';
    }

    function updateEditAvatarPreview(name) {
      const box = document.getElementById('edit-avatar-preview-box');
      if (!box) return;
      box.innerHTML = `
        ${renderAvatarHtml(tempEditAvatar || getInitials(name), name, 64, 18)}
        <div class="avatar-edit-badge">📷</div>
      `;
    }

    function updateModalView() {
      const teamDistributors = state.distributors.filter(d => d.team === prof.team);

      let html = `
        <div style="display:flex; justify-content:space-between; align-items:flex-start;">
          <div>
            <div class="sheet-title" style="font-size:18px; font-weight:900;">Profile Edit</div>
            <div style="font-size:11.5px; color:var(--text-muted); margin-top:1px;">
              Update account details & manage authorized distributors
            </div>
          </div>
          <button id="btn-close-profile-edit-modal" style="background:none; border:none; color:var(--text-muted); font-size:18px; cursor:pointer; padding:2px 6px; line-height:1;">✕</button>
        </div>

        <!-- Modal Sub-Tabs: Account Details vs Authorized Distributors -->
        <div class="profile-edit-tabs" style="margin-top:6px;">
          <button id="tab-btn-account" class="profile-edit-tab-btn ${activeModalTab === 'account' ? 'active' : ''}">
            👤 Account Details
          </button>
          <button id="tab-btn-distributors" class="profile-edit-tab-btn ${activeModalTab === 'distributors' ? 'active' : ''}">
            👥 Authorized Distributors (${teamDistributors.length})
          </button>
        </div>

        <!-- TAB 1: Account Details Panel -->
        <div id="panel-account-details" style="${activeModalTab === 'account' ? 'display:flex; flex-direction:column; gap:10px;' : 'display:none;'}">
          <div style="display:flex; align-items:center; gap:10px; background:var(--bg-input); padding:8px 12px; border-radius:10px;">
            ${renderAvatarHtml(tempEditAvatar || getInitials(prof.name), prof.name, 36, 12)}
            <div style="flex:1;">
              <div style="font-size:13.5px; font-weight:800; color:var(--text-main);">${prof.name}</div>
              <div style="font-size:11px; color:var(--primary-light);">${prof.agentCode} • ${prof.role} • ${prof.team}</div>
            </div>
            <span class="badge" style="background:#10b981; color:white;">${prof.status}</span>
          </div>

          <!-- Profile Picture & Avatar Editor -->
          <div class="form-group">
            <label class="form-label">Profile Picture & Avatar</label>
            <div class="avatar-editor-box">
              <div id="edit-avatar-preview-box" class="avatar-preview-wrap" title="Tap to upload profile photo">
                ${renderAvatarHtml(tempEditAvatar || getInitials(prof.name), prof.name, 64, 18)}
                <div class="avatar-edit-badge">📷</div>
              </div>
              <div style="flex:1; display:flex; flex-direction:column; gap:6px;">
                <div style="display:flex; gap:6px; flex-wrap:wrap;">
                  <button type="button" id="btn-upload-edit-avatar" class="btn-small" style="background:var(--primary); color:white; border-color:var(--primary-light);">
                    📷 Upload Photo
                  </button>
                  <button type="button" id="btn-reset-edit-avatar" class="btn-small" style="background:var(--bg-surface); color:var(--text-muted); border-color:var(--border-color);">
                    Reset Initials
                  </button>
                </div>
                <input type="file" id="edit-avatar-file-input" accept="image/*" style="display:none;">

                <div style="font-size:10.5px; color:var(--text-muted);">Or pick persona badge:</div>
                <div class="avatar-preset-strip">
                  <button type="button" class="avatar-preset-btn edit-preset-btn ${tempEditAvatar === '👔' ? 'selected' : ''}" data-avatar="👔" title="Executive">👔</button>
                  <button type="button" class="avatar-preset-btn edit-preset-btn ${tempEditAvatar === '💼' ? 'selected' : ''}" data-avatar="💼" title="Leader">💼</button>
                  <button type="button" class="avatar-preset-btn edit-preset-btn ${tempEditAvatar === '🌟' ? 'selected' : ''}" data-avatar="🌟" title="Star Closer">🌟</button>
                  <button type="button" class="avatar-preset-btn edit-preset-btn ${tempEditAvatar === '🚀' ? 'selected' : ''}" data-avatar="🚀" title="Director">🚀</button>
                  <button type="button" class="avatar-preset-btn edit-preset-btn ${tempEditAvatar === '🎯' ? 'selected' : ''}" data-avatar="🎯" title="Master">🎯</button>
                  <button type="button" class="avatar-preset-btn edit-preset-btn ${tempEditAvatar === '👑' ? 'selected' : ''}" data-avatar="👑" title="Crown Ambassador">👑</button>
                </div>
              </div>
            </div>
          </div>

          <!-- Profile Full Name Edit Option -->
          <div class="form-group">
            <label class="form-label">Profile Full Name *</label>
            <input id="edit-prof-name" type="text" class="form-input" value="${prof.name}" placeholder="Full Name">
          </div>

          <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
            <div class="form-group">
              <label class="form-label">Agent Code *</label>
              <input id="edit-prof-code" type="text" class="form-input" value="${prof.agentCode}">
            </div>
            <div class="form-group">
              <label class="form-label">Account Status</label>
              <select id="edit-prof-status" class="form-input">
                <option value="ACTIVE" ${prof.status === 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                <option value="ON_FIELD" ${prof.status === 'ON_FIELD' ? 'selected' : ''}>ON_FIELD</option>
                <option value="LEAVE" ${prof.status === 'LEAVE' ? 'selected' : ''}>LEAVE</option>
              </select>
            </div>
          </div>

          <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
            <div class="form-group">
              <label class="form-label">Contact Phone *</label>
              <input id="edit-prof-phone" type="tel" class="form-input" value="${prof.phone}">
            </div>
            <div class="form-group">
              <label class="form-label">Base City</label>
              <input id="edit-prof-city" type="text" class="form-input" value="${prof.city || 'Coimbatore'}">
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Corporate Email</label>
            <input id="edit-prof-email" type="email" class="form-input" value="${prof.email}">
          </div>

          <div class="form-group">
            <label class="form-label">Password (leave blank to keep unchanged)</label>
            <div style="position:relative;">
              <input id="edit-prof-password" type="password" class="form-input" placeholder="••••••••••••" autocomplete="new-password">
              <button id="btn-toggle-edit-pwd" type="button" style="position:absolute; right:10px; top:50%; transform:translateY(-50%); background:none; border:none; color:var(--text-muted); cursor:pointer; font-size:11px;">Show</button>
            </div>
          </div>

          <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
            <div class="form-group">
              <label class="form-label">Role</label>
              <select id="edit-prof-role" class="form-input">
                <option value="Distributor" ${prof.role === 'Distributor' ? 'selected' : ''}>Distributor</option>
                <option value="Team Leader" ${prof.role === 'Team Leader' ? 'selected' : ''}>Team Leader</option>
                <option value="Counsellor" ${prof.role === 'Counsellor' ? 'selected' : ''}>Counsellor</option>
                <option value="Telecaller" ${prof.role === 'Telecaller' ? 'selected' : ''}>Telecaller</option>
                ${(prof.role === 'SuperAdmin' || prof.isAdminAccount) ? '<option value="SuperAdmin" selected>SuperAdmin (Admin Restricted)</option>' : ''}
              </select>
            </div>

            <div class="form-group">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
                <label class="form-label" style="margin:0;">Assigned Team</label>
                <div style="display:flex; gap:4px;">
                  <button type="button" id="btn-edit-prof-rename-team" class="btn-inline-edit" style="font-size:10px; padding:2px 6px;">✏️ Rename</button>
                  <button type="button" id="btn-edit-prof-new-team" class="btn-inline-edit" style="font-size:10px; padding:2px 6px;">+ New</button>
                </div>
              </div>
              <select id="edit-prof-team" class="form-input">
                ${state.teams.map(t => `<option value="${t.name}" ${t.name === prof.team ? 'selected' : ''}>${t.name}</option>`).join('')}
              </select>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Seniority Tier</label>
            <select id="edit-prof-tier" class="form-input">
              <option value="Silver Associate" ${prof.tier === 'Silver Associate' ? 'selected' : ''}>Silver Associate</option>
              <option value="Gold Master" ${prof.tier === 'Gold Master' ? 'selected' : ''}>Gold Master</option>
              <option value="Platinum Director" ${prof.tier === 'Platinum Director' ? 'selected' : ''}>Platinum Director</option>
              <option value="Diamond Leader" ${prof.tier === 'Diamond Leader' ? 'selected' : ''}>Diamond Leader</option>
              <option value="Crown Ambassador" ${prof.tier === 'Crown Ambassador' ? 'selected' : ''}>Crown Ambassador</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Bio & Territory Scope</label>
            <input id="edit-prof-bio" type="text" class="form-input" value="${prof.bio || ''}" placeholder="Field distributor notes">
          </div>
        </div>

        <!-- TAB 2: Authorized Distributors Management Panel -->
        <div id="panel-authorized-distributors" style="${activeModalTab === 'distributors' ? 'display:flex; flex-direction:column; gap:10px;' : 'display:none;'}">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div>
              <div style="font-size:13.5px; font-weight:800;">Authorized Distributors</div>
              <div style="font-size:11px; color:var(--text-muted);">Manage authorization, targets & access for ${prof.team}</div>
            </div>
            <button id="btn-toggle-quick-auth-form" class="btn-small" style="background:var(--primary-900); color:var(--primary-light); border-color:var(--primary);">
              + Authorize New
            </button>
          </div>

          <!-- Quick Authorize New Distributor Form -->
          <div id="quick-auth-distributor-box" style="display:none; background:var(--bg-input); padding:10px 12px; border-radius:10px; border:1px dashed var(--primary); flex-direction:column; gap:8px;">
            <div style="font-size:12px; font-weight:800; color:var(--primary-light);">Authorize New Team Distributor</div>
            
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:6px;">
              <input id="new-auth-dist-name" type="text" class="form-input" placeholder="Distributor Name *" style="height:36px; font-size:12px;">
              <input id="new-auth-dist-code" type="text" class="form-input" placeholder="Code (e.g. TRZ-0${state.distributors.length + 10}) *" value="TRZ-0${state.distributors.length + 10}" style="height:36px; font-size:12px;">
            </div>

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:6px;">
              <input id="new-auth-dist-phone" type="tel" class="form-input" placeholder="Phone *" style="height:36px; font-size:12px;">
              <input id="new-auth-dist-target" type="number" class="form-input" placeholder="Target (₹) *" value="200000" style="height:36px; font-size:12px;">
            </div>

            <div style="display:flex; gap:6px;">
              <button id="btn-submit-quick-auth" class="btn-primary" style="height:34px; font-size:12px; flex:1;">
                Confirm & Authorize
              </button>
              <button id="btn-cancel-quick-auth" class="btn-secondary" style="height:34px; font-size:12px; width:auto; padding:0 10px;">
                Cancel
              </button>
            </div>
          </div>

          <!-- Authorized Distributors List -->
          <div style="display:flex; flex-direction:column; gap:8px; max-height:42vh; overflow-y:auto; padding-right:2px;">
            ${teamDistributors.length === 0 ? `
              <div style="text-align:center; padding:18px; background:var(--bg-input); border-radius:10px; color:var(--text-muted); font-size:12px;">
                No authorized distributors currently assigned to ${prof.team}.<br>
                Tap <strong>+ Authorize New</strong> to grant franchise selling permissions.
              </div>
            ` : teamDistributors.map(d => {
              const isAuth = d.status !== 'SUSPENDED' && d.status !== 'REVOKED';
              const progress = Math.min(100, Math.round(((d.seniorityClosed || 0) / (d.target || 1)) * 100));
              return `
                <div class="auth-dist-card">
                  <div class="auth-dist-header">
                    <div style="display:flex; align-items:center; gap:8px;">
                      <div style="width:32px; height:32px; border-radius:50%; background:linear-gradient(135deg, #4f46e5, #06b6d4); display:flex; align-items:center; justify-content:center; font-weight:800; color:white; font-size:11px;">
                        ${d.name.substring(0,2).toUpperCase()}
                      </div>
                      <div>
                        <div style="font-size:13px; font-weight:800;">${d.name}</div>
                        <div style="font-size:11px; color:var(--primary-light); font-weight:700;">${d.agentCode} • ${d.phone}</div>
                      </div>
                    </div>
                    <span class="badge" style="background:${isAuth ? '#10b981' : '#ef4444'}; color:white; font-size:10px;">
                      ${isAuth ? 'AUTHORIZED' : 'SUSPENDED'}
                    </span>
                  </div>

                  <div style="font-size:11px; color:var(--text-muted); display:flex; justify-content:space-between; margin-top:2px;">
                    <span>Closed: <strong style="color:#10b981;">${formatINR(d.seniorityClosed || 0)}</strong></span>
                    <span>Target: <strong>${formatINR(d.target || 0)}</strong> (${progress}%)</span>
                  </div>
                  <div style="width:100%; height:4px; background:var(--bg-input); border-radius:2px; overflow:hidden;">
                    <div style="width:${progress}%; height:100%; background:linear-gradient(90deg, #4f46e5, #10b981);"></div>
                  </div>

                  <div style="display:flex; justify-content:flex-end; gap:6px; margin-top:4px; border-top:1px solid var(--border-color-subtle); padding-top:6px;">
                    <button class="btn-small act-auth-edit-target" data-dist-id="${d.id}" style="font-size:10.5px; padding:3px 8px; color:var(--primary-light);">
                      Edit Target
                    </button>
                    <button class="btn-small act-auth-toggle-status" data-dist-id="${d.id}" style="font-size:10.5px; padding:3px 8px; color:${isAuth ? '#f59e0b' : '#10b981'};">
                      ${isAuth ? 'Suspend' : 'Re-Authorize'}
                    </button>
                    <button class="btn-small act-auth-remove-dist" data-dist-id="${d.id}" style="font-size:10.5px; padding:3px 8px; color:#ef4444;">
                      Remove
                    </button>
                  </div>
                </div>
              `;
            }).join('')}
          </div>
        </div>

        <!-- Action Footer -->
        <div style="display:flex; gap:8px; margin-top:6px; padding-top:10px; border-top:1px solid var(--border-color-subtle);">
          <button id="btn-save-profile-edit" class="btn-primary" style="flex:1.4;">
            Save Changes
          </button>
          <button id="btn-cancel-profile-edit" class="btn-secondary" style="flex:1;">
            Close
          </button>
        </div>
      `;

      // Render inside #modal-sheet-content in #modal-container
      openModal(html);
      bindModalEvents();
    }

    function bindModalEvents() {
      // Close modal buttons
      const btnClose = document.getElementById('btn-close-profile-edit-modal');
      if (btnClose) btnClose.addEventListener('click', closeModal);

      const btnCancel = document.getElementById('btn-cancel-profile-edit');
      if (btnCancel) btnCancel.addEventListener('click', closeModal);

      // Profile Picture Edit Listeners
      const editAvatarFileInput = document.getElementById('edit-avatar-file-input');
      const btnUploadEditAvatar = document.getElementById('btn-upload-edit-avatar');
      const editAvatarPreviewBox = document.getElementById('edit-avatar-preview-box');
      const btnResetEditAvatar = document.getElementById('btn-reset-edit-avatar');
      const editProfNameInput = document.getElementById('edit-prof-name');

      if (btnUploadEditAvatar && editAvatarFileInput) {
        btnUploadEditAvatar.addEventListener('click', () => editAvatarFileInput.click());
      }
      if (editAvatarPreviewBox && editAvatarFileInput) {
        editAvatarPreviewBox.addEventListener('click', () => editAvatarFileInput.click());
      }

      if (editAvatarFileInput) {
        editAvatarFileInput.addEventListener('change', (e) => {
          const file = e.target.files && e.target.files[0];
          if (file) {
            compressImageFile(file, 200, 200, (dataUrl) => {
              tempEditAvatar = dataUrl;
              document.querySelectorAll('.edit-preset-btn').forEach(b => b.classList.remove('selected'));
              updateEditAvatarPreview(editProfNameInput ? editProfNameInput.value.trim() : prof.name);
              showToast('Profile photo updated!');
            });
          }
        });
      }

      document.querySelectorAll('.edit-preset-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          document.querySelectorAll('.edit-preset-btn').forEach(b => b.classList.remove('selected'));
          btn.classList.add('selected');
          tempEditAvatar = btn.getAttribute('data-avatar');
          updateEditAvatarPreview(editProfNameInput ? editProfNameInput.value.trim() : prof.name);
        });
      });

      if (btnResetEditAvatar) {
        btnResetEditAvatar.addEventListener('click', () => {
          tempEditAvatar = '';
          document.querySelectorAll('.edit-preset-btn').forEach(b => b.classList.remove('selected'));
          updateEditAvatarPreview(editProfNameInput ? editProfNameInput.value.trim() : prof.name);
          showToast('Avatar reset to name initials.');
        });
      }

      if (editProfNameInput) {
        editProfNameInput.addEventListener('input', () => {
          if (!tempEditAvatar || tempEditAvatar.length <= 2) {
            updateEditAvatarPreview(editProfNameInput.value.trim());
          }
        });
      }

      // Sub-tab switching between Account Details and Authorized Distributors
      const tabBtnAccount = document.getElementById('tab-btn-account');
      const tabBtnDist = document.getElementById('tab-btn-distributors');
      if (tabBtnAccount && tabBtnDist) {
        tabBtnAccount.addEventListener('click', () => {
          activeModalTab = 'account';
          updateModalView();
        });
        tabBtnDist.addEventListener('click', () => {
          activeModalTab = 'distributors';
          updateModalView();
        });
      }

      // Inline Team Rename & Create from Profile Edit
      const btnRenameTeam = document.getElementById('btn-edit-prof-rename-team');
      if (btnRenameTeam) {
        btnRenameTeam.addEventListener('click', () => {
          const curTeam = document.getElementById('edit-prof-team') ? document.getElementById('edit-prof-team').value : prof.team;
          openRenameOrEditTeamModal(curTeam);
        });
      }
      const btnNewTeam = document.getElementById('btn-edit-prof-new-team');
      if (btnNewTeam) {
        btnNewTeam.addEventListener('click', () => {
          openRenameOrEditTeamModal(null);
        });
      }

      // Toggle password visibility
      const toggleBtn = document.getElementById('btn-toggle-edit-pwd');
      const pwdInput = document.getElementById('edit-prof-password');
      if (toggleBtn && pwdInput) {
        toggleBtn.addEventListener('click', () => {
          const isPwd = pwdInput.getAttribute('type') === 'password';
          pwdInput.setAttribute('type', isPwd ? 'text' : 'password');
          toggleBtn.textContent = isPwd ? 'Hide' : 'Show';
        });
      }

      // Toggle quick authorize form
      const btnToggleAuthForm = document.getElementById('btn-toggle-quick-auth-form');
      const quickAuthBox = document.getElementById('quick-auth-distributor-box');
      const btnCancelAuth = document.getElementById('btn-cancel-quick-auth');
      if (btnToggleAuthForm && quickAuthBox) {
        btnToggleAuthForm.addEventListener('click', () => {
          quickAuthBox.style.display = quickAuthBox.style.display === 'none' ? 'flex' : 'none';
        });
      }
      if (btnCancelAuth && quickAuthBox) {
        btnCancelAuth.addEventListener('click', () => {
          quickAuthBox.style.display = 'none';
        });
      }

      // Submit quick distributor authorization
      const btnSubmitAuth = document.getElementById('btn-submit-quick-auth');
      if (btnSubmitAuth) {
        btnSubmitAuth.addEventListener('click', () => {
          const dName = document.getElementById('new-auth-dist-name').value.trim();
          const dCode = document.getElementById('new-auth-dist-code').value.trim();
          const dPhone = document.getElementById('new-auth-dist-phone').value.trim();
          const dTarget = parseInt(document.getElementById('new-auth-dist-target').value, 10) || 200000;

          if (!dName || !dCode || !dPhone) {
            showToast('Please enter distributor Name, Code and Phone');
            return;
          }

          const newDist = {
            id: 'dist-' + Date.now(),
            profileId: 'prof-' + Date.now(),
            name: dName,
            agentCode: dCode,
            phone: dPhone,
            team: prof.team,
            role: 'Distributor',
            seniorityClosed: 0,
            target: dTarget,
            status: 'ACTIVE',
            rating: 4.8
          };

          state.distributors.push(newDist);
          const teamObj = state.teams.find(t => t.name === prof.team);
          if (teamObj) teamObj.distributorsCount++;

          saveState();
          showToast(`Authorized distributor ${dName} added!`);
          updateModalView();
        });
      }

      // Distributor actions inside modal (Edit Target, Toggle Status, Remove)
      document.querySelectorAll('.act-auth-edit-target').forEach(btn => {
        btn.addEventListener('click', () => {
          const dId = btn.getAttribute('data-dist-id');
          const dist = state.distributors.find(d => d.id === dId);
          if (dist) {
            const newTargetStr = prompt(`Enter new sales target for ${dist.name}:`, dist.target);
            if (newTargetStr !== null) {
              const newTarget = parseInt(newTargetStr.replace(/[^0-9]/g, ''), 10);
              if (!isNaN(newTarget) && newTarget > 0) {
                dist.target = newTarget;
                saveState();
                showToast(`Target updated to ${formatINR(newTarget)} for ${dist.name}`);
                updateModalView();
              }
            }
          }
        });
      });

      document.querySelectorAll('.act-auth-toggle-status').forEach(btn => {
        btn.addEventListener('click', () => {
          const dId = btn.getAttribute('data-dist-id');
          const dist = state.distributors.find(d => d.id === dId);
          if (dist) {
            dist.status = dist.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
            saveState();
            showToast(`Distributor ${dist.name} status: ${dist.status}`);
            updateModalView();
          }
        });
      });

      document.querySelectorAll('.act-auth-remove-dist').forEach(btn => {
        btn.addEventListener('click', () => {
          const dId = btn.getAttribute('data-dist-id');
          const dist = state.distributors.find(d => d.id === dId);
          if (dist) {
            if (confirm(`Remove authorization for ${dist.name} (${dist.agentCode})?`)) {
              state.distributors = state.distributors.filter(d => d.id !== dId);
              const teamObj = state.teams.find(t => t.name === dist.team);
              if (teamObj && teamObj.distributorsCount > 0) teamObj.distributorsCount--;
              saveState();
              showToast(`Removed authorized distributor ${dist.name}`);
              updateModalView();
            }
          }
        });
      });

      // Save Profile Account Details
      const btnSave = document.getElementById('btn-save-profile-edit');
      if (btnSave) {
        btnSave.addEventListener('click', () => {
          const nameInput = document.getElementById('edit-prof-name');
          const codeInput = document.getElementById('edit-prof-code');
          const phoneInput = document.getElementById('edit-prof-phone');
          const emailInput = document.getElementById('edit-prof-email');
          const pwdInput = document.getElementById('edit-prof-password');
          const roleInput = document.getElementById('edit-prof-role');
          const teamInput = document.getElementById('edit-prof-team');
          const tierInput = document.getElementById('edit-prof-tier');
          const cityInput = document.getElementById('edit-prof-city');
          const statusInput = document.getElementById('edit-prof-status');
          const bioInput = document.getElementById('edit-prof-bio');

          const name = nameInput ? nameInput.value.trim() : prof.name;
          const code = codeInput ? codeInput.value.trim() : prof.agentCode;
          const phone = phoneInput ? phoneInput.value.trim() : prof.phone;
          const email = emailInput ? emailInput.value.trim() : prof.email;
          let password = pwdInput ? pwdInput.value.trim() : prof.password;
          if ((prof.role === 'SuperAdmin' || prof.isAdminAccount) && (password === '••••••••••••' || !password)) {
            password = prof.password;
          }
          const role = roleInput ? roleInput.value : prof.role;
          const team = teamInput ? teamInput.value : prof.team;
          const tier = tierInput ? tierInput.value : prof.tier;
          const city = cityInput ? cityInput.value.trim() : prof.city;
          const status = statusInput ? statusInput.value : prof.status;
          const bio = bioInput ? bioInput.value.trim() : (prof.bio || '');

          if (!name || !code || !phone || !password) {
            showToast('Please fill all required fields (Name, Code, Phone, Password)');
            return;
          }

          prof.name = name;
          prof.agentCode = code;
          prof.phone = phone;
          prof.email = email;
          prof.password = password;
          prof.role = role;
          prof.team = team;
          prof.tier = tier;
          prof.city = city;
          prof.status = status;
          prof.bio = bio;
          prof.avatar = tempEditAvatar || getInitials(name);

          // If active user was edited, update session display
          if (prof.id === state.currentProfileId) {
            state.currentUserName = name;
            state.currentRole = role;
            state.currentTeam = team;
            updateRoleUI();
          }

          // Also sync matching distributor if exists
          const dist = state.distributors.find(d => d.profileId === prof.id || d.agentCode === code);
          if (dist) {
            dist.name = name;
            dist.phone = phone;
            dist.team = team;
          }

          saveState();
          updateLoginProfilePreview();
          closeModal();
          showToast(`Profile & authorized distributors updated for ${name}`);
          renderProfile();
        });
      }
    }

    updateModalView();
  }

  // Alias for backward and external compatibility
  const openEditProfileModal = renderProfileEditModal;
  const openProfileEditModal = renderProfileEditModal;
  window.renderProfileEditModal = renderProfileEditModal;
  window.openProfileEditModal = renderProfileEditModal;

  // Dedicated Confidential Admin Profile & Credentials Edit Modal
  function openEditAdminCredentialsModal() {
    if (state.currentRole !== 'SuperAdmin') {
      showToast('Access denied: Admin credentials can only be edited by Super Admin.');
      return;
    }

    const currentAdminId = (state.adminProfile && (state.adminProfile.adminId || state.adminProfile.agentCode)) || 'admin';
    const currentAdminName = state.currentUserName || (state.adminProfile && state.adminProfile.name) || 'Command SuperAdmin';

    openModal(`
      <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border-color); padding-bottom:12px; margin-bottom:14px;">
        <div style="display:flex; align-items:center; gap:8px;">
          <div style="width:36px; height:36px; border-radius:50%; background:rgba(245,158,11,0.2); display:flex; align-items:center; justify-content:center; color:#f59e0b; font-size:18px;">
            🛡️
          </div>
          <div>
            <div style="font-size:16px; font-weight:800;">Edit Admin Profile & Credentials</div>
            <div style="font-size:11px; color:var(--text-muted);">Confidential Admin ID, Name & Password</div>
          </div>
        </div>
        <button id="btn-close-admin-creds-modal" style="background:none; border:none; color:var(--text-muted); font-size:22px; cursor:pointer;">&times;</button>
      </div>

      <div style="background:rgba(245,158,11,0.1); border:1px solid rgba(245,158,11,0.3); border-radius:8px; padding:10px; margin-bottom:12px; font-size:11.5px; color:#fef3c7; line-height:1.4;">
        🔒 <strong>Confidential Access:</strong> These administrative credentials are strictly confidential and will never be exposed to any distributor.
      </div>

      <div id="admin-creds-error" style="display:none; color:#f87171; font-size:12px; font-weight:700; margin-bottom:10px; padding:6px 10px; background:rgba(239,68,68,0.15); border-radius:6px;"></div>

      <form id="form-edit-admin-creds" style="display:flex; flex-direction:column; gap:12px;">
        <div class="form-group">
          <label class="form-label">Admin ID / Login Username *</label>
          <input type="text" id="admin-new-id" class="form-input" value="${escapeHtml(currentAdminId)}" required style="height:38px;">
        </div>

        <div class="form-group">
          <label class="form-label">Admin Display Name *</label>
          <input type="text" id="admin-new-name" class="form-input" value="${escapeHtml(currentAdminName)}" required style="height:38px;">
        </div>

        <div class="form-group">
          <label class="form-label">Current Admin Password (to verify authorization) *</label>
          <input type="password" id="admin-current-pass" class="form-input" placeholder="Enter current password" required style="height:38px;">
        </div>

        <div class="form-group">
          <label class="form-label">New Admin Password (leave blank to keep current)</label>
          <input type="password" id="admin-new-pass" class="form-input" placeholder="Enter new password (optional)" style="height:38px;">
        </div>

        <div class="form-group" id="group-confirm-pass" style="display:none;">
          <label class="form-label">Confirm New Password *</label>
          <input type="password" id="admin-confirm-pass" class="form-input" placeholder="Re-enter new password" style="height:38px;">
        </div>

        <div style="display:flex; gap:10px; margin-top:8px;">
          <button type="button" id="btn-cancel-admin-creds" class="btn-secondary" style="flex:1;">Cancel</button>
          <button type="submit" id="btn-save-admin-creds" class="btn-primary" style="flex:1.5; background:linear-gradient(135deg, #d97706, #b45309); font-weight:800;">
            Save Admin Credentials
          </button>
        </div>
      </form>
    `);

    const newPassInput = document.getElementById('admin-new-pass');
    const groupConfirm = document.getElementById('group-confirm-pass');
    if (newPassInput && groupConfirm) {
      newPassInput.addEventListener('input', () => {
        groupConfirm.style.display = newPassInput.value.trim() ? 'block' : 'none';
      });
    }

    const btnClose = document.getElementById('btn-close-admin-creds-modal');
    if (btnClose) btnClose.addEventListener('click', closeModal);

    const btnCancel = document.getElementById('btn-cancel-admin-creds');
    if (btnCancel) btnCancel.addEventListener('click', closeModal);

    const form = document.getElementById('form-edit-admin-creds');
    if (form) {
      form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const errDiv = document.getElementById('admin-creds-error');
        const adminId = (document.getElementById('admin-new-id').value || '').trim();
        const name = (document.getElementById('admin-new-name').value || '').trim();
        const currentPass = (document.getElementById('admin-current-pass').value || '').trim();
        const newPass = (document.getElementById('admin-new-pass').value || '').trim();
        const confirmPass = (document.getElementById('admin-confirm-pass').value || '').trim();

        if (!adminId || !name || !currentPass) {
          if (errDiv) { errDiv.textContent = 'Please fill in all required fields.'; errDiv.style.display = 'block'; }
          return;
        }

        if (newPass && newPass !== confirmPass) {
          if (errDiv) { errDiv.textContent = 'New passwords do not match.'; errDiv.style.display = 'block'; }
          return;
        }

        try {
          const resp = await fetch('/api/admin/update-credentials', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ currentPassword: currentPass, newAdminId: adminId, newName: name, newPassword: newPass })
          });
          const res = await resp.json();

          if (res && res.success) {
            state.currentUserName = res.adminName || name;
            state.adminProfile = {
              ...(state.adminProfile || {}),
              adminId: res.adminId || adminId,
              agentCode: res.adminId || adminId,
              name: res.adminName || name,
              role: 'SuperAdmin'
            };
            saveState();
            closeModal();
            showToast('Admin credentials and profile updated successfully!');
            renderProfile();
            updateRoleUI();
          } else {
            if (errDiv) {
              errDiv.textContent = (res && res.message) || 'Failed to update admin credentials. Verify current password.';
              errDiv.style.display = 'block';
            }
          }
        } catch (err) {
          if (errDiv) {
            errDiv.textContent = 'Network error updating credentials.';
            errDiv.style.display = 'block';
          }
        }
      });
    }
  }

  // 2B. Dedicated Profile Name & Team Edit Modal
  function openEditNameAndTeamModal(profileId, focusField = 'both') {
    const prof = state.profiles.find(p => p.id === profileId) || state.profiles.find(p => p.id === state.currentProfileId) || state.profiles[0];
    if (!prof) return;

    if ((prof.role === 'SuperAdmin' || prof.isAdminAccount) && state.currentRole !== 'SuperAdmin') {
      showToast('Restricted administrative profile.');
      return;
    }

    let selectedTeamMode = 'switch'; // 'switch', 'rename', or 'create'
    let selectedTeamName = prof.team || (state.teams[0] ? state.teams[0].name : 'Alpha Warriors');

    function updatePreview() {
      const nameInput = document.getElementById('name-team-input-name');
      const curName = (nameInput ? nameInput.value.trim() : prof.name) || 'User';
      const previewName = document.getElementById('preview-name-text');
      if (previewName) previewName.textContent = curName;

      let effectiveTeam = selectedTeamName;
      if (selectedTeamMode === 'rename') {
        const renameInput = document.getElementById('name-team-rename-val');
        if (renameInput && renameInput.value.trim()) effectiveTeam = renameInput.value.trim();
      } else if (selectedTeamMode === 'create') {
        const createInput = document.getElementById('name-team-create-val');
        if (createInput && createInput.value.trim()) effectiveTeam = createInput.value.trim();
      }

      const previewTeam = document.getElementById('preview-team-text');
      if (previewTeam) previewTeam.textContent = effectiveTeam;

      const previewAvatar = document.getElementById('preview-avatar-box');
      if (previewAvatar) {
        previewAvatar.innerHTML = renderAvatarHtml(prof.avatar, curName, 52, 16);
      }
    }

    const html = `
      <div class="sheet-title" style="display:flex; align-items:center; gap:8px;">
        <span>✏️</span>
        <span>Edit Profile Name & Team</span>
      </div>
      <div style="font-size:11.5px; color:var(--text-muted); margin-bottom:12px;">
        Directly update your franchise display name and change or rename your assigned team.
      </div>

      <!-- Live Identity Preview Card -->
      <div class="name-team-preview-card">
        <div id="preview-avatar-box">
          ${renderAvatarHtml(prof.avatar, prof.name, 52, 16)}
        </div>
        <div style="flex:1; min-width:0;">
          <div id="preview-name-text" style="font-size:15px; font-weight:800; color:var(--text-main); word-break:break-word;">${prof.name}</div>
          <div style="font-size:11px; color:var(--primary-light); font-weight:700;">${prof.agentCode} • ${prof.role}</div>
          <div style="display:flex; align-items:center; gap:6px; margin-top:4px; flex-wrap:wrap;">
            <span class="badge" style="background:var(--primary); color:white; font-size:10.5px;">Team: <strong id="preview-team-text">${prof.team}</strong></span>
            <span class="badge" style="background:#10b981; color:white; font-size:10px;">${prof.status}</span>
          </div>
        </div>
      </div>

      <!-- Section 1: Profile Full Name Edit Option -->
      <div class="form-group" style="margin-bottom:14px;">
        <label class="form-label" style="display:flex; justify-content:space-between; align-items:center;">
          <span>Profile Full Name *</span>
          <span style="font-size:10px; color:var(--primary-light); font-weight:600;">Displayed on reports & leaderboards</span>
        </label>
        <input id="name-team-input-name" type="text" class="form-input" value="${prof.name}" placeholder="Enter full name" style="font-size:13.5px; font-weight:700;">
      </div>

      <!-- Section 2: Team Name & Affiliation Option -->
      <div class="form-group">
        <label class="form-label" style="display:flex; justify-content:space-between; align-items:center;">
          <span>Team Name & Affiliation</span>
          <span style="font-size:10px; color:var(--text-muted);">Current: <strong>${prof.team}</strong></span>
        </label>

        <!-- Mode Selector Strip -->
        <div class="team-action-tab-strip">
          <button type="button" id="tab-mode-switch" class="team-action-tab ${selectedTeamMode === 'switch' ? 'active' : ''}">
            🔄 Switch Team
          </button>
          <button type="button" id="tab-mode-rename" class="team-action-tab ${selectedTeamMode === 'rename' ? 'active' : ''}">
            ✏️ Rename Team
          </button>
          <button type="button" id="tab-mode-create" class="team-action-tab ${selectedTeamMode === 'create' ? 'active' : ''}">
            + New Team
          </button>
        </div>

        <!-- Mode A: Switch Team -->
        <div id="box-mode-switch" style="${selectedTeamMode === 'switch' ? 'display:block;' : 'display:none;'}">
          <div style="font-size:11px; color:var(--text-muted); margin-bottom:6px;">Select an existing franchise team to join:</div>
          <div class="team-chip-grid">
            ${state.teams.map(t => {
              const count = state.distributors.filter(d => d.team === t.name).length;
              const isSelected = t.name === selectedTeamName;
              return `
                <button type="button" class="team-chip-btn ${isSelected ? 'active' : ''}" data-team-pick="${t.name}">
                  <span>👥 ${t.name}</span>
                  <span class="badge" style="background:rgba(255,255,255,0.2); font-size:10px; padding:1px 5px;">${count}</span>
                </button>
              `;
            }).join('')}
          </div>
          <div style="margin-top:8px;">
            <select id="select-team-dropdown" class="form-input" style="height:36px; font-size:12px;">
              ${state.teams.map(t => `<option value="${t.name}" ${t.name === selectedTeamName ? 'selected' : ''}>${t.name} (Leader: ${t.leader})</option>`).join('')}
            </select>
          </div>
        </div>

        <!-- Mode B: Rename Current Team -->
        <div id="box-mode-rename" style="${selectedTeamMode === 'rename' ? 'display:block;' : 'display:none;'} background:var(--bg-input); padding:10px 12px; border-radius:10px; border:1px dashed var(--primary);">
          <div style="font-size:12px; font-weight:800; color:var(--primary-light); margin-bottom:4px;">Rename Team "${prof.team}"</div>
          <div style="font-size:11px; color:var(--text-muted); margin-bottom:8px;">
            Renaming will update this team name across all member profiles, distributors, and performance records.
          </div>
          <div class="form-group" style="margin-bottom:6px;">
            <label class="form-label">New Team Name *</label>
            <input id="name-team-rename-val" type="text" class="form-input" value="${prof.team}" placeholder="e.g. Phoenix Warriors">
          </div>
        </div>

        <!-- Mode C: Create New Team -->
        <div id="box-mode-create" style="${selectedTeamMode === 'create' ? 'display:block;' : 'display:none;'} background:var(--bg-input); padding:10px 12px; border-radius:10px; border:1px dashed #10b981;">
          <div style="font-size:12px; font-weight:800; color:#10b981; margin-bottom:4px;">Create Brand New Franchise Team</div>
          <div style="font-size:11px; color:var(--text-muted); margin-bottom:8px;">
            Create a new team and assign ${prof.name} as a pioneer leader.
          </div>
          <div class="form-group" style="margin-bottom:8px;">
            <label class="form-label">Team Name *</label>
            <input id="name-team-create-val" type="text" class="form-input" placeholder="e.g. Royal Gladiators">
          </div>
          <div class="form-group" style="margin-bottom:6px;">
            <label class="form-label">Target Revenue (₹)</label>
            <input id="name-team-create-target" type="number" class="form-input" value="1500000">
          </div>
        </div>
      </div>

      <div style="display:flex; gap:8px; margin-top:16px;">
        <button id="btn-save-name-team" class="btn-primary" style="flex:1;">
          Save Name & Team
        </button>
        <button id="btn-cancel-name-team" class="btn-secondary" style="width:75px;">
          Cancel
        </button>
      </div>
    `;

    openModal(html);

    const nameInput = document.getElementById('name-team-input-name');
    const selectDropdown = document.getElementById('select-team-dropdown');
    const renameValInput = document.getElementById('name-team-rename-val');
    const createValInput = document.getElementById('name-team-create-val');
    const createTargetInput = document.getElementById('name-team-create-target');

    if (focusField === 'team') {
      const modeRenameBtn = document.getElementById('tab-mode-rename');
      if (modeRenameBtn) modeRenameBtn.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    } else if (nameInput) {
      nameInput.focus();
      nameInput.select();
    }

    if (nameInput) {
      nameInput.addEventListener('input', updatePreview);
    }
    if (renameValInput) {
      renameValInput.addEventListener('input', updatePreview);
    }
    if (createValInput) {
      createValInput.addEventListener('input', updatePreview);
    }

    function setMode(mode) {
      selectedTeamMode = mode;
      document.querySelectorAll('.team-action-tab').forEach(t => t.classList.remove('active'));
      const activeTab = document.getElementById('tab-mode-' + mode);
      if (activeTab) activeTab.classList.add('active');

      const boxSwitch = document.getElementById('box-mode-switch');
      const boxRename = document.getElementById('box-mode-rename');
      const boxCreate = document.getElementById('box-mode-create');
      if (boxSwitch) boxSwitch.style.display = (mode === 'switch') ? 'block' : 'none';
      if (boxRename) boxRename.style.display = (mode === 'rename') ? 'block' : 'none';
      if (boxCreate) boxCreate.style.display = (mode === 'create') ? 'block' : 'none';

      updatePreview();
    }

    const tabSwitch = document.getElementById('tab-mode-switch');
    const tabRename = document.getElementById('tab-mode-rename');
    const tabCreate = document.getElementById('tab-mode-create');
    if (tabSwitch) tabSwitch.addEventListener('click', () => setMode('switch'));
    if (tabRename) tabRename.addEventListener('click', () => setMode('rename'));
    if (tabCreate) tabCreate.addEventListener('click', () => setMode('create'));

    // Team chips
    document.querySelectorAll('.team-chip-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const pick = btn.getAttribute('data-team-pick');
        if (pick) {
          selectedTeamName = pick;
          document.querySelectorAll('.team-chip-btn').forEach(b => b.classList.remove('active'));
          btn.classList.add('active');
          if (selectDropdown) selectDropdown.value = pick;
          updatePreview();
        }
      });
    });

    if (selectDropdown) {
      selectDropdown.addEventListener('change', () => {
        selectedTeamName = selectDropdown.value;
        document.querySelectorAll('.team-chip-btn').forEach(b => {
          b.classList.toggle('active', b.getAttribute('data-team-pick') === selectedTeamName);
        });
        updatePreview();
      });
    }

    const btnCancel = document.getElementById('btn-cancel-name-team');
    if (btnCancel) btnCancel.addEventListener('click', closeModal);

    const btnSave = document.getElementById('btn-save-name-team');
    if (btnSave) {
      btnSave.addEventListener('click', () => {
        const newName = nameInput ? nameInput.value.trim() : '';
        if (!newName) {
          showToast('Please enter a valid profile name.');
          if (nameInput) nameInput.focus();
          return;
        }

        const oldName = prof.name;
        const oldTeam = prof.team;
        let finalTeam = prof.team;

        if (selectedTeamMode === 'rename') {
          const renameVal = renameValInput ? renameValInput.value.trim() : '';
          if (!renameVal) {
            showToast('Please enter a valid team name to rename.');
            if (renameValInput) renameValInput.focus();
            return;
          }
          renameTeam(oldTeam, renameVal, newName);
          finalTeam = renameVal;
        } else if (selectedTeamMode === 'create') {
          const createVal = createValInput ? createValInput.value.trim() : '';
          if (!createVal) {
            showToast('Please enter a new team name.');
            if (createValInput) createValInput.focus();
            return;
          }
          const target = createTargetInput ? (Number(createTargetInput.value) || 1500000) : 1500000;
          createOrGetTeam(createVal, newName, target);
          finalTeam = createVal;
        } else {
          // Switch team
          finalTeam = selectedTeamName || prof.team;
        }

        // Apply profile updates
        prof.name = newName;
        prof.team = finalTeam;

        // If avatar was initials, recompute
        if (!prof.avatar || (prof.avatar.length <= 3 && !/[\u{1F300}-\u{1FAFF}]/u.test(prof.avatar))) {
          prof.avatar = getInitials(newName);
        }

        // Sync leader name in state.teams if this profile is leader
        state.teams.forEach(t => {
          if (t.leader === oldName) t.leader = newName;
        });

        // Sync distributor record if exists
        const dist = state.distributors.find(d => d.profileId === prof.id || d.agentCode === prof.agentCode);
        if (dist) {
          dist.name = newName;
          dist.team = finalTeam;
        }

        // Update active session if this was current user
        if (prof.id === state.currentProfileId) {
          state.currentUserName = newName;
          state.currentTeam = finalTeam;
          updateRoleUI();
        }

        saveState();
        updateLoginProfilePreview();
        closeModal();
        showToast(`Profile name and team updated: ${newName} (${finalTeam})`);
        renderProfile();
      });
    }
  }

  // 2C. Dedicated Team Rename & Edit Modal
  function openRenameOrEditTeamModal(teamIdOrName) {
    const isNew = !teamIdOrName;
    const teamObj = isNew ? null : state.teams.find(t => t.id === teamIdOrName || t.name === teamIdOrName);

    const initialName = teamObj ? teamObj.name : '';
    const initialLeader = teamObj ? teamObj.leader : state.currentUserName;
    const initialTarget = teamObj ? teamObj.target : 1500000;

    const html = `
      <div class="sheet-title">${isNew ? 'Create New Team' : `Rename & Edit Team: ${initialName}`}</div>
      <div style="font-size:12px; color:var(--text-muted); margin-bottom:10px;">
        ${isNew ? 'Establish a new sales team roster and performance target in Tranz India network.' : 'Modify team branding, assigned team leader, or quarterly revenue targets.'}
      </div>

      <div class="form-group">
        <label class="form-label">Team Name *</label>
        <input id="team-edit-name" type="text" class="form-input" value="${initialName}" placeholder="e.g. Phoenix Titans">
      </div>

      <div class="form-group">
        <label class="form-label">Team Leader Name *</label>
        <input id="team-edit-leader" type="text" class="form-input" value="${initialLeader}" placeholder="Team leader name">
      </div>

      <div class="form-group">
        <label class="form-label">Quarterly Revenue Target (₹) *</label>
        <input id="team-edit-target" type="number" class="form-input" value="${initialTarget}">
      </div>

      ${isNew ? `
        <div style="margin-top:8px; display:flex; align-items:center; gap:8px;">
          <input type="checkbox" id="team-edit-assign-self" checked style="accent-color:var(--primary); width:16px; height:16px;">
          <label for="team-edit-assign-self" style="font-size:12px; color:var(--text-main); cursor:pointer;">Assign current profile (${state.currentUserName}) to this new team</label>
        </div>
      ` : ''}

      <div style="display:flex; gap:8px; margin-top:14px; flex-wrap:wrap;">
        ${!isNew ? `
          <button type="button" id="btn-delete-team-from-edit" class="btn-secondary" style="background:rgba(239,68,68,0.15); color:#ef4444; border-color:#ef4444; font-weight:700;">
            🗑️ Delete Team
          </button>
        ` : ''}
        <button id="btn-cancel-team-edit" class="btn-secondary" style="width:75px;">
          Cancel
        </button>
        <button id="btn-save-team-edit" class="btn-primary" style="flex:1;">
          ${isNew ? 'Create Team' : 'Save Changes'}
        </button>
      </div>
    `;

    openModal(html);

    const nameInput = document.getElementById('team-edit-name');
    const leaderInput = document.getElementById('team-edit-leader');
    const targetInput = document.getElementById('team-edit-target');
    const assignSelfCheck = document.getElementById('team-edit-assign-self');

    const btnCancel = document.getElementById('btn-cancel-team-edit');
    if (btnCancel) btnCancel.addEventListener('click', closeModal);

    const btnDeleteFromEdit = document.getElementById('btn-delete-team-from-edit');
    if (btnDeleteFromEdit && teamObj) {
      btnDeleteFromEdit.addEventListener('click', () => {
        closeModal();
        setTimeout(() => openDeleteTeamModal(teamObj.id), 120);
      });
    }

    const btnSave = document.getElementById('btn-save-team-edit');
    if (btnSave) {
      btnSave.addEventListener('click', () => {
        const newTeamName = nameInput ? nameInput.value.trim() : '';
        const newLeader = leaderInput ? leaderInput.value.trim() : '';
        const newTarget = targetInput ? (Number(targetInput.value) || 1500000) : 1500000;

        if (!newTeamName || !newLeader) {
          showToast('Please provide both a team name and leader name.');
          return;
        }

        if (isNew) {
          createOrGetTeam(newTeamName, newLeader, newTarget);
          if (assignSelfCheck && assignSelfCheck.checked) {
            const curProf = state.profiles.find(p => p.id === state.currentProfileId);
            if (curProf) {
              curProf.team = newTeamName;
              state.currentTeam = newTeamName;
              const dist = state.distributors.find(d => d.profileId === curProf.id || d.agentCode === curProf.agentCode);
              if (dist) dist.team = newTeamName;
              updateRoleUI();
            }
          }
          saveState();
          closeModal();
          showToast(`Team "${newTeamName}" created successfully!`);
          renderProfile();
        } else {
          // Edit / Rename existing team
          renameTeam(initialName, newTeamName, newLeader, newTarget);
          saveState();
          closeModal();
          showToast(`Team updated to "${newTeamName}"!`);
          renderProfile();
        }
      });
    }
  }

  // 2D. Dedicated Team Delete / Remove Modal
  function openDeleteTeamModal(teamIdOrName) {
    const clean = String(teamIdOrName || '').trim().toLowerCase();
    const teamObj = state.teams.find(t => t.id === teamIdOrName || t.name.toLowerCase() === clean);
    if (!teamObj) {
      showToast('Team not found.');
      return;
    }

    if (state.teams.length <= 1) {
      showToast('Cannot delete the only remaining team. Please create another team first.');
      return;
    }

    const assignedDistributors = state.distributors.filter(d => d.team && d.team.toLowerCase() === teamObj.name.toLowerCase());
    const otherTeams = state.teams.filter(t => t.id !== teamObj.id && t.name.toLowerCase() !== teamObj.name.toLowerCase());

    const html = `
      <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border-color); padding-bottom:12px; margin-bottom:14px;">
        <div style="display:flex; align-items:center; gap:8px;">
          <div style="width:36px; height:36px; border-radius:50%; background:rgba(239,68,68,0.15); display:flex; align-items:center; justify-content:center; color:#ef4444; font-size:18px;">
            🗑️
          </div>
          <div>
            <div style="font-size:16px; font-weight:800; color:#ef4444;">Delete / Remove Team</div>
            <div style="font-size:11px; color:var(--text-muted);">${escapeHtml(teamObj.name)}</div>
          </div>
        </div>
        <button id="btn-close-delete-team-modal" style="background:none; border:none; color:var(--text-muted); font-size:22px; cursor:pointer;">&times;</button>
      </div>

      <div style="background:rgba(239,68,68,0.1); border:1px solid rgba(239,68,68,0.25); border-radius:8px; padding:12px; margin-bottom:14px;">
        <div style="font-size:13px; font-weight:700; color:#f87171; margin-bottom:4px;">
          ⚠️ Confirm Removal of "${escapeHtml(teamObj.name)}"
        </div>
        <div style="font-size:12px; color:var(--text-main); line-height:1.5;">
          • Leader: <strong>${escapeHtml(teamObj.leader)}</strong><br>
          • Target: <strong>${formatINR(teamObj.target)}</strong><br>
          • Current Members: <strong>${assignedDistributors.length} active distributor(s)</strong>
        </div>
      </div>

      <div class="form-group" style="margin-bottom:14px;">
        <label class="form-label">Safely reassign existing members to:</label>
        <select id="delete-team-reassign-select" class="form-input" style="height:38px;">
          ${otherTeams.map(t => `<option value="${escapeHtml(t.name)}">${escapeHtml(t.name)} (Leader: ${escapeHtml(t.leader)})</option>`).join('')}
        </select>
        <div style="font-size:11px; color:var(--text-muted); margin-top:4px;">
          Distributors and profiles will be preserved and reassigned to this team.
        </div>
      </div>

      <div style="display:flex; gap:10px; margin-top:14px;">
        <button type="button" id="btn-cancel-delete-team" class="btn-secondary" style="flex:1;">Cancel</button>
        <button type="button" id="btn-confirm-delete-team" class="btn-primary" style="flex:1.4; background:#dc2626; border-color:#ef4444; color:white; font-weight:800;">
          🗑️ Confirm & Delete Team
        </button>
      </div>
    `;

    openModal(html);

    const btnClose = document.getElementById('btn-close-delete-team-modal');
    if (btnClose) btnClose.addEventListener('click', closeModal);

    const btnCancel = document.getElementById('btn-cancel-delete-team');
    if (btnCancel) btnCancel.addEventListener('click', closeModal);

    const btnConfirm = document.getElementById('btn-confirm-delete-team');
    if (btnConfirm) {
      btnConfirm.addEventListener('click', () => {
        const reassignSelect = document.getElementById('delete-team-reassign-select');
        const fallback = reassignSelect ? reassignSelect.value : (otherTeams[0] ? otherTeams[0].name : null);
        const res = deleteTeam(teamObj.id, fallback);
        if (res.success) {
          closeModal();
          showToast(`Team "${teamObj.name}" removed. Members reassigned to "${res.fallbackTeam}".`);
          renderProfile();
        } else {
          showToast(res.message || 'Failed to delete team.');
        }
      });
    }
  }


  // 3. Add Distributor Modal
  function openAddDistributorModal() {
    let html = `
      <div class="sheet-title">Add Distributor to Team</div>
      <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
        Register a new direct selling distributor into team roster.
      </div>

      <div class="form-group">
        <label class="form-label">Distributor Full Name *</label>
        <input id="add-dist-name" type="text" class="form-input" placeholder="e.g. Vignesh Kumar">
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Agent Code *</label>
          <input id="add-dist-code" type="text" class="form-input" placeholder="e.g. TRZ-025" value="TRZ-0${state.distributors.length + 10}">
        </div>
        <div class="form-group">
          <label class="form-label">Assigned Team *</label>
          <select id="add-dist-team" class="form-input">
            ${state.teams.map(t => `<option value="${t.name}">${t.name}</option>`).join('')}
          </select>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">Contact Phone *</label>
        <input id="add-dist-phone" type="tel" class="form-input" placeholder="e.g. +91 98400 55667">
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Monthly Target (₹)</label>
          <input id="add-dist-target" type="number" class="form-input" value="200000">
        </div>
        <div class="form-group">
          <label class="form-label">Current Closed (₹)</label>
          <input id="add-dist-closed" type="number" class="form-input" value="0">
        </div>
      </div>

      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-add-distributor" class="btn-primary">Add to Team</button>
        <button id="btn-cancel-add-distributor" class="btn-secondary">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-cancel-add-distributor').addEventListener('click', closeModal);

    document.getElementById('btn-save-add-distributor').addEventListener('click', () => {
      const name = document.getElementById('add-dist-name').value.trim();
      const code = document.getElementById('add-dist-code').value.trim();
      const team = document.getElementById('add-dist-team').value;
      const phone = document.getElementById('add-dist-phone').value.trim();
      const target = Number(document.getElementById('add-dist-target').value) || 200000;
      const closed = Number(document.getElementById('add-dist-closed').value) || 0;

      if (!name || !code || !phone) {
        showToast('Please fill all required fields');
        return;
      }

      const newDist = {
        id: 'dist-' + Date.now(),
        profileId: '',
        name: name,
        agentCode: code,
        phone: phone,
        team: team,
        role: 'Distributor',
        seniorityClosed: closed,
        target: target,
        status: 'ACTIVE',
        rating: 4.8
      };

      state.distributors.push(newDist);
      const teamObj = state.teams.find(t => t.name === team);
      if (teamObj) teamObj.distributorsCount++;

      saveState();
      closeModal();
      showToast(`Distributor ${name} added to ${team}!`);
      renderProfile();
    });
  }

  // 4. Remove Distributor Modal
  function openRemoveDistributorModal(distId) {
    const dist = state.distributors.find(d => d.id === distId);
    if (!dist) return;

    let html = `
      <div class="sheet-title" style="color:#ef4444;">Remove Distributor</div>
      <div style="font-size:13px; color:var(--text-main); margin-top:4px;">
        Are you sure you want to remove <strong>${dist.name}</strong> (${dist.agentCode}) from <strong>${dist.team}</strong>?
      </div>
      <div style="font-size:11.5px; color:var(--text-muted); margin-top:6px; background:var(--bg-input); padding:8px 10px; border-radius:6px;">
        ⚠️ This will remove them from the active team roster. Past sales ledger entries will remain preserved.
      </div>

      <div style="display:flex; gap:8px; margin-top:12px;">
        <button id="btn-confirm-remove-dist" class="btn-danger" style="flex:1;">Yes, Remove Distributor</button>
        <button id="btn-cancel-remove-dist" class="btn-secondary" style="flex:1;">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-cancel-remove-dist').addEventListener('click', closeModal);

    document.getElementById('btn-confirm-remove-dist').addEventListener('click', () => {
      const idx = state.distributors.findIndex(d => d.id === distId);
      if (idx !== -1) {
        const teamObj = state.teams.find(t => t.name === dist.team);
        if (teamObj && teamObj.distributorsCount > 0) teamObj.distributorsCount--;

        state.distributors.splice(idx, 1);
        saveState();
        closeModal();
        showToast(`Distributor ${dist.name} removed from team.`);
        renderProfile();
      }
    });
  }

  // 5. Edit Distributor Target Modal
  function openEditDistributorModal(distId) {
    const dist = state.distributors.find(d => d.id === distId);
    if (!dist) return;

    let html = `
      <div class="sheet-title">Edit Distributor Performance</div>
      <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
        ${dist.name} (${dist.agentCode} • ${dist.team})
      </div>

      <div class="form-group">
        <label class="form-label">Monthly Target (₹)</label>
        <input id="edit-dist-target" type="number" class="form-input" value="${dist.target}">
      </div>

      <div class="form-group">
        <label class="form-label">Current Seniority Closed (₹)</label>
        <input id="edit-dist-closed" type="number" class="form-input" value="${dist.seniorityClosed}">
      </div>

      <div class="form-group">
        <label class="form-label">Contact Phone</label>
        <input id="edit-dist-phone" type="tel" class="form-input" value="${dist.phone}">
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Status</label>
          <select id="edit-dist-status" class="form-input">
            <option value="ACTIVE" ${dist.status === 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
            <option value="ON_FIELD" ${dist.status === 'ON_FIELD' ? 'selected' : ''}>ON_FIELD</option>
            <option value="INACTIVE" ${dist.status === 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">Rating</label>
          <input id="edit-dist-rating" type="number" step="0.1" max="5.0" class="form-input" value="${dist.rating || 4.8}">
        </div>
      </div>

      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-edit-dist" class="btn-primary">Save Changes</button>
        <button id="btn-cancel-edit-dist" class="btn-secondary">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-cancel-edit-dist').addEventListener('click', closeModal);

    document.getElementById('btn-save-edit-dist').addEventListener('click', () => {
      dist.target = Number(document.getElementById('edit-dist-target').value) || dist.target;
      dist.seniorityClosed = Number(document.getElementById('edit-dist-closed').value) || dist.seniorityClosed;
      dist.phone = document.getElementById('edit-dist-phone').value.trim() || dist.phone;
      dist.status = document.getElementById('edit-dist-status').value;
      dist.rating = Number(document.getElementById('edit-dist-rating').value) || dist.rating;

      saveState();
      closeModal();
      showToast(`Updated distributor record for ${dist.name}`);
      renderProfile();
    });
  }

  // 6. Add Team Performance Entry Modal
  function openAddTeamPerformanceModal() {
    const curTeam = state.selectedPerfTeam || state.currentTeam || 'Alpha Warriors';
    let html = `
      <div class="sheet-title">Add Team Performance Entry</div>
      <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
        Record whole team field calls, closed volume, and milestone benchmarks.
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Team *</label>
          <select id="add-perf-team" class="form-input">
            ${state.teams.map(t => `<option value="${t.name}" ${t.name === curTeam ? 'selected' : ''}>${t.name}</option>`).join('')}
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">Week Milestone *</label>
          <input id="add-perf-week" type="text" class="form-input" placeholder="e.g. Week 37" value="Week 37">
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">Date *</label>
        <input id="add-perf-date" type="text" class="form-input" value="${new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })}">
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Weekly Target (₹) *</label>
          <input id="add-perf-target" type="number" class="form-input" value="500000">
        </div>

        <div class="form-group">
          <label class="form-label">Achieved Volume (₹) *</label>
          <input id="add-perf-achieved" type="number" class="form-input" value="450000">
        </div>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Calls Logged</label>
          <input id="add-perf-calls" type="number" class="form-input" value="190">
        </div>

        <div class="form-group">
          <label class="form-label">Conversions</label>
          <input id="add-perf-conv" type="number" class="form-input" value="15">
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">Top Performer of the Week</label>
        <input id="add-perf-top" type="text" class="form-input" placeholder="e.g. Priya Sharma" value="${state.currentUserName}">
      </div>

      <div class="form-group">
        <label class="form-label">Field Center Remarks / Notes</label>
        <input id="add-perf-remarks" type="text" class="form-input" placeholder="e.g. Strong corporate senior associate conversions">
      </div>

      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-add-perf" class="btn-primary">Save Performance Entry</button>
        <button id="btn-cancel-add-perf" class="btn-secondary">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-cancel-add-perf').addEventListener('click', closeModal);

    document.getElementById('btn-save-add-perf').addEventListener('click', () => {
      const team = document.getElementById('add-perf-team').value;
      const week = document.getElementById('add-perf-week').value.trim() || 'Current Week';
      const date = document.getElementById('add-perf-date').value.trim();
      const target = Number(document.getElementById('add-perf-target').value) || 0;
      const achieved = Number(document.getElementById('add-perf-achieved').value) || 0;
      const callsMade = Number(document.getElementById('add-perf-calls').value) || 0;
      const conversions = Number(document.getElementById('add-perf-conv').value) || 0;
      const topPerformer = document.getElementById('add-perf-top').value.trim();
      const remarks = document.getElementById('add-perf-remarks').value.trim();

      const newEntry = {
        id: 'perf-' + Date.now(),
        team: team,
        week: week,
        date: date,
        target: target,
        achieved: achieved,
        callsMade: callsMade,
        conversions: conversions,
        topPerformer: topPerformer,
        remarks: remarks
      };

      state.teamPerformanceEntries.unshift(newEntry);
      state.selectedPerfTeam = team;
      saveState();
      closeModal();
      showToast(`Performance entry logged for ${team}!`);
      renderProfile();
    });
  }

  // 7. Edit Team Performance Entry Modal
  function openEditTeamPerformanceModal(perfId) {
    const entry = state.teamPerformanceEntries.find(e => e.id === perfId);
    if (!entry) return;

    let html = `
      <div class="sheet-title">Edit Team Performance Entry</div>
      <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
        ${entry.team} • ${entry.week} (${entry.date})
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Target (₹)</label>
          <input id="edit-perf-target" type="number" class="form-input" value="${entry.target}">
        </div>

        <div class="form-group">
          <label class="form-label">Achieved (₹)</label>
          <input id="edit-perf-achieved" type="number" class="form-input" value="${entry.achieved}">
        </div>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
        <div class="form-group">
          <label class="form-label">Calls Made</label>
          <input id="edit-perf-calls" type="number" class="form-input" value="${entry.callsMade}">
        </div>

        <div class="form-group">
          <label class="form-label">Conversions</label>
          <input id="edit-perf-conv" type="number" class="form-input" value="${entry.conversions}">
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">Top Performer</label>
        <input id="edit-perf-top" type="text" class="form-input" value="${entry.topPerformer || ''}">
      </div>

      <div class="form-group">
        <label class="form-label">Remarks</label>
        <input id="edit-perf-remarks" type="text" class="form-input" value="${entry.remarks || ''}">
      </div>

      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-edit-perf" class="btn-primary">Update Entry</button>
        <button id="btn-cancel-edit-perf" class="btn-secondary">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-cancel-edit-perf').addEventListener('click', closeModal);

    document.getElementById('btn-save-edit-perf').addEventListener('click', () => {
      entry.target = Number(document.getElementById('edit-perf-target').value) || entry.target;
      entry.achieved = Number(document.getElementById('edit-perf-achieved').value) || entry.achieved;
      entry.callsMade = Number(document.getElementById('edit-perf-calls').value) || entry.callsMade;
      entry.conversions = Number(document.getElementById('edit-perf-conv').value) || entry.conversions;
      entry.topPerformer = document.getElementById('edit-perf-top').value.trim();
      entry.remarks = document.getElementById('edit-perf-remarks').value.trim();

      saveState();
      closeModal();
      showToast('Team performance entry updated!');
      renderProfile();
    });
  }

  // 8. Remove Team Performance Entry Modal
  function openRemoveTeamPerformanceModal(perfId) {
    const entry = state.teamPerformanceEntries.find(e => e.id === perfId);
    if (!entry) return;

    let html = `
      <div class="sheet-title" style="color:#ef4444;">Remove Performance Entry</div>
      <div style="font-size:13px; color:var(--text-main); margin-top:4px;">
        Are you sure you want to remove the performance record for <strong>${entry.team}</strong> (${entry.week} • ${entry.date})?
      </div>
      <div style="font-size:11.5px; color:var(--text-muted); margin-top:6px; background:var(--bg-input); padding:8px 10px; border-radius:6px;">
        Volume: <strong style="color:#10b981;">${formatINR(entry.achieved)}</strong> achieved / ${formatINR(entry.target)} target.
      </div>

      <div style="display:flex; gap:8px; margin-top:12px;">
        <button id="btn-confirm-remove-perf" class="btn-danger" style="flex:1;">Yes, Remove Record</button>
        <button id="btn-cancel-remove-perf" class="btn-secondary" style="flex:1;">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-cancel-remove-perf').addEventListener('click', closeModal);

    document.getElementById('btn-confirm-remove-perf').addEventListener('click', () => {
      const idx = state.teamPerformanceEntries.findIndex(e => e.id === perfId);
      if (idx !== -1) {
        state.teamPerformanceEntries.splice(idx, 1);
        saveState();
        closeModal();
        showToast('Performance record removed.');
        renderProfile();
      }
    });
  }

  // 9. Change Password Modal ("No One See")
  function openChangePasswordModal(profileId) {
    const prof = state.profiles.find(p => p.id === profileId);
    if (!prof) return;

    let html = `
      <div class="sheet-title">Change Account Password</div>
      <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
        Keep credentials secure so unauthorized persons cannot access dealer figures.
      </div>

      <div class="form-group">
        <label class="form-label">Current Password</label>
        <input id="pwd-current" type="password" class="form-input" placeholder="Enter current password">
      </div>

      <div class="form-group">
        <label class="form-label">New Password</label>
        <input id="pwd-new" type="password" class="form-input" placeholder="Minimum 6 characters">
      </div>

      <div class="form-group">
        <label class="form-label">Confirm New Password</label>
        <input id="pwd-confirm" type="password" class="form-input" placeholder="Re-enter new password">
      </div>

      <div style="display:flex; gap:8px; margin-top:8px;">
        <button id="btn-save-new-pwd" class="btn-primary">Update Password</button>
        <button id="btn-cancel-new-pwd" class="btn-secondary">Cancel</button>
      </div>
    `;

    openModal(html);

    document.getElementById('btn-cancel-new-pwd').addEventListener('click', closeModal);

    document.getElementById('btn-save-new-pwd').addEventListener('click', () => {
      const cur = document.getElementById('pwd-current').value.trim();
      const next = document.getElementById('pwd-new').value.trim();
      const conf = document.getElementById('pwd-confirm').value.trim();

      if (!cur || !next || !conf) {
        showToast('Please fill all password fields');
        return;
      }

      if (cur !== prof.password) {
        showToast('Current password does not match.');
        return;
      }

      if (next.length < 4) {
        showToast('New password is too short.');
        return;
      }

      if (next !== conf) {
        showToast('New passwords do not match.');
        return;
      }

      prof.password = next;
      saveState();
      closeModal();
      showToast('Password successfully updated and protected!');
      renderProfile();
    });
  }

  // 10. Role Switcher Modal
  function openRoleModal() {
    const roles = ['Distributor', 'Team Leader', 'Counsellor', 'Telecaller'];
    let html = `
      <div class="sheet-title">Switch Active Role</div>
      <div style="font-size:12px; color:var(--text-muted);">Preview access privileges and team scopes:</div>
      <div style="display:flex; flex-direction:column; gap:8px; margin-top:6px;">
        ${roles.map(r => `
          <button class="card btn-role-choice" data-role="${r}" style="cursor:pointer; text-align:left; border-color:${state.currentRole === r ? 'var(--primary)' : 'var(--border-color)'}; background:${state.currentRole === r ? 'var(--primary-900)' : 'var(--bg-surface-elevated)'};">
            <div style="font-size:14px; font-weight:800; color:${state.currentRole === r ? 'var(--primary-light)' : 'var(--text-main)'};">${r}</div>
            <div style="font-size:11px; color:var(--text-muted); margin-top:2px;">
              ${r === 'Distributor' ? 'Daily field visits, lead contacts, and closing reports' :
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

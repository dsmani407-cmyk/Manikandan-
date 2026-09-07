const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const HOST = '0.0.0.0';
const PUBLIC_DIR = path.join(__dirname, 'public');

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.webmanifest': 'application/manifest+json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.webp': 'image/webp',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf'
};

// Secure server-side admin configuration (stored outside public directory)
const ADMIN_CONFIG_FILE = path.join(__dirname, 'admin_config.json');

function getAdminConfig() {
  try {
    if (fs.existsSync(ADMIN_CONFIG_FILE)) {
      const data = JSON.parse(fs.readFileSync(ADMIN_CONFIG_FILE, 'utf8'));
      return data;
    }
  } catch (e) {
    console.error('Error reading admin config:', e);
  }
  // Default server-side configuration
  const defaultData = {
    adminId: 'admin',
    name: 'Command SuperAdmin',
    password: process.env.ADMIN_PASSWORD || 'adminsecret',
    role: 'SuperAdmin',
    team: 'HQ Executive',
    phone: '+91 94440 00000',
    email: 'd.s.mani407@gmail.com'
  };
  try {
    fs.writeFileSync(ADMIN_CONFIG_FILE, JSON.stringify(defaultData, null, 2), 'utf8');
  } catch (e) {}
  return defaultData;
}

function saveAdminConfig(cfg) {
  fs.writeFileSync(ADMIN_CONFIG_FILE, JSON.stringify(cfg, null, 2), 'utf8');
}

function parseJsonBody(req) {
  return new Promise((resolve) => {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        resolve(JSON.parse(body || '{}'));
      } catch (e) {
        resolve({});
      }
    });
  });
}

const server = http.createServer(async (req, res) => {
  const parsedUrl = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  let pathname = parsedUrl.pathname;

  // Handle health check
  if (pathname === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'ok', app: 'Smart Group PWA', time: new Date().toISOString() }));
    return;
  }

  // Backend Admin Authentication API (Credentials never exposed to frontend code)
  if (pathname === '/api/auth/admin-login' && req.method === 'POST') {
    const body = await parseJsonBody(req);
    const enteredId = (body.adminId || '').trim();
    const enteredPass = (body.password || '').trim();
    const adminConfig = getAdminConfig();

    if (
      (enteredId.toLowerCase() === adminConfig.adminId.toLowerCase() ||
       enteredId.toLowerCase() === (adminConfig.email || '').toLowerCase()) &&
      enteredPass === adminConfig.password
    ) {
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({
        success: true,
        profile: {
          id: 'prof-admin',
          adminId: adminConfig.adminId,
          name: adminConfig.name,
          role: 'SuperAdmin',
          team: adminConfig.team || 'HQ Executive',
          email: adminConfig.email,
          phone: adminConfig.phone
        }
      }));
      return;
    } else {
      res.writeHead(401, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: false, message: 'Invalid administrative credentials.' }));
      return;
    }
  }

  // Backend Admin Update Credentials API
  if (pathname === '/api/admin/update-credentials' && req.method === 'POST') {
    const body = await parseJsonBody(req);
    const { currentPassword, newAdminId, newName, newPassword } = body;
    const adminConfig = getAdminConfig();

    if (!currentPassword || currentPassword !== adminConfig.password) {
      res.writeHead(403, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: false, message: 'Current password verification failed.' }));
      return;
    }

    if (newAdminId && newAdminId.trim()) adminConfig.adminId = newAdminId.trim();
    if (newName && newName.trim()) adminConfig.name = newName.trim();
    if (newPassword && newPassword.trim()) adminConfig.password = newPassword.trim();

    saveAdminConfig(adminConfig);

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      success: true,
      message: 'Admin credentials updated successfully.',
      profile: {
        id: 'prof-admin',
        adminId: adminConfig.adminId,
        name: adminConfig.name,
        role: 'SuperAdmin',
        team: adminConfig.team,
        email: adminConfig.email
      }
    }));
    return;
  }

  // Safe path resolution
  if (pathname === '/') {
    pathname = '/index.html';
  }

  let filePath = path.join(PUBLIC_DIR, pathname);

  // Security: prevent path traversal
  if (!filePath.startsWith(PUBLIC_DIR)) {
    res.writeHead(403, { 'Content-Type': 'text/plain' });
    res.end('Forbidden');
    return;
  }

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      // SPA Fallback: serve index.html
      filePath = path.join(PUBLIC_DIR, 'index.html');
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    // Headers
    const headers = {
      'Content-Type': contentType,
      'Access-Control-Allow-Origin': '*'
    };

    // Prevent caching of service worker and manifest for instant PWA updates
    if (pathname === '/sw.js' || pathname === '/manifest.json') {
      headers['Cache-Control'] = 'no-cache, no-store, must-revalidate';
      headers['Service-Worker-Allowed'] = '/';
    } else {
      headers['Cache-Control'] = 'public, max-age=3600';
    }

    res.writeHead(200, headers);
    const readStream = fs.createReadStream(filePath);
    readStream.pipe(res);
  });
});

server.listen(PORT, HOST, () => {
  console.log(`[Smart Group PWA] Server listening on http://${HOST}:${PORT}`);
});

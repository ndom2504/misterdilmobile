const { verifyToken } = require('./auth');

function withAuth(handler) {
  return async (req, res) => {
    if (req.method === 'OPTIONS') {
      return res.status(200).end();
    }

    const authHeader = req.headers['authorization'];
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ error: 'Unauthorized' });
    }

    const token = authHeader.split(' ')[1];
    const decoded = verifyToken(token);
    if (!decoded) {
      return res.status(401).json({ error: 'Invalid or expired token' });
    }

    req.user = decoded;
    return handler(req, res);
  };
}

module.exports = { withAuth };

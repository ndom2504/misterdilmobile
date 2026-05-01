const { verifyToken } = require('./auth');
const { sql } = require('./db');

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

    // Pass JWT claims to PostgreSQL for RLS policies
    try {
      await sql`SET LOCAL request.jwt.claims = ${JSON.stringify(decoded)}`;
      await sql`SET LOCAL request.jwt.user_id = ${decoded.userId}`;
    } catch (err) {
      console.error('Failed to set JWT claims for RLS:', err);
      // Continue anyway - middleware guards will still protect
    }

    return handler(req, res);
  };
}

module.exports = { withAuth };

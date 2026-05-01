const { sql } = require('../../../lib/db');
const { withAuth } = require('../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  try {
    const dossiers = await sql`
      SELECT id, client_name, type, status, progress, last_update
      FROM dossiers
      WHERE user_id = ${req.user.userId}
      ORDER BY created_at DESC
    `;
    return res.status(200).json(dossiers);
  } catch (err) {
    console.error('Get dossiers error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
});

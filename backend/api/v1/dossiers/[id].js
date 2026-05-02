const { sql } = require('../../../lib/db');
const { withAuth } = require('../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  const { id } = req.query;

  try {
    const dossiers = await sql`
      SELECT d.id, d.client_name, d.type, d.status, d.progress, d.last_update, d.user_id, u.avatar_url
      FROM dossiers d
      LEFT JOIN users u ON u.id = d.user_id
      WHERE d.id = ${id} AND d.user_id = ${req.user.userId}
      LIMIT 1
    `;

    if (!dossiers[0]) return res.status(404).json({ error: 'Dossier introuvable' });
    return res.status(200).json(dossiers[0]);
  } catch (err) {
    console.error('Get dossier error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
});

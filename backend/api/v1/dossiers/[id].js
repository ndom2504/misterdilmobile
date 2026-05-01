const { sql } = require('../../../lib/db');
const { withAuth } = require('../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  const { id } = req.query;

  try {
    const dossiers = await sql`
      SELECT id, client_name, type, status, progress, last_update
      FROM dossiers
      WHERE id = ${id} AND user_id = ${req.user.userId}
      LIMIT 1
    `;

    if (!dossiers[0]) return res.status(404).json({ error: 'Dossier introuvable' });
    return res.status(200).json(dossiers[0]);
  } catch (err) {
    console.error('Get dossier error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
});

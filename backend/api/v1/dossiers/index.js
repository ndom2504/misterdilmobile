const { sql } = require('../../../lib/db');
const { withAuth } = require('../../../lib/middleware');
const { guardAdmin } = require('../../../lib/guards');

module.exports = withAuth(async (req, res) => {
  if (req.method === 'GET') {
    try {
      // Admins: voir tous les dossiers
      // Clients: voir seulement leurs dossiers
      const dossiers = req.user.role === 'admin'
        ? await sql`
            SELECT id, client_name, type, status, progress, last_update, user_id
            FROM dossiers
            ORDER BY created_at DESC
          `
        : await sql`
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
  }

  if (req.method === 'POST') {
    const { type } = req.body || {};
    if (!type) return res.status(400).json({ error: 'Le type de dossier est requis' });

    try {
      const users = await sql`SELECT name FROM users WHERE id = ${req.user.userId}`;
      const clientName = users[0]?.name ?? 'Utilisateur';
      const lastUpdate = new Date().toLocaleDateString('fr-CA', {
        day: '2-digit', month: 'short', year: 'numeric'
      });

      const result = await sql`
        INSERT INTO dossiers (client_name, type, status, progress, last_update, user_id)
        VALUES (${clientName}, ${type}, 'En attente', 0.0, ${lastUpdate}, ${req.user.userId})
        RETURNING id, client_name, type, status, progress, last_update
      `;
      return res.status(201).json(result[0]);
    } catch (err) {
      console.error('Create dossier error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  return res.status(405).json({ error: 'Method not allowed' });
});

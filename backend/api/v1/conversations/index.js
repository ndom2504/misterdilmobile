const { sql } = require('../../../lib/db');
const { withAuth } = require('../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method === 'GET') {
    try {
      const isAdmin = req.user.role === 'admin';

      const conversations = isAdmin
        ? await sql`
            SELECT
              c.id,
              u.name AS client_name,
              u.avatar_url AS avatar_url,
              c.project_name,
              c.last_message,
              c.time,
              c.unread_count
            FROM conversations c
            JOIN users u ON u.id = c.user_id
            WHERE c.admin_id = ${req.user.userId}
            ORDER BY c.created_at DESC`
        : await sql`
            SELECT
              c.id,
              u.name AS client_name,
              u.avatar_url AS avatar_url,
              c.project_name,
              c.last_message,
              c.time,
              c.unread_count
            FROM conversations c
            JOIN users u ON u.id = c.admin_id
            WHERE c.user_id = ${req.user.userId}
            ORDER BY c.created_at DESC`;

      return res.status(200).json(conversations);
    } catch (err) {
      console.error('Get conversations error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  if (req.method === 'POST') {
    const { admin_id, admin_name, project_name, dossier_id } = req.body || {};
    if (!admin_id || !project_name) {
      return res.status(400).json({ error: 'admin_id et project_name requis' });
    }
    try {
      const result = await sql`
        INSERT INTO conversations (client_name, project_name, last_message, time, unread_count, user_id, admin_id, dossier_id)
        VALUES (
          ${admin_name ?? 'Conseiller'},
          ${project_name},
          'Dossier soumis',
          to_char(NOW(), 'HH24:MI'),
          1,
          ${req.user.userId},
          ${admin_id},
          ${dossier_id || null}
        )
        RETURNING id, client_name, project_name, last_message, time, unread_count
      `;

      const autoText = '📁 Dossier soumis : ' + project_name + '\n\nBonjour, je viens de soumettre mon dossier d\'immigration. Merci de me contacter pour les prochaines étapes.';
      await sql`
        INSERT INTO messages (id, conversation_id, sender_id, text, timestamp, is_from_me)
        VALUES (
          ${Date.now().toString()},
          ${result[0].id},
          ${req.user.userId},
          ${autoText},
          ${Date.now()},
          true
        )
      `;

      if (dossier_id) {
        // Correction de la progression : 0.2 (20%) au lieu de 20.0 (2000%)
        await sql`
          UPDATE dossiers SET status = 'Soumis', progress = 0.2, last_update = to_char(NOW(), 'DD/MM/YYYY')
          WHERE id = ${dossier_id}
        `;
      }

      return res.status(201).json(result[0]);
    } catch (err) {
      console.error('Create conversation error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  return res.status(405).json({ error: 'Method not allowed' });
});

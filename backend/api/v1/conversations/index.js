const { sql } = require('../../../lib/db');
const { withAuth } = require('../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  try {
    const conversations = await sql`
      SELECT id, client_name, project_name, last_message, time, unread_count
      FROM conversations
      WHERE user_id = ${req.user.userId}
      ORDER BY created_at DESC
    `;
    return res.status(200).json(conversations);
  } catch (err) {
    console.error('Get conversations error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
});

const { sql } = require('../../lib/db');
const { withAuth } = require('../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { name, avatar_url } = req.body || {};

  try {
    const result = await sql`
      UPDATE users
      SET 
        name = COALESCE(${name}, name),
        avatar_url = COALESCE(${avatar_url}, avatar_url)
      WHERE id = ${req.user.userId}
      RETURNING id, name, email, avatar_url, role
    `;

    if (result.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    return res.status(200).json(result[0]);
  } catch (err) {
    console.error('Update profile error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
});

const { sql } = require('../../lib/db');
const { withAuth } = require('../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'POST' && req.method !== 'PUT') return res.status(405).json({ error: 'Method not allowed' });

  const { name, phone, language, avatar_url } = req.body || {};

  try {
    const result = await sql`
      UPDATE users
      SET 
        name = CASE WHEN ${name} IS NOT NULL THEN ${name} ELSE name END,
        avatar_url = CASE WHEN ${avatar_url} IS NOT NULL THEN ${avatar_url} ELSE avatar_url END
      WHERE id = ${req.user.userId}
      RETURNING id, name, email, avatar_url, role
    `;

    if (result.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    const user = result[0];
    return res.status(200).json({
      token: req.headers.authorization?.replace('Bearer ', '') || '',
      user_id: user.id,
      email: user.email,
      name: user.name,
      role: user.role,
      avatar_url: user.avatar_url
    });
  } catch (err) {
    console.error('Update profile error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
});

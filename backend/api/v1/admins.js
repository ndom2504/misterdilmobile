const { sql } = require('../../lib/db');

module.exports = async (req, res) => {
  if (req.method !== 'GET') return res.status(405).json({ error: 'Method not allowed' });

  try {
    let admins;
    try {
      admins = await sql`
        SELECT id, name, email, avatar_url FROM users WHERE role = 'admin' ORDER BY id ASC
      `;
    } catch {
      admins = await sql`
        SELECT id, name, email FROM users WHERE role = 'admin' ORDER BY id ASC
      `;
    }
    return res.status(200).json(admins);
  } catch (err) {
    console.error('Get admins error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
};

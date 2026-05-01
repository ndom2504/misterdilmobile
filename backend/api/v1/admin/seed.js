const bcrypt = require('bcryptjs');
const { sql } = require('../../../lib/db');

module.exports = async (req, res) => {
  if (req.method !== 'POST') return res.status(405).json({ error: 'POST only' });

  const { secret } = req.body || {};
  if (secret !== process.env.SEED_SECRET) {
    return res.status(403).json({ error: 'Forbidden' });
  }

  try {
    const hash = await bcrypt.hash('Mobilier241.@!', 10);

    const result = await sql`
      INSERT INTO users (name, email, password_hash, role)
      VALUES
        ('Admin 1', 'info@misterdil.ca', ${hash}, 'admin'),
        ('Admin 2', 'divinegismille@gmail.com', ${hash}, 'admin')
      ON CONFLICT (email)
      DO UPDATE SET
        role          = 'admin',
        password_hash = EXCLUDED.password_hash,
        name          = EXCLUDED.name
      RETURNING id, name, email, role
    `;

    return res.status(200).json({ message: 'Admins créés', admins: result });
  } catch (err) {
    console.error('Seed error:', err);
    return res.status(500).json({ error: err.message });
  }
};

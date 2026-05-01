const bcrypt = require('bcryptjs');
const { sql } = require('../../../lib/db');
const { generateToken } = require('../../../lib/auth');

module.exports = async (req, res) => {
  if (req.method === 'OPTIONS') return res.status(200).end();
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { name, email, password, role = 'user' } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({ error: 'Nom, email et mot de passe requis' });
  }
  if (password.length < 8) {
    return res.status(400).json({ error: 'Mot de passe trop court (min. 8 caractères)' });
  }
  if (!['user', 'admin'].includes(role)) {
    return res.status(400).json({ error: 'Rôle invalide' });
  }

  try {
    const existing = await sql`SELECT id FROM users WHERE email = ${email.toLowerCase()} LIMIT 1`;
    if (existing[0]) {
      return res.status(409).json({ error: 'Un compte existe déjà avec cet email' });
    }

    const passwordHash = await bcrypt.hash(password, 10);

    const result = await sql`
      INSERT INTO users (email, password_hash, name, role)
      VALUES (${email.toLowerCase()}, ${passwordHash}, ${name}, ${role})
      RETURNING id, email, name, role
    `;

    const user = result[0];
    const token = generateToken({ userId: user.id, email: user.email, role: user.role });

    return res.status(201).json({
      token,
      user_id: user.id,
      email: user.email,
      name: user.name,
      role: user.role,
    });
  } catch (err) {
    console.error('Register error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
};

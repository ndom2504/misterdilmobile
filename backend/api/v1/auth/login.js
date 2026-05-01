const bcrypt = require('bcryptjs');
const { sql } = require('../../../lib/db');
const { generateToken } = require('../../../lib/auth');

module.exports = async (req, res) => {
  if (req.method === 'OPTIONS') return res.status(200).end();
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email et mot de passe requis' });
  }

  try {
    const users = await sql`
      SELECT id, email, password_hash, name, role FROM users
      WHERE email = ${email.toLowerCase()}
      LIMIT 1
    `;

    const user = users[0];
    if (!user || !(await bcrypt.compare(password, user.password_hash))) {
      return res.status(401).json({ error: 'Identifiants invalides' });
    }

    const token = generateToken({ userId: user.id, email: user.email, role: user.role });

    return res.status(200).json({
      token,
      user_id: user.id,
      email: user.email,
      name: user.name,
      role: user.role,
    });
  } catch (err) {
    console.error('Login error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
};

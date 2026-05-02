const { OAuth2Client } = require('google-auth-library');
const { sql } = require('../../../lib/db');
const { generateToken } = require('../../../lib/auth');

const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

module.exports = async (req, res) => {
  if (req.method === 'OPTIONS') return res.status(200).end();
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { idToken } = req.body;
  if (!idToken) return res.status(400).json({ error: 'Token Google requis' });

  try {
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    const { email, name, sub: googleId } = payload;

    let users = await sql`SELECT id, email, name, role, avatar_url FROM users WHERE email = ${email.toLowerCase()} LIMIT 1`;
    let user = users[0];

    if (!user) {
      const result = await sql`
        INSERT INTO users (email, password_hash, name, role)
        VALUES (${email.toLowerCase()}, ${`google_${googleId}`}, ${name}, 'user')
        RETURNING id, email, name, role, avatar_url
      `;
      user = result[0];
    }

    const token = generateToken({ userId: user.id, email: user.email, role: user.role });

    return res.status(200).json({
      token,
      user_id: user.id,
      email: user.email,
      name: user.name,
      role: user.role,
      avatar_url: user.avatar_url
    });
  } catch (err) {
    console.error('Google auth error:', err);
    return res.status(401).json({ error: 'Token Google invalide' });
  }
};

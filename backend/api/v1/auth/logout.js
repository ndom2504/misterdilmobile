const { withAuth } = require('../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });
  return res.status(200).json({ message: 'Déconnexion réussie' });
});

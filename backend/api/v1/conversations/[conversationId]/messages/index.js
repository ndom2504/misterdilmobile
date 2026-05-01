const { sql } = require('../../../../../lib/db');
const { withAuth } = require('../../../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  const { conversationId } = req.query;

  if (req.method === 'GET') {
    try {
      const messages = await sql`
        SELECT id, conversation_id, sender_id, text, timestamp, is_from_me
        FROM messages
        WHERE conversation_id = ${conversationId}
        ORDER BY timestamp ASC
      `;
      return res.status(200).json(messages);
    } catch (err) {
      console.error('Get messages error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  if (req.method === 'POST') {
    const { id, sender_id, text, timestamp, is_from_me } = req.body;
    if (!id || !sender_id || !text || !timestamp) {
      return res.status(400).json({ error: 'Champs requis manquants' });
    }

    try {
      const result = await sql`
        INSERT INTO messages (id, conversation_id, sender_id, text, timestamp, is_from_me)
        VALUES (${id}, ${conversationId}, ${sender_id}, ${text}, ${timestamp}, ${is_from_me ?? false})
        RETURNING id, conversation_id, sender_id, text, timestamp, is_from_me
      `;

      await sql`
        UPDATE conversations
        SET last_message = ${text}, time = to_char(NOW(), 'HH24:MI'), unread_count = 0
        WHERE id = ${conversationId}
      `;

      return res.status(201).json(result[0]);
    } catch (err) {
      console.error('Send message error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  return res.status(405).json({ error: 'Method not allowed' });
});

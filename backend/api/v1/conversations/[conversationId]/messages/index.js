const { sql } = require('../../../../../lib/db');
const { withAuth } = require('../../../../../lib/middleware');
const { guardConversationMember } = require('../../../../../lib/guards');

module.exports = withAuth(async (req, res) => {
  const { conversationId } = req.params;

  // Fetch conversation to check membership
  const conv = await sql`
    SELECT user_id, admin_id FROM conversations WHERE id = ${conversationId}
  `;
  if (conv.length === 0) {
    return res.status(404).json({ error: 'Conversation non trouvée' });
  }
  req.conversation = conv[0];

  // Apply guard - check if user is member of conversation
  if (req.user.role !== 'admin' && 
      req.conversation.user_id !== req.user.userId && 
      req.conversation.admin_id !== req.user.userId) {
    return res.status(403).json({ error: 'Accès refusé: pas membre de la conversation' });
  }

  if (req.method === 'GET') {
    try {
      const messages = await sql`
        SELECT id, sender_id, text, timestamp, is_from_me
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
    const { text } = req.body || {};
    if (!text) return res.status(400).json({ error: 'Texte du message requis' });

    try {
      const newMessage = await sql`
        INSERT INTO messages (id, conversation_id, sender_id, text, timestamp, is_from_me)
        VALUES (
          ${Date.now().toString()},
          ${conversationId},
          ${req.user.userId},
          ${text},
          ${Date.now()},
          true
        )
        RETURNING id, sender_id, text, timestamp, is_from_me
      `;

      // Update conversation last message and time
      await sql`
        UPDATE conversations
        SET last_message = ${text}, time = to_char(NOW(), 'HH24:MI'), unread_count = unread_count + 1
        WHERE id = ${conversationId}
      `;

      return res.status(201).json(newMessage);
    } catch (err) {
      console.error('Send message error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  return res.status(405).json({ error: 'Method not allowed' });
});

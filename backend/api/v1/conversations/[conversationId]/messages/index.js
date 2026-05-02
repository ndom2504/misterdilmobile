const { sql } = require('../../../../../lib/db');
const { withAuth } = require('../../../../../lib/middleware');

module.exports = withAuth(async (req, res) => {
  // Correction: utiliser req.query pour les paramètres de route Vercel
  const { conversationId } = req.query;

  if (!conversationId) return res.status(400).json({ error: 'ID de conversation manquant' });

  // Récupérer la conversation pour vérifier l'accès
  const conv = await sql`
    SELECT id, user_id, admin_id, dossier_id FROM conversations WHERE id = ${conversationId}
  `;

  if (conv.length === 0) {
    return res.status(404).json({ error: 'Conversation non trouvée' });
  }
  const conversation = conv[0];

  if (req.user.role !== 'admin' &&
      conversation.user_id !== req.user.userId &&
      conversation.admin_id !== req.user.userId) {
    return res.status(403).json({ error: 'Accès refusé' });
  }

  if (req.method === 'GET') {
    try {
      // Jointure pour récupérer l'avatar de l'expéditeur
      const messages = await sql`
        SELECT
          m.id,
          m.sender_id,
          m.text,
          m.timestamp,
          (m.sender_id = ${req.user.userId}) as is_from_me,
          u.avatar_url as sender_avatar
        FROM messages m
        LEFT JOIN users u ON u.id::text = m.sender_id
        WHERE m.conversation_id = ${conversationId}
        ORDER BY m.timestamp ASC
      `;
      return res.status(200).json(messages);
    } catch (err) {
      console.error('Get messages error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  if (req.method === 'POST') {
    const { text } = req.body || {};
    if (!text) return res.status(400).json({ error: 'Texte requis' });

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

      // Mise à jour de la conversation
      await sql`
        UPDATE conversations
        SET last_message = ${text}, time = to_char(NOW(), 'HH24:MI'), unread_count = unread_count + 1
        WHERE id = ${conversationId}
      `;

      // LOGIQUE D'ACCEPTATION : Si l'admin envoie un message d'acceptation, on met à jour le dossier
      if (req.user.role === 'admin' && conversation.dossier_id && text.includes('accepte')) {
        await sql`
          UPDATE dossiers
          SET status = 'En cours', progress = 40.0
          WHERE id = ${conversation.dossier_id}
        `;
      }

      return res.status(201).json(newMessage[0]);
    } catch (err) {
      console.error('Send message error:', err);
      return res.status(500).json({ error: 'Erreur serveur' });
    }
  }

  return res.status(405).json({ error: 'Method not allowed' });
});

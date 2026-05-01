const guardAdmin = (req, res, next) => {
  if (req.user.role !== 'admin') {
    return res.status(403).json({ error: 'Accès refusé: rôle admin requis' });
  }
  next();
};

const guardOwner = (resourceField) => (req, res, next) => {
  if (req.user.role === 'admin') return next();
  if (req[resourceField] && req[resourceField].user_id === req.user.userId) return next();
  return res.status(403).json({ error: 'Accès refusé: ressource non appartenante' });
};

const guardConversationMember = (req, res, next) => {
  if (req.user.role === 'admin') return next();
  // Vérifier si l'utilisateur est membre de la conversation
  if (req.conversation && (req.conversation.user_id === req.user.userId || req.conversation.admin_id === req.user.userId)) {
    return next();
  }
  return res.status(403).json({ error: 'Accès refusé: pas membre de la conversation' });
};

module.exports = {
  guardAdmin,
  guardOwner,
  guardConversationMember
};

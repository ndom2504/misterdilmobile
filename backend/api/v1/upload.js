const { withAuth } = require('../lib/middleware');

// Simple in-memory storage for demo (in production, use AWS S3, Cloudinary, etc.)
const uploadedFiles = new Map();

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  try {
    // Vercel serverless doesn't support streaming multipart parsing easily
    // For now, accept base64 file data in JSON body
    const { fileName, fileData } = req.body || {};

    if (!fileName || !fileData) {
      return res.status(400).json({ error: 'fileName and fileData required' });
    }

    // Generate a unique ID for the file
    const fileId = `file_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    
    // Store file metadata (in production, store actual file in cloud storage)
    uploadedFiles.set(fileId, {
      id: fileId,
      name: fileName,
      data: fileData,
      uploadedAt: new Date().toISOString()
    });

    // Return the file URL (in production, this would be a cloud storage URL)
    const fileUrl = `https://your-storage.com/files/${fileId}/${encodeURIComponent(fileName)}`;

    return res.status(200).json({ fileId, fileName, fileUrl });
  } catch (err) {
    console.error('File upload error:', err);
    return res.status(500).json({ error: 'Erreur serveur' });
  }
});

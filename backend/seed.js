require('dotenv').config();
const bcrypt = require('bcryptjs');
const { sql } = require('./lib/db');

async function seed() {
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

  console.log('✅ Admins créés / mis à jour :');
  result.forEach(r => console.log(`  [${r.id}] ${r.name} — ${r.email} — ${r.role}`));
  process.exit(0);
}

seed().catch(err => {
  console.error('❌ Erreur seed :', err);
  process.exit(1);
});

const Stripe = require('stripe');
const { withAuth } = require('../../../lib/middleware');

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);

module.exports = withAuth(async (req, res) => {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  const { amount, currency = 'cad' } = req.body;
  if (!amount || amount <= 0) {
    return res.status(400).json({ error: 'Montant invalide' });
  }

  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount,
      currency,
      automatic_payment_methods: { enabled: true },
    });

    return res.status(200).json({
      clientSecret: paymentIntent.client_secret,
      publishableKey: process.env.STRIPE_PUBLISHABLE_KEY,
      customerId: null,
      ephemeralKeySecret: null,
    });
  } catch (err) {
    console.error('Payment intent error:', err);
    return res.status(500).json({ error: err.message });
  }
});

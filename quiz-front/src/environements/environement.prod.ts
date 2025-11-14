export const environment = {
  production: true,
  gatewayUrl: 'https://qlture.dewal.fr',
  // Utiliser les préfixes qui seront capturés par les routes du Gateway
  quizApi: '/api/quiz',   // Le Gateway mappera '/api/quiz/**' vers QUIZ-SERVICE
  userApi: '/api/user',   // Le Gateway mappera '/api/user/**' vers USER-SERVICE
  rewardApi: '/api/score', // Le Gateway mappera '/api/score/**' vers REWARD-SERVICE
};

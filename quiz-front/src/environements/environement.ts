export const environment = {
  production: false,
  gatewayUrl: 'http://localhost:9001',
  quizApi: '/api/quiz',   // Le Gateway mappera '/api/quiz/**' vers QUIZ-SERVICE
  userApi: '/api/user',   // Le Gateway mappera '/api/user/**' vers USER-SERVICE
  rewardApi: '/api/score', // Le Gateway mappera '/api/score/**' vers REWARD-SERVICE
};

export const environment = {
  production: true,
  gatewayUrl: '',
  quizApi: '/api/quiz',   // Le Gateway mappera '/api/quiz/**' vers QUIZ-SERVICE
  userApi: '/api/user',   // Le Gateway mappera '/api/user/**' vers USER-SERVICE
  rewardApi: '/api/score', // Le Gateway mappera '/api/score/**' vers REWARD-SERVICE

};

console.log('*** ENVIRONMENT PROD: https://quizcult.dewal.fr/ est actif ***');

module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true
  },
  parser: '@typescript-eslint/parser',
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:lit/recommended',
    'plugin:prettier/recommended'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  plugins: ['lit', '@typescript-eslint', 'prettier'],
  rules: {
    'prettier/prettier': 'error'
  }
};

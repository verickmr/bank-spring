const TOKEN_KEY = 'conta-segura:token'

export const tokenStorage = {
  get: () => localStorage.getItem(TOKEN_KEY),
  save: (token: string) => localStorage.setItem(TOKEN_KEY, token),
  remove: () => localStorage.removeItem(TOKEN_KEY),
}

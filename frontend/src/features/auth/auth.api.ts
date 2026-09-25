import { request } from '../../shared/api/httpClient'
import type { LoginCredentials, TokenResponse } from './auth.types'

export function authenticate(credentials: LoginCredentials) {
  return request<TokenResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  })
}

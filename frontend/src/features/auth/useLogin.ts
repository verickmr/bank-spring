import { useMutation } from '@tanstack/react-query'
import { HttpError } from '../../shared/api/httpClient'
import { tokenStorage } from '../../shared/storage/tokenStorage'
import { authenticate } from './auth.api'
import type { LoginCredentials, TokenResponse } from './auth.types'

export function useLogin() {
  return useMutation<TokenResponse, HttpError, LoginCredentials>({
    mutationFn: authenticate,
    onSuccess: ({ token }) => tokenStorage.save(token),
  })
}

import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { HttpError } from '../../shared/api/httpClient'
import { createAccount } from './accounts.api'
import type { AccountCreateFormData } from './accounts.schema'
import type { CreateAccountResponse } from './accounts.types'

export function useCreateAccount() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  return useMutation<CreateAccountResponse, HttpError, AccountCreateFormData>({
    mutationFn: ({ tipo, ...payload }) => createAccount(tipo, payload),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['accounts'] }),
        queryClient.invalidateQueries({ queryKey: ['customers'] }),
      ])
      navigate('/', {
        replace: true,
        state: { accountCreated: true },
      })
    },
  })
}

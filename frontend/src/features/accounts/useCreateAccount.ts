import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { HttpError } from '../../shared/api/httpClient'
import { createAccount } from './accounts.api'
import type { AccountCreateFormData } from './accounts.schema'
import type { CreateAccountResponse } from './accounts.types'

export function useCreateAccount(customerId: number) {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  return useMutation<CreateAccountResponse, HttpError, AccountCreateFormData>({
    mutationFn: ({ tipo, ...payload }) => createAccount(tipo, {
      ...payload,
      correntistaId: customerId,
    }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['accounts'] })
      navigate('/', {
        replace: true,
        state: { accountCreated: true },
      })
    },
  })
}

import { useMutation, useQueryClient } from '@tanstack/react-query'
import { HttpError } from '../../shared/api/httpClient'
import type { Transaction } from '../transactions/transactions.types'
import { executeAccountOperation } from './accounts.api'
import type { AccountOperationFormData } from './accountOperation.schema'
import type { Account } from './accounts.types'

export function useAccountOperation(account: Account) {
  const queryClient = useQueryClient()

  return useMutation<Transaction, HttpError, AccountOperationFormData>({
    mutationFn: ({ operacao, valor }) => executeAccountOperation(
      account,
      operacao,
      { valor },
    ),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['accounts'] }),
        queryClient.invalidateQueries({ queryKey: ['transactions', account.id] }),
        queryClient.invalidateQueries({ queryKey: ['customers'] }),
      ])
    },
  })
}

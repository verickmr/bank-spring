import { useMutation, useQueryClient } from '@tanstack/react-query'
import { HttpError } from '../../shared/api/httpClient'
import type { Transaction } from '../transactions/transactions.types'
import { applyAccountRate } from './accounts.api'
import type { AccountRateFormData } from './accountRate.schema'
import type { Account } from './accounts.types'

export function useAccountRate(account: Account) {
  const queryClient = useQueryClient()

  return useMutation<Transaction, HttpError, AccountRateFormData>({
    mutationFn: ({ percentual }) => applyAccountRate(account, {
      taxa: percentual / 100,
    }),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['accounts'] }),
        queryClient.invalidateQueries({ queryKey: ['transactions', account.id] }),
      ])
    },
  })
}

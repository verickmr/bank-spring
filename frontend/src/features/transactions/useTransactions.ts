import { useQuery } from '@tanstack/react-query'
import { HttpError } from '../../shared/api/httpClient'
import { listTransactionsByAccount } from './transactions.api'
import type { Transaction } from './transactions.types'

export function useTransactions(accountId: number, enabled: boolean) {
  return useQuery<Transaction[], HttpError>({
    queryKey: ['transactions', accountId],
    queryFn: () => listTransactionsByAccount(accountId),
    enabled,
  })
}

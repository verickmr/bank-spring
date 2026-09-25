import { useQuery } from '@tanstack/react-query'
import { HttpError } from '../../shared/api/httpClient'
import { listAccounts } from './accounts.api'
import type { Account } from './accounts.types'

export function useAccounts() {
  return useQuery<Account[], HttpError>({
    queryKey: ['accounts'],
    queryFn: listAccounts,
  })
}

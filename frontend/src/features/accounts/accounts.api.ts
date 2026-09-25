import { request } from '../../shared/api/httpClient'
import type { Account } from './accounts.types'

export function listAccounts() {
  return request<Account[]>('/api/contas')
}

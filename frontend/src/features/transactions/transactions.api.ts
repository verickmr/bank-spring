import { request } from '../../shared/api/httpClient'
import type { Transaction } from './transactions.types'

export function listTransactionsByAccount(accountId: number) {
  return request<Transaction[]>(`/api/transacoes/conta/${accountId}`)
}

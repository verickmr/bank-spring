import { request } from '../../shared/api/httpClient'
import type {
  Account,
  AccountCreateType,
  CreateAccountPayload,
  CreateAccountResponse,
} from './accounts.types'

export function listAccounts() {
  return request<Account[]>('/api/contas')
}

export function createAccount(type: AccountCreateType, payload: CreateAccountPayload) {
  return request<CreateAccountResponse>(`/api/contas/${type}`, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

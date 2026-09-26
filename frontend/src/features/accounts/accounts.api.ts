import { request } from '../../shared/api/httpClient'
import type { Transaction } from '../transactions/transactions.types'
import type {
  Account,
  AccountCreateType,
  AccountOperation,
  AccountOperationPayload,
  AccountRatePayload,
  CreateAccountPayload,
  CreateAccountResponse,
} from './accounts.types'

function getAccountPathType(account: Account) {
  return account.tipo === 'ContaCorrente' ? 'corrente' : 'poupanca'
}

export function listAccounts() {
  return request<Account[]>('/api/contas')
}

export function createAccount(type: AccountCreateType, payload: CreateAccountPayload) {
  return request<CreateAccountResponse>(`/api/contas/${type}`, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function executeAccountOperation(
  account: Account,
  operation: AccountOperation,
  payload: AccountOperationPayload,
) {
  return request<Transaction>(
    `/api/contas/${getAccountPathType(account)}/${account.id}/${operation}`,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
  )
}

export function applyAccountRate(account: Account, payload: AccountRatePayload) {
  return request<Transaction>(
    `/api/contas/${getAccountPathType(account)}/${account.id}/taxa`,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
  )
}

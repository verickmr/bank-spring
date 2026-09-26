export type AccountType = 'ContaCorrente' | 'ContaPoupanca'
export type AccountCreateType = 'corrente' | 'poupanca'

export type Account = {
  id: number
  numero: string
  saldo: number
  tipo: AccountType
}

export type CreateAccountPayload = {
  numero: string
  correntistaId: number
  limite?: number
}

export type CreateAccountResponse = {
  id: number
  numero: string
  saldo: number
}

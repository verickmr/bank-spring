import type { Account } from '../accounts/accounts.types'

export type Customer = {
  id: number
  cpf: string
  nome: string
  email: string
  contas: Account[]
}

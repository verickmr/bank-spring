export type TransactionType = 'DEPOSITO' | 'SAQUE' | 'RENDIMENTO' | 'JUROS'

export type Transaction = {
  id: number
  tipo: TransactionType
  valor: number
  data: string
  descricao: string
}

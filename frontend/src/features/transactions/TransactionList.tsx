import { Alert, AlertDescription } from '@/components/ui/alert'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import type { Transaction, TransactionType } from './transactions.types'

const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

const dateFormatter = new Intl.DateTimeFormat('pt-BR', {
  dateStyle: 'short',
  timeStyle: 'short',
})

const transactionLabels: Record<TransactionType, string> = {
  DEPOSITO: 'Depósito',
  SAQUE: 'Saque',
  RENDIMENTO: 'Rendimento',
  JUROS: 'Juros',
}

const creditTypes = new Set<TransactionType>(['DEPOSITO', 'RENDIMENTO'])

function TransactionRow({ transaction }: { transaction: Transaction }) {
  const isCredit = creditTypes.has(transaction.tipo)
  const signedValue = isCredit ? transaction.valor : -transaction.valor

  return (
    <li className="flex flex-col gap-2 border-b py-4 last:border-b-0 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <p className="font-medium">{transactionLabels[transaction.tipo]}</p>
        <p className="text-sm text-muted-foreground">{transaction.descricao}</p>
        <time className="text-xs text-muted-foreground" dateTime={transaction.data}>
          {dateFormatter.format(new Date(transaction.data))}
        </time>
      </div>
      <p className={isCredit ? 'font-semibold text-primary' : 'font-semibold text-foreground'}>
        {isCredit ? '+' : '−'} {currencyFormatter.format(Math.abs(signedValue))}
      </p>
    </li>
  )
}

type TransactionListProps = {
  transactions?: Transaction[]
  isPending: boolean
  errorMessage?: string
}

export function TransactionList({
  transactions,
  isPending,
  errorMessage,
}: TransactionListProps) {
  if (isPending) {
    return <div className="h-64 animate-pulse rounded-xl border bg-card" aria-label="Carregando extrato" />
  }

  if (errorMessage) {
    return (
      <Alert variant="destructive">
        <AlertDescription>{errorMessage}</AlertDescription>
      </Alert>
    )
  }

  if (!transactions?.length) {
    return (
      <Card className="border-dashed">
        <CardHeader>
          <CardTitle className="text-base">Nenhuma movimentação</CardTitle>
          <CardDescription>As operações desta conta aparecerão aqui.</CardDescription>
        </CardHeader>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Movimentações</CardTitle>
        <CardDescription>{transactions.length} transação(ões) encontrada(s)</CardDescription>
      </CardHeader>
      <CardContent>
        <ul>
          {transactions.map((transaction) => (
            <TransactionRow key={transaction.id} transaction={transaction} />
          ))}
        </ul>
      </CardContent>
    </Card>
  )
}
